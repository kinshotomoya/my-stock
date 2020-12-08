package freeMonad

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import freeMonad.actions.Actions
import freeMonad.domains.{SearchRequest, StockCode}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.Future

object Main {
  implicit val actorSystem: ActorSystem = ActorSystem("my-system")
  implicit val ec: MessageDispatcher =
    actorSystem.dispatchers.lookup("request-response-executor")

  implicit val stockCodeFormat: RootJsonFormat[StockCode] =
    jsonFormat(StockCode.apply, "value")

  implicit val searchRequestFormat: RootJsonFormat[SearchRequest] = jsonFormat1(
    SearchRequest
  )

  val stockUseCase: StockUseCase[Future] = StockUseCase()

  val routes = concat(get {
    path("searchStocks") {
      entity(as[SearchRequest]) { request: SearchRequest =>
        {
          onSuccess(stockUseCase.run(Actions.searchStocks(request))) { _ =>
            complete(HttpResponse(StatusCodes.OK))
          }
        }
      }
    }
  })

  Http().newServerAt("localhost", 8000)
}
