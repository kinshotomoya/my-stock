package repository

import com.google.inject.Singleton
import com.jimmoores.quandl.{DataSetRequest, Frequency, TabularResult}
import com.jimmoores.quandl.classic.ClassicQuandlSession
import domain.repository.StockRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepositoryImpl extends StockRepository {
  override def getStock(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): Future[TabularResult] = {
    val result: Future[TabularResult] = Future {
      val session: ClassicQuandlSession = ClassicQuandlSession.create()
      val request: DataSetRequest = requestBuilder.build("MULTPL/SP500_REAL_PRICE_MONTH", Frequency.ANNUAL)
      session.getDataSet(request)
    }
    result
  }
}
