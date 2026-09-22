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

import play.api.libs.json.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

final case class GamblingReturnPeriods(
  mgdRegNumber: String,
  returnPeriodsId: Option[Int] = None,
  nstpEndDate1: Option[LocalDate] = None,
  nstpEndDate2: Option[LocalDate] = None,
  nstpEndDate3: Option[LocalDate] = None,
  nstpEndDate4: Option[LocalDate] = None,
  nstpEndDate5: Option[LocalDate] = None,
  nstpEndDate6: Option[LocalDate] = None,
  nstpEndDate7: Option[LocalDate] = None,
  nstpEndDate8: Option[LocalDate] = None,
  isInLastNstp: Option[String] = None,
  finalPeriodWarning: Option[String] = None,
  hasExistingNstpValues: Option[String] = None,
  systemDate: Option[LocalDate] = None
)

object GamblingReturnPeriodsFormats {

  private val nstpDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH)



  private def optionalIntWrites(value: Option[Int]): JsValue =
    value.map(JsNumber(_)).getOrElse(JsNull)

  private def optionalStringWrites(value: Option[String]): JsValue =
    value.map(JsString.apply).getOrElse(JsNull)

  private def optionalNstpDateWrites(value: Option[LocalDate]): JsValue =
    value
      .map(date => JsString(date.format(nstpDateFormatter).toUpperCase(Locale.ENGLISH)))
      .getOrElse(JsNull)

  private def optionalSystemDateWrites(
                                        value: Option[LocalDate]): JsValue =
    value
      .map(date =>
        JsString(
          date
            .format(nstpDateFormatter)
            .toUpperCase(Locale.ENGLISH)
        )
      )
      .getOrElse(JsNull)

  implicit val gamblingReturnPeriodsWrites: OWrites[GamblingReturnPeriods] =
    OWrites { returnPeriods =>
      Json.obj(
        "mgdRegNumber"          -> returnPeriods.mgdRegNumber,
        "returnPeriodsId"       -> optionalIntWrites(returnPeriods.returnPeriodsId),
        "nstpEndDate1"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate1),
        "nstpEndDate2"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate2),
        "nstpEndDate3"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate3),
        "nstpEndDate4"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate4),
        "nstpEndDate5"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate5),
        "nstpEndDate6"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate6),
        "nstpEndDate7"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate7),
        "nstpEndDate8"          -> optionalNstpDateWrites(returnPeriods.nstpEndDate8),
        "isInLastNstp"          -> optionalStringWrites(returnPeriods.isInLastNstp),
        "finalPeriodWarning"    -> optionalStringWrites(returnPeriods.finalPeriodWarning),
        "hasExistingNstpValues" -> optionalStringWrites(returnPeriods.hasExistingNstpValues),
        "systemDate"            -> optionalSystemDateWrites(returnPeriods.systemDate)
      )
    }

  def fullModel(mgdRegNumber: String): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = mgdRegNumber,
      returnPeriodsId       = Some(1),
      nstpEndDate1          = Some(LocalDate.of(2024, 10, 14)),
      nstpEndDate2          = Some(LocalDate.of(2025, 1, 14)),
      nstpEndDate3          = Some(LocalDate.of(2025, 4, 15)),
      nstpEndDate4          = Some(LocalDate.of(2025, 7, 15)),
      nstpEndDate5          = Some(LocalDate.of(2025, 10, 14)),
      nstpEndDate6          = Some(LocalDate.of(2026, 1, 14)),
      nstpEndDate7          = Some(LocalDate.of(2026, 4, 15)),
      nstpEndDate8          = Some(LocalDate.of(2026, 7, 17)),
      isInLastNstp          = Some("1"),
      finalPeriodWarning    = Some("0"),
      hasExistingNstpValues = Some("1"),
      systemDate            = Some(LocalDate.of(2026, 5, 31))
    )

  def partialModel(mgdRegNumber: String): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = mgdRegNumber,
      returnPeriodsId       = Some(2),
      nstpEndDate1          = Some(LocalDate.of(2026, 3, 31)),
      isInLastNstp          = Some("0"),
      finalPeriodWarning    = Some("0"),
      hasExistingNstpValues = Some("1"),
      systemDate            = Some(LocalDate.of(2026, 5, 31))
    )

  def noNstpValuesModel(mgdRegNumber: String): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = mgdRegNumber,
      returnPeriodsId       = Some(1),
      isInLastNstp          = Some("0"),
      finalPeriodWarning    = Some("0"),
      hasExistingNstpValues = Some("0"),
      systemDate            = Some(LocalDate.of(2026, 5, 31))
    )
}
