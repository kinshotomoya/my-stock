package defaults.repository.api

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import cats.data.{Nested, OptionT}
import cats.implicits._
import com.google.inject.{Inject, Singleton}
import com.jimmoores.quandl.classic.ClassicQuandlSession
import com.jimmoores.quandl.{DataSetRequest, Frequency, TabularResult}
import defaults.domain.TimeOutError
import defaults.domain.model.{QuandlResult, StockCode}
import defaults.domain.repository.api.QuandleApiRepository
import defaults.exts.QuandlImplicits._
import defaults.repository.Builder

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

@Singleton
class QuandleApiRepositoryImpl @Inject()(actorSystem: ActorSystem)
    extends QuandleApiRepository {
  // 現状ではセッションはプロセス毎に１つだけ作って、それを使い回すようにしているが↓
  // TODO: akka-actorを使うなら、各actorごとにセッション持つべきなので、
  // actorを作る際に、セッションも作ってimplicitで依存しているモジュールに渡す設計にする
  // 参考：https://qiita.com/negokaz/items/ba065abcb5ee40c150a9
  private val session: ClassicQuandlSession = ClassicQuandlSession.create()
  // quandle-apiを叩く用のスレッドプールを作成
  implicit val ec: ExecutionContextExecutor =
    actorSystem.dispatchers.lookup("quandle-api-executor")

  override def getStock(code: StockCode, frequency: Frequency)(
    implicit requestBuilder: Builder[DataSetRequest]
  ): OptionT[Future, QuandlResult] = {
    val future = Future {
      val request: DataSetRequest = requestBuilder.build(code, frequency)
      session.getDataSet(request)
    }

    OptionT
      .liftF {
        Future
          .firstCompletedOf(Seq(delayed, future))
          .map(_.convertToDomainObject)
      }
      .recoverWith {
        case _ => OptionT.fromOption[Future](None)
      }
  }

  override def getStocks(codes: List[StockCode],
                         frequency: Frequency = Frequency.ANNUAL)(
    implicit requestBuilder: Builder[DataSetRequest]
  ): Future[List[QuandlResult]] = {
    // Seq[Future[TabularResult]] -> Future[Seq[TabularResult]]
    val future: Future[List[TabularResult]] = codes.map { code =>
      Future {
        val request: DataSetRequest = requestBuilder.build(code, frequency)
        session.getDataSet(request)
      }
    }.sequence

    Future
      .firstCompletedOf(
        Seq(delayed, Nested(future).map(_.convertToDomainObject).value)
      )
      .recoverWith {
        case _ => Future.successful(Nil)
      }
  }

  private def delayed(implicit ec: ExecutionContext): Future[Nothing] =
    akka.pattern.after(
      FiniteDuration(5000L, TimeUnit.MILLISECONDS),
      actorSystem.scheduler
    )(Future.failed(TimeOutError("timeout error")))

}
