package uk.gov.hmrc.gamblingexternalstub.models

import play.api.libs.json.{Json, OFormat}

case class ReturnSummary(mgdRegNumber: String, returnsDue: Int, returnsOverdue: Int)

object ReturnSummary {
  implicit val format: OFormat[ReturnSummary] = Json.format[ReturnSummary]
}