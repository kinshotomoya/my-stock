package domain.repository.api

import domain.model.StockCode

import scala.concurrent.Future

trait YahooFinanceApiRepository {
  def getNews(stockCode: List[StockCode]): Future[Nothing]

  def getStockInfo(stockCode: StockCode): Future[Nothing]

  def getStockDetailInfo(stockCode: StockCode): Future[Nothing]
}
