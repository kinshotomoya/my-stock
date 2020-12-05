package defaults.repository

import com.jimmoores.quandl.{DataSetRequest, Frequency}
import defaults.domain.model.StockCode

trait Builder[A] {
  def build(code: StockCode, frequency: Frequency): A
}

object Builder {

  implicit def requestBuilder: Builder[DataSetRequest] =
    new Builder[DataSetRequest] {
      override def build(code: StockCode,
                         frequency: Frequency): DataSetRequest = {
        DataSetRequest.Builder.of(code.value).withFrequency(frequency).build()
      }
    }
}
