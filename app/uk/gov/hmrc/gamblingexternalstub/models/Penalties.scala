/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.gamblingexternalstub.models

import play.api.libs.json.{Json, OFormat, OWrites, Writes}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

final case class PenaltyItem(
  dateRaised: LocalDate,
  descriptionCode: Int,
  amount: BigDecimal,
  periodStartDate: LocalDate,
  periodEndDate: LocalDate
)

object PenaltyItem {
  private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
  implicit val localDateWrites: Writes[LocalDate] = Writes.temporalWrites[LocalDate, DateTimeFormatter](fmt)
  implicit val writes: OWrites[PenaltyItem] = Json.writes[PenaltyItem]
  implicit val format: OFormat[PenaltyItem] = Json.format[PenaltyItem]
}

final case class Penalties(
  periodStartDate: Option[LocalDate],
  periodEndDate: Option[LocalDate],
  total: BigDecimal,
  totalRecords: Int,
  items: Seq[PenaltyItem]
)

object Penalties {
  import PenaltyItem.localDateWrites
  implicit val writes: OWrites[Penalties] = Json.writes[Penalties]
  implicit val format: OFormat[Penalties] = Json.format[Penalties]
}
