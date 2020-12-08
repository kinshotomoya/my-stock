package freeMonad.actions

import cats.free.Free
import freeMonad.Validators.StockValidator
import freeMonad.domains.{
  RequestError,
  RequestErrors,
  SearchRequest,
  SearchResponse
}

// 1. まず、ASTを作成する
// 株式を取得するためのSearch Actionをまずは定義
object Actions {
  sealed trait Actions[A]
  case class Search(request: SearchRequest)
      extends Actions[Either[RequestError, SearchResponse]]

  type Program[A] = Free[Actions, A]
  type Result[A] = Either[RequestError, A]

  // 2. 次はDSLを定義
  // とりあえず、Free Monadに変換する
  private def execute[A](action: Actions[A]): Free[Actions, A] =
    Free.liftF[Actions, A](action)

  // executeメソッドで、より抽象化している
  // search以外にも、delete, updateなどもある想定で
  private def search(request: SearchRequest): Program[Result[SearchResponse]] =
    Free.liftF[Actions, Result[SearchResponse]](Search(request))

  // 3. 次は、ロジックを作成
  // ここが重要！
  // 副作用を含んでいないので、テストめっちゃしやすくなっている
  // DBとかをモックする必要がない！
  def searchStocks(
    searchRequest: SearchRequest
  ): Program[Result[SearchResponse]] = {
    StockValidator
      .validateSearchRequest(searchRequest)
      .fold(
        errors =>
          Free.pure[Actions, Result[SearchResponse]](
            Left(RequestErrors(errors.toNonEmptyList.toList.mkString(",")))
        ),
        request => execute(Search(request))
      )
  }
}
