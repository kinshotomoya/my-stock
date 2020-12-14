package freeMonad

import cats.~>
import freeMonad.actions.Actions
import freeMonad.actions.Actions.{Actions, GetNews, Program, Result, Search}
import freeMonad.domains._
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class StockUseCaseSpec extends FunSpec with Matchers {
  describe("test Actions") {

    implicit val ec = ExecutionContext.global
    // interpreterをモックするだけでよくなる！
    val testInterpreter: Actions ~> Future = new (Actions ~> Future) {
      override def apply[A](fa: Actions[A]): Future[A] = fa match {
        case Search(_)  => Future(Right(SearchResponse(Stock())))
        case GetNews(_) => Future(Right(SearchResponse(Stock())))
      }
    }

    it("stockCodeが存在しない場合、バリデーションに引っかかる") {
      // given
      val searchRequest = SearchRequest(StockCode(""))
      // when
      val result: Program[Result[SearchResponse]] =
        Actions.searchStocks(searchRequest)
      // then
      import cats.instances.future._
      Await.result(result.foldMap(testInterpreter), Duration.Inf) shouldBe Left(
        RequestErrors("sotckcodeが空です。")
      )
    }

    it("stockCodeが大文字を含んでいる場合、バリデーションに引っかかる") {
      // given
      val searchRequest = SearchRequest(StockCode("Contains-Big-Letter"))
      // when
      val result: Program[Result[SearchResponse]] =
        Actions.searchStocks(searchRequest)
      // then
      import cats.instances.future._
      Await.result(result.foldMap(testInterpreter), Duration.Inf) shouldBe Left(
        RequestErrors("小文字英数字のみが可能です。")
      )
    }

    // TODO: GetNewsを含んだロジックのテスト
  }
}
