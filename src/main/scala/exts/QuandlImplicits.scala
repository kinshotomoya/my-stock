package exts

import com.jimmoores.quandl.{Row, TabularResult}
import domain.model.QuandlResult

object QuandlImplicits {

  implicit class RichTabularResult(val result: TabularResult) {
    def convertToDomainObject: QuandlResult ={
      val listMap: Seq[(String, String)] = for(i <- 0 until result.size()) yield {
        val row: Row = result.get(i)
        row.getString(0) -> row.getString(1)
      }
      QuandlResult(listMap.toMap)
    }
  }

}
