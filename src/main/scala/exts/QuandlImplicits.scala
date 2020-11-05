package exts

import com.jimmoores.quandl.TabularResult
import domain.model.QuandlResult

object QuandlImplicits {

  implicit class RichTabularResult(val result: TabularResult) {
    // TODO: domainオブジェクトに詰める
    def convertToDomainObject: QuandlResult = {
      ???
    }
  }

}
