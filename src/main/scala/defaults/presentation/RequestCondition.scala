package defaults.presentation

import defaults.domain.model.StockCode

case class RequestCondition(accountId: Long, stockCodes: List[StockCode])
