package freeMonad.domains

sealed trait Request {
  val stockCode: StockCode
}
case class SearchRequest(code: StockCode) extends Request {
  override val stockCode: StockCode = code
}
