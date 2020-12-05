package defaults.domain.repository.api

import defaults.domain.model.StockCode

import scala.concurrent.Future

trait YahooFinanceApiRepository {
  def getNews(stockCode: List[StockCode]): Future[Nothing]

  def getStockInfo(stockCode: StockCode): Future[Nothing]

  def getStockDetailInfo(stockCode: StockCode): Future[Nothing]
}
