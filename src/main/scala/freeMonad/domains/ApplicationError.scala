package freeMonad.domains

sealed trait RequestError {
  val message: String = ""
}
case object EmptyStockCodeError extends RequestError {
  override val message: String = "sotckcodeが空です。"
}
case object SizeIsSmallError extends RequestError {
  override val message: String = "小文字英数字のみが可能です。"
}
case class RequestErrors(messages: String) extends RequestError
