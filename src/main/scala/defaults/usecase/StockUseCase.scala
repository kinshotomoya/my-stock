package defaults.usecase

import cats.data.OptionT
import com.google.inject.Inject
import com.jimmoores.quandl.Frequency
import defaults.domain.model.{QuandlResult, StockCode}
import defaults.repository.api.{
  QuandleApiRepositoryImpl,
  YahooFinanceApiRepositoryImpl
}

import scala.concurrent.Future

class StockUseCase @Inject()(
  stockRepository: QuandleApiRepositoryImpl,
  yahooFinanceApiRepositoryImpl: YahooFinanceApiRepositoryImpl
) {

  // ------------------Quandle APIをベースにしている--------------

  def getStocksBy(stockCodes: List[StockCode]): Future[List[QuandlResult]] = {
    val stockList: Future[List[QuandlResult]] =
      stockRepository.getStocks(stockCodes, Frequency.ANNUAL)
    stockList
  }

  def getStockBy(stockCode: StockCode): OptionT[Future, QuandlResult] = {
    val stockList: OptionT[Future, QuandlResult] =
      stockRepository.getStock(stockCode, Frequency.ANNUAL)
    stockList
  }

  // ------------------Quandle APIをベースにしている--------------

  // ------------------Yahoo Finance APIをベースにしている--------------

  // accountIdを元に以下の４つを取得する
  // 1. mysqlに保存しているお気に入りのstockCode
  // 2. 1のstockCodeのそれぞれの名前、指数、簡単なグラフ
  // 3. 1のstockCodeにひもづくビジネスニュース
  // TODO: 2、3は並列で取得するので、akka-actorを使う
  def listMyPage(accountId: Long): Future[Nothing] = {
    ???
  }

  // お気に入りしているstockCodeの詳細情報を取得する
  // 1. 名前、グラフ、高値などの値
  // 2. ひもづくビジネスニュース
  // TODO: 1、2は並列で取得するので、akka-actorを使う
  def listStockDetailPage(stockCode: StockCode): Future[Nothing] = {
    ???
  }
  // ------------------Yahoo Finance APIをベースにしている--------------

}
