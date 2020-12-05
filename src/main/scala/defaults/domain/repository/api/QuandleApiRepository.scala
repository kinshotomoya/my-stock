package defaults.domain.repository.api

import cats.data.OptionT
import com.jimmoores.quandl.{DataSetRequest, Frequency}
import defaults.domain.model.{QuandlResult, StockCode}
import defaults.repository.Builder

import scala.concurrent.Future

trait QuandleApiRepository {
  def getStock(code: StockCode, frequency: Frequency)(
    implicit requestBuilder: Builder[DataSetRequest]
  ): OptionT[Future, QuandlResult]
  def getStocks(codes: List[StockCode], frequency: Frequency)(
    implicit requestBuilder: Builder[DataSetRequest]
  ): Future[List[QuandlResult]]
}
