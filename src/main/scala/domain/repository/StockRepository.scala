package domain.repository


import com.jimmoores.quandl.{DataSetRequest, Frequency}
import domain.model.{QuandlResult, StockCode}
import repository.Builder

import scala.concurrent.{ExecutionContext, Future}

trait StockRepository {
  def getStock(code: StockCode, frequency: Frequency)(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): Future[Option[QuandlResult]]
}
