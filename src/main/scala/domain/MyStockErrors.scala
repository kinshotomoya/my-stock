package domain

trait MyStockErrors {
  val message: String
}

case class TimeOutError(message: String) extends Throwable with MyStockErrors
