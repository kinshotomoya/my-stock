package freeMonad.domains

import scala.util.matching.Regex

case class StockCode(value: String) extends AnyVal {
  def isContainsBigLetter: Boolean = {
    val regex: Regex = "^(?=.*[A-Z])".r
    regex.findFirstIn(value).isDefined
  }
}
