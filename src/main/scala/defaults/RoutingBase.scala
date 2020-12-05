package defaults

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler

trait RoutingBase {
  // 参考:https://doc.akka.io/docs/akka-http/current/routing-dsl/exception-handling.html
  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Exception =>
        extractUri { uri =>
          println(e.getMessage) // TODO: ログの設定行う
          complete(HttpResponse(StatusCodes.InternalServerError))
        }
    }
}
