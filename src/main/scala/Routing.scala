import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import domain.model.{QuandlResult, StockCode}
import presentation.RequestCondition
import repository.api.{QuandleApiRepositoryImpl, YahooFinanceApiRepositoryImpl}
import repository.mysql.StockRepositoryImpl
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import usecase.StockUseCase

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn


object Routing extends RoutingBase {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatchers.lookup("request-response-executor")

    val quandleApiRepository = new QuandleApiRepositoryImpl(actorSystem = system)
    val stockRepository = new StockRepositoryImpl(system)
    val yahooFinanceApiRepositoryImpl = new YahooFinanceApiRepositoryImpl
    val stockUseCase = new StockUseCase(quandleApiRepository, yahooFinanceApiRepositoryImpl)
    val validator = new Validator(system = system, quandleApiRepository = quandleApiRepository)

    implicit val resultFormat: RootJsonFormat[QuandlResult] = jsonFormat1(QuandlResult)
    implicit val stockCodeFormat: RootJsonFormat[StockCode] = jsonFormat(StockCode.apply, "value")
    implicit val conditionFormat: RootJsonFormat[RequestCondition] = jsonFormat2(RequestCondition)

    val route: Route =
      concat(
        get{
          path("init") {
            println("compiling...")
            complete(HttpResponse(StatusCodes.OK))
          }
        },
        // Quandle APIをベースにしたエンドポイント
        post {
          path("searchStocks") {
            entity(as[RequestCondition]) {condition =>
              validator.validateRequestCondition(condition).fold(
                e => complete(e.map(v => v.validationMessage).toChain.toList),
                condition => {
                  val stockInfo: Future[List[QuandlResult]] = stockUseCase.getStocksBy(condition.stockCodes)
                  onSuccess(stockInfo) {
                    case l @ List(_) => complete(l)
                    case Nil => complete(StatusCodes.NotFound)
                  }
                }
              )
            }
          }
        }
        // Yahoo Finance APIをベースにしたエンドポイント

      )

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8000).bind(route)

    println("listen service at http://localhost:8000")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }

}
