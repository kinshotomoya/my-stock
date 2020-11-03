package domain.repository

import com.google.inject.ImplementedBy
import repository.StockRepositoryImpl

import scala.concurrent.Future

@ImplementedBy(classOf[StockRepositoryImpl])
trait StockRepository {
  def getStock[A]: Future[A]
}
