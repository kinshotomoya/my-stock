import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.data.{NonEmptyChain, OptionT, Validated, ValidatedNec}
import cats.kernel.Semigroup
import com.jimmoores.quandl.Frequency
import domain.model.{QuandlResult, StockCode}
import presentation.RequestCondition
import repository.StockRepositoryImpl
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import cats._


object Routing extends RoutingBase {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatchers.lookup("my-fork-join-executor")
    val stockRepository = new StockRepositoryImpl(actorSystem = system)

    // TODO: 多くなってきたら別ファイルに移す

    implicit val resultFormat: RootJsonFormat[QuandlResult] = jsonFormat1(QuandlResult)
    implicit val stockCodeFormat: RootJsonFormat[StockCode] = jsonFormat(StockCode.apply, "value")
    implicit val conditionFormat: RootJsonFormat[RequestCondition] = jsonFormat1(RequestCondition)

    val route: Route =
      concat(
        get{
          path("init") {
            complete(HttpResponse(StatusCodes.OK))
          }
        },
        post {
          path("searchStocks") {
            entity(as[RequestCondition]) {condition =>
              Validator.validateRequestCondition(condition).fold(
                e => complete(e.map(v => v.validationMessage).toChain.toList),
                condition => {
                  // 続き！！！
                  // TODO: 並行してstock apiを叩く処理追加
                  // TODO: ２つのresponseをOptionTで合成
                  val stockInfo: OptionT[Future, QuandlResult] = stockRepository.getStock(StockCode("MULTPL/SP500_REAL_PRICE_MONTH"), Frequency.ANNUAL)
                  onSuccess(stockInfo.value) {
                    case Some(value) => complete(value)
                    case None => complete(HttpResponse(StatusCodes.InternalServerError))
                  }
                }
              )
            }
          }
        }
      )

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8000).bind(route)

    println("listen service at http://localhost:8000")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }

}
