import akka.actor.ActorSystem
import cats.data.ValidatedNec
import cats.implicits._
import com.google.inject.Inject
import presentation.RequestCondition
import repository.api.QuandleApiRepositoryImpl


class Validator @Inject()(system: ActorSystem, quandleApiRepository: QuandleApiRepositoryImpl) {

  type ValidationResult[A] = ValidatedNec[ValidationError, A]

  def validateRequestCondition(condition: RequestCondition): ValidationResult[RequestCondition] =
    (validateStockCodeIsExists(condition), validateLetterSizeIsSmall(condition)).mapN((condition, _) =>
      RequestCondition(accountId = condition.accountId, stockCodes = condition.stockCodes)
    )

  private def validateStockCodeIsExists(condition: RequestCondition): ValidationResult[RequestCondition] = {
    if(condition.stockCodes.isEmpty) emptyStockCodes.invalidNec else condition.validNec
  }


  private def validateLetterSizeIsSmall(condition: RequestCondition): ValidationResult[RequestCondition] = {
    val isValidLetterSize = condition.stockCodes.forall(code => code.isBigLetter)
    if(isValidLetterSize) condition.validNec else letterSizeIsSmall.invalidNec
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

case object emptyStockCodes extends ValidationError {
  override def validationMessage: String = "最低1つのStockCodeを入力してください。"
}