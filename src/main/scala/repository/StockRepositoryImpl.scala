package repository

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import com.jimmoores.quandl.classic.ClassicQuandlSession
import com.jimmoores.quandl.{DataSetRequest, Frequency, TabularResult}
import domain.TimeOutError
import domain.model.QuandlResult
import domain.repository.StockRepository

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import exts.QuandlImplicits._

@Singleton
class StockRepositoryImpl @Inject()(actorSystem: ActorSystem) extends StockRepository {
  override def getStock(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): Future[Option[QuandlResult]] = {
    // ・TabularResultをドメインオブジェクトに変換

    val delayed = akka.pattern.after(FiniteDuration(500L, TimeUnit.MILLISECONDS), actorSystem.scheduler)(Future.failed(TimeOutError("timeout error")))

    val result = Future {
      val session: ClassicQuandlSession = ClassicQuandlSession.create()
      val request: DataSetRequest = requestBuilder.build("MULTPL/SP500_REAL_PRICE_MONTH", Frequency.ANNUAL)
      session.getDataSet(request)
    }

    Future.firstCompletedOf(Seq(delayed, result)).map(result => Some(result.convertToDomainObject)).recoverWith{
      case _ => Future(None)
    }
  }
}
