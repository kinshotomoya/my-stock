package presentation

import domain.model.StockCode

  case class RequestCondition(accountId: Long, stockCodes: List[StockCode])
