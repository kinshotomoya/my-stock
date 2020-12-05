package defaults.repository.mysql

import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import defaults.domain.model.StockCode
import defaults.domain.repository.mysql.StockRepository

import scala.concurrent.{ExecutionContextExecutor, Future}

@Singleton
class StockRepositoryImpl @Inject()(actorSystem: ActorSystem)
    extends StockRepository {

  implicit val ec: ExecutionContextExecutor =
    actorSystem.dispatchers.lookup("mysql-executor")

  // TODO: 実際にDBから取得する
  override def getStockCodesBy(accountId: Long): Future[List[StockCode]] = {
    Future.successful(List(StockCode("S&P500"), StockCode("NASDAQ100")))
  }
}
