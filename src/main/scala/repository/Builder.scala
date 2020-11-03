package repository

import com.jimmoores.quandl.{DataSetRequest, Frequency}

trait Builder[A] {
  def build(code: String, frequency: Frequency): A
}

object Builder {

  implicit def requestBuilder: Builder[DataSetRequest] = new Builder[DataSetRequest] {
    override def build(code: String, frequency: Frequency): DataSetRequest = {
      DataSetRequest.Builder.of(code).withFrequency(frequency).build()
    }
  }
}