import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.jimmoores.quandl.Frequency
import domain.model.StockCode
import repository.StockRepositoryImpl

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn


object Route{

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val ec: ExecutionContextExecutor = system.dispatchers.lookup("my-fork-join-executor")
    val stockRepository = new StockRepositoryImpl(actorSystem = system)


    // TODO: JSONを返せるようにする
    val route = path("hello") {
      stockRepository.getStock(StockCode("MULTPL/SP500_REAL_PRICE_MONTH"), Frequency.ANNUAL)
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>hello world</h1>" ))
      }
    }

    val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8000).bind(route)

    println("listen service at http://localhost:8000")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }

}
