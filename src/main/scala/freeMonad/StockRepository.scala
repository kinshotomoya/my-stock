package freeMonad

import freeMonad.domains.{Stock, StockCode}
import akka.actor.ActorSystem

import scala.concurrent.Future

class StockRepository()(implicit val actorSystem: ActorSystem) {
  implicit val repositoryExecutor =
    actorSystem.dispatchers.lookup("quandle-api-executor")

  def fetchStock(stockCode: StockCode): Future[Option[Stock]] = {
    Future(Some(Stock()))
  }

  // TODO: def fetchNewsを作成
}
