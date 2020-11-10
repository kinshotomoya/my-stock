import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import cats.data.OptionT
import com.jimmoores.quandl.Frequency
import domain.model.{QuandlResult, StockCode}
import repository.StockRepositoryImpl

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._


object Route{

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatchers.lookup("my-fork-join-executor")
    val stockRepository = new StockRepositoryImpl(actorSystem = system)

    implicit val resultFormat = jsonFormat1(QuandlResult)


    val route = path("hello") {
      val stockInfo: OptionT[Future, QuandlResult] = stockRepository.getStock(StockCode("MULTPL/SP500_REAL_PRICE_MONTH"), Frequency.ANNUAL)
      onSuccess(stockInfo.value) {
        case Some(value) => complete(value)
        case None => complete(StatusCodes.NotFound)
      }
    }

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8000).bind(route)

    println("listen service at http://localhost:8000")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }

}
