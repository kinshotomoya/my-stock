package freeMonad

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{
  as,
  complete,
  concat,
  entity,
  get,
  post,
  onSuccess,
  path
}
import akka.http.scaladsl.server.Route
import freeMonad.actions.Actions
import freeMonad.domains.{SearchRequest, StockCode}
import spray.json.DefaultJsonProtocol.{jsonFormat, _}
import spray.json.RootJsonFormat

import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("my-system")
    implicit val ec: MessageDispatcher =
      actorSystem.dispatchers.lookup("request-response-executor")

    implicit val stockCodeFormat: RootJsonFormat[StockCode] =
      jsonFormat(StockCode.apply, "value")

    implicit val searchRequestFormat: RootJsonFormat[SearchRequest] =
      jsonFormat(SearchRequest, "code")

    val stockUseCase: StockUseCase[Future] = StockUseCase(new StockRepository)

    val routes: Route = concat(
      // curl -X POST http://localhost:8000/searchStocks  -H "Content-Type: application/json" -d '{"code": {"value": "ss"}}'
      post {
        path("searchStocks") {
          entity(as[SearchRequest]) { request: SearchRequest =>
            {
              onSuccess(stockUseCase.run(Actions.searchStocks(request))) {
                res =>
                  complete(HttpResponse(StatusCodes.OK))
              }
            }
          }
        }
      },
      get {
        path("init") {
          complete(HttpResponse(StatusCodes.OK))
        }
      }
    )

    println("listen service at http://localhost:8000")
    Http().newServerAt("localhost", 8000).bind(routes)
  }
}
