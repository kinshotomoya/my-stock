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
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import spray.json.DefaultJsonProtocol._


object Routing{

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatchers.lookup("my-fork-join-executor")
    val stockRepository = new StockRepositoryImpl(actorSystem = system)

    // TODO: 多くなってきたら別ファイルに移す
    implicit val resultFormat = jsonFormat1(QuandlResult)

    // 参考:https://doc.akka.io/docs/akka-http/current/routing-dsl/exception-handling.html
    implicit def exceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case e: Exception => extractUri{uri =>
          println(e.getMessage) // TODO: ログの設定行う
          complete(HttpResponse(StatusCodes.InternalServerError))
        }
      }

    // TODO: リクエストに指標（sp500など）を指定できるようにし、catsのvalidatedを使ってバリデーションかける
    val route: Route = path("hello") {
      // TODO: 並行してstock apiを叩く処理ついか
      // TODO: ２つのresponseをOptionTで合成
      val stockInfo: OptionT[Future, QuandlResult] = stockRepository.getStock(StockCode("MULTPL/SP500_REAL_PRICE_MONTH"), Frequency.ANNUAL)
      onSuccess(stockInfo.value) {
        case Some(value) => complete(value)
        case None => complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8000).bind(route)

    println("listen service at http://localhost:8000")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }

}
