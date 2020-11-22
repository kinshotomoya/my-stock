package usecase

import cats.data.OptionT
import com.google.inject.Inject
import com.jimmoores.quandl.Frequency
import domain.model.{QuandlResult, StockCode}
import repository.StockRepositoryImpl

import scala.concurrent.{ExecutionContext, Future}

class StockUseCase @Inject()(stockRepository: StockRepositoryImpl){

  def getStocksBy(stockCodes: List[StockCode])(implicit ec: ExecutionContext): Future[List[QuandlResult]] = {
    val stockList: Future[List[QuandlResult]] = stockRepository.getStocks(stockCodes, Frequency.ANNUAL)
    stockList
  }

  def getStockBy(stockCode: StockCode)(implicit ec: ExecutionContext): OptionT[Future, QuandlResult] = {
    val stockList: OptionT[Future, QuandlResult] = stockRepository.getStock(stockCode, Frequency.ANNUAL)
    stockList
  }

}
