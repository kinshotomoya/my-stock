package repository

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import cats.data.OptionT
import com.google.inject.{Inject, Singleton}
import com.jimmoores.quandl.classic.ClassicQuandlSession
import com.jimmoores.quandl.{DataSetRequest, Frequency}
import cats.implicits._
import domain.TimeOutError
import domain.model.{QuandlResult, StockCode}
import domain.repository.StockRepository
import exts.QuandlImplicits._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepositoryImpl @Inject()(actorSystem: ActorSystem) extends StockRepository {
  override def getStock(code: StockCode, frequency: Frequency)(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): OptionT[Future, QuandlResult] = {
    val delayed = akka.pattern.after(FiniteDuration(500L, TimeUnit.MILLISECONDS), actorSystem.scheduler)(Future.failed(TimeOutError("timeout error")))

    val future = Future {
      val session: ClassicQuandlSession = ClassicQuandlSession.create()
      val request: DataSetRequest = requestBuilder.build(code, frequency)
      session.getDataSet(request)
    }

    OptionT.liftF{
      Future.firstCompletedOf(Seq(delayed, future)).map(_.convertToDomainObject)
    }.recoverWith{
      case _ => OptionT.fromOption[Future](None)
    }
  }
}
