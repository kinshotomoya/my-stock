import akka.actor.ActorSystem
import cats.data.ValidatedNec
import cats.implicits._
import com.google.inject.Inject
import domain.model.StockCode
import presentation.RequestCondition
import repository.api.QuandleApiRepositoryImpl


class Validator @Inject()(system: ActorSystem, quandleApiRepository: QuandleApiRepositoryImpl) {

  type ValidationResult[A] = ValidatedNec[ValidationError, A]

  def validateRequestCondition(condition: RequestCondition): ValidationResult[RequestCondition] =
    validateLetterSizeIsSmall(condition.stockCodes).map(stockCodes =>
      RequestCondition(accountId = condition.accountId, stockCodes = stockCodes)
    )


  private def validateLetterSizeIsSmall(stockCodes: List[StockCode]): ValidationResult[List[StockCode]] = {
    val isValidLetterSize = stockCodes.forall(code => code.isBigLetter)
    if(isValidLetterSize) stockCodes.validNec else letterSizeIsSmall.invalidNec
  }

}

sealed trait ValidationError {
  def validationMessage: String
}
case object notRegisteredStockCode extends ValidationError {
  override def validationMessage: String = "そのstockコードは登録していません。"
}

case object letterSizeIsSmall extends ValidationError {
  override def validationMessage: String = "大文字の英数字で指定してください。"
}