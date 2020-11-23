package domain.repository.mysql

import domain.model.StockCode

import scala.concurrent.Future

trait StockRepository {

  def getStockCodesBy(accountId: Long): Future[List[StockCode]]

}
