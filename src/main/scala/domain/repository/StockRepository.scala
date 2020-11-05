package domain.repository


import com.jimmoores.quandl.{DataSetRequest, TabularResult}
import repository.Builder

import scala.concurrent.{ExecutionContext, Future}

trait StockRepository {
  def getStock(implicit ec: ExecutionContext, requestBuilder: Builder[DataSetRequest]): Future[Option[TabularResult]]
}
