package repository.api

import domain.model.StockCode
import domain.repository.api.YahooFinanceApiRepository

import scala.concurrent.Future


// TODO: rakuten.rapid.apiを叩く
// 続き！！！！！！！！！！
// TODO: 自分で、rapid-api経由でYahoo apiを叩くclient libraryを作る！
class YahooFinanceApiRepositoryImpl extends YahooFinanceApiRepository{
  override def getNews(stockCode: List[StockCode]): Future[Nothing] = ???

  override def getStockInfo(stockCode: StockCode): Future[Nothing] = ???

  override def getStockDetailInfo(stockCode: StockCode): Future[Nothing] = ???
}
