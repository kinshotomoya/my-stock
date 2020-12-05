package freeMonad.domains

case class StockCode(value: String) extends AnyVal {
  def isBigLetter: Boolean = value.matches("^[A-Z0-9_\\-\\/\\_]+$")
}
