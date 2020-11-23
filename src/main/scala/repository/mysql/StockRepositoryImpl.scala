package repository.mysql

import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import domain.repository.mysql.StockRepository

import scala.concurrent.ExecutionContextExecutor

@Singleton
class StockRepositoryImpl @Inject()(actorSystem: ActorSystem) extends StockRepository {

  implicit val ec: ExecutionContextExecutor = actorSystem.dispatchers.lookup("mysql-executor")

}
