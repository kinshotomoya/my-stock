package repository

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import cats.data.OptionT
import com.google.inject.{Inject, Singleton}
import com.jimmoores.quandl.classic.ClassicQuandlSession
import com.jimmoores.quandl.{DataSetRequest, Frequency, TabularResult}
import cats.implicits._
import domain.TimeOutError
import domain.model.{QuandlResult, StockCode}
import domain.repository.StockRepository
import exts.QuandlImplicits._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepositoryImpl @Inject()(actorSystem: ActorSystem) extends StockRepository {
  // セッションはプロセス毎に１つだけ作って、それを使い回す
  // TODO: akka-actorを使うなら、各actorごとにセッション持つべきなので、
  // actorを作る際に、セッションも作ってimplicitで依存しているモジュールに渡す設計にする
  // 参考：https://qiita.com/negokaz/items/ba065abcb5ee40c150a9
  private val session: ClassicQuandlSession = ClassicQuandlSession.create()

  override def getStock(code: StockCode, frequency: Frequency)(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): OptionT[Future, QuandlResult] = {
    val future = Future {
      val request: DataSetRequest = requestBuilder.build(code, frequency)
      session.getDataSet(request)
    }

    OptionT.liftF{
      Future.firstCompletedOf(Seq(delayed, future)).map(_.convertToDomainObject)
    }.recoverWith{
      case _ => OptionT.fromOption[Future](None)
    }
  }

  override def getStocks(codes: List[StockCode], frequency: Frequency)(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): Future[List[QuandlResult]] = {
    // Seq[Future[TabularResult]] -> Future[Seq[TabularResult]]
    val future: Future[List[TabularResult]] = codes.map { code =>
      Future {
        val request: DataSetRequest = requestBuilder.build(code, frequency)
        session.getDataSet(request)
      }
    }.sequence


    Future.firstCompletedOf(Seq(delayed, future)).map(_.map(_.convertToDomainObject))
      .recoverWith{
      case _ => Future.successful(Nil)
    }
  }

  private def delayed(implicit ec: ExecutionContext) = akka.pattern.after(FiniteDuration(5000L, TimeUnit.MILLISECONDS), actorSystem.scheduler)(Future.failed(TimeOutError("timeout error")))
}
