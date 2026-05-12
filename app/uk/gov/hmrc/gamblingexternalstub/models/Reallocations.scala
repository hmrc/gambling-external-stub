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
import play.api.libs.json.{Json, OWrites, Writes}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

final case class ReallocationItem(
  dateProcessed: Option[LocalDate],
  amount: Option[BigDecimal]
)

object ReallocationItem {
  private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

  implicit val localDateWrites: Writes[LocalDate] =
    Writes.temporalWrites[LocalDate, DateTimeFormatter](fmt)

  implicit val writes: OWrites[ReallocationItem] =
    Json.writes[ReallocationItem]
}

final case class Reallocations(
  periodStartDate: Option[LocalDate],
  periodEndDate: Option[LocalDate],
  total: Option[BigDecimal],
  totalRecords: Option[Int],
  items: Seq[ReallocationItem]
)

object Reallocations {
  import ReallocationItem.localDateWrites

  implicit val writes: OWrites[Reallocations] =
    Json.writes[Reallocations]
}
