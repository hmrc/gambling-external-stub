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

case class GamblingReturnPeriods(
  mgdRegNumber: String,
  returnPeriodsId: String,
  nstpEndDate1: String,
  nstpEndDate2: String,
  nstpEndDate3: String,
  nstpEndDate4: String,
  nstpEndDate5: String,
  nstpEndDate6: String,
  nstpEndDate7: String,
  nstpEndDate8: String,
  isInLastNstp: String,
  finalPeriodWarning: String,
  hasExistingNstpValues: String,
  systemDate: String
)

object GamblingReturnPeriodsFormats {

  implicit val gamblingReturnPeriodsFormat: OFormat[GamblingReturnPeriods] =
    Json.format[GamblingReturnPeriods]

  def fullModel(
    mgdRegNumber: String
  ): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = mgdRegNumber,
      returnPeriodsId       = "1",
      nstpEndDate1          = "14-OCT-24",
      nstpEndDate2          = "14-JAN-25",
      nstpEndDate3          = "15-APR-25",
      nstpEndDate4          = "15-JUL-25",
      nstpEndDate5          = "14-OCT-25",
      nstpEndDate6          = "14-JAN-26",
      nstpEndDate7          = "15-APR-26",
      nstpEndDate8          = "17-JUL-26",
      isInLastNstp          = "1",
      finalPeriodWarning    = "0",
      hasExistingNstpValues = "1",
      systemDate            = "2026-05-31"
    )

  def partialModel(
    mgdRegNumber: String
  ): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = mgdRegNumber,
      returnPeriodsId       = "2",
      nstpEndDate1          = "31-MAR-26",
      nstpEndDate2          = "",
      nstpEndDate3          = "",
      nstpEndDate4          = "",
      nstpEndDate5          = "",
      nstpEndDate6          = "",
      nstpEndDate7          = "",
      nstpEndDate8          = "",
      isInLastNstp          = "0",
      finalPeriodWarning    = "0",
      hasExistingNstpValues = "1",
      systemDate            = "2026-05-31"
    )

  def noDataModel(
    mgdRegNumber: String
  ): GamblingReturnPeriods =
    GamblingReturnPeriods(
      mgdRegNumber          = "",
      returnPeriodsId       = "",
      nstpEndDate1          = "",
      nstpEndDate2          = "",
      nstpEndDate3          = "",
      nstpEndDate4          = "",
      nstpEndDate5          = "",
      nstpEndDate6          = "",
      nstpEndDate7          = "",
      nstpEndDate8          = "",
      isInLastNstp          = "",
      finalPeriodWarning    = "",
      hasExistingNstpValues = "",
      systemDate            = ""
    )

  def `XWM00000001770`: GamblingReturnPeriods =
    fullModel("XWM00000001770")
}
