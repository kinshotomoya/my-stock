package repository

import domain.repository.StockRepository

import scala.concurrent.Future

class StockRepositoryImpl extends StockRepository{
  override def getStock[A]: Future[A] = ???

}
