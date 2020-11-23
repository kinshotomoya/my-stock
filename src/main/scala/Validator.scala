import akka.actor.ActorSystem
import cats.data.ValidatedNec
import cats.implicits._
import com.google.inject.Inject
import domain.model.StockCode
import presentation.RequestCondition
import repository.mysql.StockRepositoryImpl


class Validator @Inject()(system: ActorSystem, stockRepository: StockRepositoryImpl) {

  type ValidationResult[A] = ValidatedNec[ValidationError, A]

  def validateRequestCondition(condition: RequestCondition): ValidationResult[RequestCondition] = {
    (validateNonRegisterStockCode(condition.stockCodes), validateLetterSizeIsSmall(condition.stockCodes)).mapN((stockCodes, _) => RequestCondition(stockCodes))
  }

  private def validateNonRegisterStockCode(stockCodes: List[StockCode]): ValidationResult[List[StockCode]] = {
    // 続き
    // TODO: DBから自分が登録しているstockCodeを取得する
    // stockRepositoryimpleから取得する
    // catsのnestedで、いい感じにFuture[Option[A]]などを結合できるようにする
    // https://typelevel.org/cats/datatypes/nested.html
    stockCodes.validNec
  }

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