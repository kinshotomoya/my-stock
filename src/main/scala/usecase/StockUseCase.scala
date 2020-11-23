package usecase

import cats.data.OptionT
import com.google.inject.Inject
import com.jimmoores.quandl.Frequency
import domain.model.{QuandlResult, StockCode}
import repository.api.QuandleApiRepositoryImpl

import scala.concurrent.Future

class StockUseCase @Inject()(stockRepository: QuandleApiRepositoryImpl){

  def getStocksBy(stockCodes: List[StockCode]): Future[List[QuandlResult]] = {
    val stockList: Future[List[QuandlResult]] = stockRepository.getStocks(stockCodes, Frequency.ANNUAL)
    stockList
  }

  def getStockBy(stockCode: StockCode): OptionT[Future, QuandlResult] = {
    val stockList: OptionT[Future, QuandlResult] = stockRepository.getStock(stockCode, Frequency.ANNUAL)
    stockList
  }

}
