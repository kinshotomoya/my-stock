package defaults.domain.repository.mysql

import defaults.domain.model.StockCode

import scala.concurrent.Future

trait StockRepository {

  def getStockCodesBy(accountId: Long): Future[List[StockCode]]

}
