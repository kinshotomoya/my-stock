package freeMonad.domains

sealed trait RequestError
case object EmptyStockCodeError extends RequestError {
  val message: String = "sotckcodeが空です。"
}
case object SizeIsSmallError extends RequestError {
  val message: String = "小文字英数字のみが可能です。"
}
case class RequestErrors(messages: String) extends RequestError
