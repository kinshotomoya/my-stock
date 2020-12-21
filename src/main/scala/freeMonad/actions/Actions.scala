package freeMonad.actions

import cats.data.EitherT
import cats.free.Free
import freeMonad.Validators.StockValidator
import freeMonad.domains.{
  RequestError,
  RequestErrors,
  SearchRequest,
  SearchResponse,
  Stock
}

// 1. まず、ADTを作成する
// 株式を取得するためのSearch Actionをまずは定義
object Actions {
  sealed trait Actions[A]
  case class Search(request: SearchRequest)
      extends Actions[Either[RequestError, SearchResponse]]
  case class GetNews(stock: Stock)
      extends Actions[Either[RequestError, SearchResponse]]

  type Program[A] = Free[Actions, A]
  type Result[A] = Either[RequestError, A]

  // 2. 次はDSLを定義
  // とりあえず、Free Monadに変換する
  private def execute[A](action: Actions[A]): Free[Actions, A] =
    Free.liftF[Actions, A](action)

  // executeメソッドで、より抽象化している
  // search以外にも、delete, updateなどもある想定で
  // TODO: Program[Result[Stock]]を返すようにする
  private def search(request: SearchRequest): Program[Result[SearchResponse]] =
    Free.liftF[Actions, Result[SearchResponse]](Search(request))

  // TODO: Program[Result[News]]を返すようにする
  private def getNews(stock: Stock): Program[Result[SearchResponse]] =
    Free.liftF[Actions, Result[SearchResponse]](GetNews(stock))

  // 3. 次は、ロジックを作成
  // ここが重要！
  // 副作用を含んでいないので、テストめっちゃしやすくなっている
  // DBとかをモックする必要がない！
  // TODO: StockとNewsを組み合わせて、SearchResponseを作る
  def searchStocks(
    searchRequest: SearchRequest
  ): Program[Result[SearchResponse]] = {
    StockValidator
      .validateSearchRequest(searchRequest)
      .fold(
        errors =>
          Free.pure[Actions, Result[SearchResponse]](
            Left(
              RequestErrors(
                errors.toNonEmptyList.toList.map(_.message).mkString(",")
              )
            )
        ),
        request => {
          val result = for {
            a <- EitherT(search(request))
            b <- EitherT(getNews(a.stock))
          } yield b
          result.value
        }
      )
  }
}
