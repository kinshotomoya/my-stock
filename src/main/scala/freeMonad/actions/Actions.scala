package freeMonad.actions

import akka.dispatch.MessageDispatcher
import cats.free.Free
import cats.~>
import freeMonad.Main.actorSystem
import freeMonad.Validators.StockValidator
import freeMonad.actions.Actions.{Actions, Search}
import freeMonad.domains.{
  RequestError,
  RequestErrors,
  SearchRequest,
  SearchResponse
}

import scala.concurrent.Future

// 1. まず、ASTを作成する
// 株式を取得するためのSearch Actionをまずは定義
object Actions {
  sealed trait Actions[A]
  case class Search(request: SearchRequest)
      extends Actions[Either[RequestError, SearchResponse]]

  type Program[A] = Free[Actions, A]

  // 2. 次はDSLを定義
  // とりあえず、Free Monadに変換する
  private def execute[A](action: Actions[A]): Free[Actions, A] =
    Free.liftF[Actions, A](action)

  // exeuteメソッドで、より抽象化している
  // search以外にも、delete, updateなどもある想定で
  private def search(
    request: SearchRequest
  ): Program[Either[RequestError, SearchResponse]] =
    Free.liftF[Actions, Either[RequestError, SearchResponse]](Search(request))

  // 3. 次は、ロジックを作成
  // ここが重要！
  // 副作用を含んでいないので、テストめっちゃしやすくなっている
  // DBとかをモックする必要がない！
  def searchStocks(
    searchRequest: SearchRequest
  ): Program[Either[RequestError, SearchResponse]] = {
    StockValidator
      .validateSearchRequest(searchRequest)
      .fold(
        errors =>
          Free.pure[Actions, Either[RequestError, SearchResponse]](
            Left(RequestErrors(errors.toNonEmptyList.toList.mkString(",")))
        ),
        request => execute(Search(request))
      )
  }
}

// 4. interpreterを作成
// このオブジェクトをMainで呼び出せるようにする
object StockService {
  implicit val ec: MessageDispatcher =
    actorSystem.dispatchers.lookup("quandle-api-executor")

  // TODO: 実際にこれを実行できるようにする
  def interpreter: Actions ~> Future =
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
}
