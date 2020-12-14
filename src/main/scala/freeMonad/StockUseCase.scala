package freeMonad

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import cats.~>
import freeMonad.actions.Actions
import freeMonad.actions.Actions.{Actions, GetNews, Program, Search}
import freeMonad.domains.{RequestErrors, SearchRequest, SearchResponse, Stock}

import scala.concurrent.Future

trait StockUseCase[F[_]] {
  def run[A](program: Actions.Program[A]): F[A]
}

// 4. interpreterを作成
object StockUseCase {
  private def searchStock(request: SearchRequest,
                          stockRepository: StockRepository)(
    implicit useCaseExecutor: MessageDispatcher
  ): Future[Either[RequestErrors, SearchResponse]] = {
    println(s"${request.stockCode}でstockを検索しています・・・")
    stockRepository.fetchStock(request.stockCode) map {
      case Some(stock: Stock) => Right(SearchResponse(stock))
      case None               => Left(RequestErrors("該当のstockは存在しません。"))
    }
  }

  private def getNews(stock: Stock)(
    implicit useCaseExecutor: MessageDispatcher
  ): Future[Either[RequestErrors, SearchResponse]] = {
    // TODO: repositoryのメソッドを呼ぶ
    Future(Right(SearchResponse(stock)))
  }

  private def interpreter(
    stockRepository: StockRepository
  )(implicit useCaseExecutor: MessageDispatcher): Actions ~> Future = {

    new (Actions ~> Future) {
      override def apply[A](fa: Actions[A]): Future[A] = {
        fa match {
          case Search(request: SearchRequest) =>
            searchStock(request, stockRepository)
          case GetNews(stock: Stock) => getNews(stock)
        }
      }
    }
  }

  def apply(
    stockRepository: StockRepository
  )(implicit actorSystem: ActorSystem): StockUseCase[Future] = {
    // useCaseレイヤーのExecutionContextを作成
    implicit val ec: MessageDispatcher =
      actorSystem.dispatchers.lookup("quandle-api-executor")

    new StockUseCase[Future] {
      override def run[A](program: Program[A]): Future[A] = {
        // Future Monadを作るために必要
        // foldMapの第二引数で指定されている
        // Future[_]を作るために必要なメソッドが定義されている
        import cats.instances.future._
        program.foldMap(interpreter(stockRepository))
      }
    }
  }
}
