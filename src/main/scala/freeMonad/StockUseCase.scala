package freeMonad

import akka.dispatch.MessageDispatcher
import cats.~>
import freeMonad.Main.actorSystem
import freeMonad.actions.Actions
import freeMonad.actions.Actions.{Actions, Program, Search}
import freeMonad.domains.{RequestError, SearchResponse}

import scala.concurrent.Future

trait StockUseCase[F[_]] {
  def run[A](program: Actions.Program[A]): F[A]
}

// 4. interpreterを作成
object StockUseCase {
  implicit val ec: MessageDispatcher =
    actorSystem.dispatchers.lookup("quandle-api-executor")

  // TODO: このメソッドは、ここで定義してもいいのか？もっといい場所があるはず！
  private def interpreter: Actions ~> Future =
    new (Actions ~> Future) {
      override def apply[A](fa: Actions[A]): Future[A] = {
        fa match {
          case Search(request) => {
            println(s"${request.stockCode}でstockを検索しています・・・")
            // TODO: 実際にrepositoryをインジェクトして、そっから値を返すようにする
            Future {
              val either: Either[RequestError, SearchResponse] =
                Right(SearchResponse())
              either
            }
          }
        }
      }
    }

  def apply(): StockUseCase[Future] = {
    new StockUseCase[Future] {
      override def run[A](program: Program[A]): Future[A] = {
        // Future Monadを作るために必要
        // foldMapの第二引数で指定されている
        // Future[_]を作るために必要なメソッドが定義されている
        import cats.instances.future._
        program.foldMap(interpreter)
      }
    }
  }
}
