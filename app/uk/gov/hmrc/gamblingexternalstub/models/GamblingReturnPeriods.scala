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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale
import scala.util.Using

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
    new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .appendPattern("dd-MMM-yy")
      .toFormatter(Locale.ENGLISH)

  private val systemDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ISO_LOCAL_DATE

  /*
   * The fixtures may contain ISO dates (yyyy-MM-dd) or NSTP dates
   * (dd-MMM-yy). Support both when reading the JSON.
   */
  private implicit val localDateReads: Reads[LocalDate] =
    Reads.of[String].flatMapResult { value =>
      try {
        JsSuccess(LocalDate.parse(value, systemDateFormatter))
      } catch {
        case _: java.time.format.DateTimeParseException =>
          try {
            JsSuccess(LocalDate.parse(value, nstpDateFormatter))
          } catch {
            case exception: java.time.format.DateTimeParseException =>
              JsError(
                s"Invalid date '$value'. Expected yyyy-MM-dd or dd-MMM-yy"
              )
          }
      }
    }

  /*
   * Read all fields from the JSON fixture.
   *
   * readNullable supports both null and missing optional fields.
   */
  implicit val gamblingReturnPeriodsReads: Reads[GamblingReturnPeriods] =
    (
      (__ \ "mgdRegNumber").read[String] and
        (__ \ "returnPeriodsId").readNullable[Int] and
        (__ \ "nstpEndDate1").readNullable[LocalDate] and
        (__ \ "nstpEndDate2").readNullable[LocalDate] and
        (__ \ "nstpEndDate3").readNullable[LocalDate] and
        (__ \ "nstpEndDate4").readNullable[LocalDate] and
        (__ \ "nstpEndDate5").readNullable[LocalDate] and
        (__ \ "nstpEndDate6").readNullable[LocalDate] and
        (__ \ "nstpEndDate7").readNullable[LocalDate] and
        (__ \ "nstpEndDate8").readNullable[LocalDate] and
        (__ \ "isInLastNstp").readNullable[String] and
        (__ \ "finalPeriodWarning").readNullable[String] and
        (__ \ "hasExistingNstpValues").readNullable[String] and
        (__ \ "systemDate").readNullable[LocalDate]
    )(GamblingReturnPeriods.apply _)

  private def optionalIntWrites(value: Option[Int]): JsValue =
    value.map(JsNumber(_)).getOrElse(JsNull)

  private def optionalStringWrites(value: Option[String]): JsValue =
    value.map(JsString.apply).getOrElse(JsNull)

  private def optionalNstpDateWrites(value: Option[LocalDate]): JsValue =
    value
      .map(date =>
        JsString(
          date.format(nstpDateFormatter).toUpperCase(Locale.ENGLISH)
        )
      )
      .getOrElse(JsNull)

  private def optionalSystemDateWrites(value: Option[LocalDate]): JsValue =
    value
      .map(date =>
        JsString(
          date.format(nstpDateFormatter).toUpperCase(Locale.ENGLISH)
        )
      )
      .getOrElse(JsNull)

  /*
   * Preserve the existing response JSON structure and date formatting.
   */
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

  /*
   * Load a JSON fixture from the classpath and convert it into a model.
   *
   * Files under conf/data/return-periods/ are accessed using
   * /data/return-periods/<filename>.
   */
  private def loadFixture(filename: String): GamblingReturnPeriods = {
    val resourcePath = s"/data/return-periods/$filename"

    val inputStream =
      Option(getClass.getResourceAsStream(resourcePath))
        .getOrElse {
          throw new IllegalStateException(
            s"Could not find return-periods fixture: $resourcePath"
          )
        }

    Using.resource(inputStream) { stream =>
      Json.parse(stream).as[GamblingReturnPeriods]
    }
  }

  private lazy val xgm00000001761Data: GamblingReturnPeriods =
    loadFixture("XGM00000001761.json")

  private lazy val xgm00000001762Data: GamblingReturnPeriods =
    loadFixture("XGM00000001762.json")

  private lazy val xgm00000001763Data: GamblingReturnPeriods =
    loadFixture("XGM00000001763.json")

  private lazy val xgm00000001764Data: GamblingReturnPeriods =
    loadFixture("XGM00000001764.json")

  private lazy val xgm00000001765Data: GamblingReturnPeriods =
    loadFixture("XGM00000001765.json")

  private lazy val xgm00000001774Data: GamblingReturnPeriods =
    loadFixture("XGM00000001774.json")  

  private lazy val noData: GamblingReturnPeriods =
    loadFixture("return-periods.json")

  def xgm00000001761Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001761Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate   = Some(LocalDate.now())
    )

  def xgm00000001762Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001762Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate   = Some(LocalDate.now())
    )

  def xgm00000001763Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001763Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate   = Some(LocalDate.now())
    )

  def xgm00000001764Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001764Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate   = Some(LocalDate.now())
    )

  def xgm00000001765Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001765Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate   = Some(LocalDate.now())
    )

  def xgm00000001774Model(mgdRegNumber: String): GamblingReturnPeriods =
    xgm00000001774Data.copy(
      mgdRegNumber = mgdRegNumber,
      systemDate = Some(LocalDate.now())
    )  

  def noDataModel(): GamblingReturnPeriods =
    noData
}
