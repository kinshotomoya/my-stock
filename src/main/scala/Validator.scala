import cats.data.ValidatedNec
import cats.implicits._
import domain.model.StockCode
import presentation.RequestCondition


object Validator {

  type ValidationResult[A] = ValidatedNec[ValidationError, A]

  def validateRequestCondition(condition: RequestCondition): ValidationResult[RequestCondition] = {
    (validateNonRegisterStockCode(condition.stockCodes), validateLetterSizeIsSmall(condition.stockCodes)).mapN((stockCodes, _) => RequestCondition(stockCodes))
  }

  private def validateNonRegisterStockCode(stockCodes: List[StockCode]): ValidationResult[List[StockCode]] = {
    // TODO: DBから自分が登録しているstockCodeを取得する
    stockCodes.validNec
  }

  private def validateLetterSizeIsSmall(stockCodes: List[StockCode]): ValidationResult[List[StockCode]] = {
    // 続き！！！
    // 小文字が含まれてたら、invalidにする
    letterSizeIsSmall.invalidNec
  }

}

sealed trait ValidationError {
  def validationMessage: String
}
case object notRegisteredStockCode extends ValidationError {
  override def validationMessage: String = "そのstockコードは登録していません。"
}

case object letterSizeIsSmall extends ValidationError {
  override def validationMessage: String = "大文字で指定してください。"
}