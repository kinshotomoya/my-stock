package freeMonad.Validators

import cats.data.ValidatedNec
import cats.implicits._
import freeMonad.domains.{
  EmptyStockCodeError,
  Request,
  RequestError,
  SearchRequest,
  SizeIsSmallError
}

object StockValidator {

  type ValidationResult[A] = ValidatedNec[RequestError, A]

  def validateSearchRequest(
    request: SearchRequest
  ): ValidationResult[SearchRequest] = {
    (
      validateStockCodeIsEmptyText(request),
      validateStockCodeIsSmallLetter(request)
    ).mapN((request, _) => {
      SearchRequest(code = request.stockCode)
    })
  }

  private def validateStockCodeIsEmptyText(
    request: Request
  ): ValidationResult[Request] = {
    if (request.stockCode.value.isEmpty) EmptyStockCodeError.invalidNec
    else request.validNec
  }

  private def validateStockCodeIsSmallLetter(
    request: Request
  ): ValidationResult[Request] = {
    if (request.stockCode.isBigLetter) SizeIsSmallError.invalidNec
    else request.validNec
  }
}
