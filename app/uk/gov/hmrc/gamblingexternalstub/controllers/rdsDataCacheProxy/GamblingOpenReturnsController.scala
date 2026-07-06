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

package uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GamblingOpenReturnsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val supportedRegimes = List(Regime.MGD)

  // Last 3 digits of regNumber  = HTTP status code  (e.g. ...200, ...401, ...404, ...500)
  // 4th and 5th digits from right = 2-digit record count (00-99, max 50 in practice)
  //   e.g. XWM00003103200 -> 03 records, XWM00003150200 -> 50 records
  def getOpenPeriods(
    regime: String,
    regNumber: String,
    sortBy: Option[Int],
    orderBy: Option[String]
  ): Action[AnyContent] = Action { implicit request =>

    if (!Regime.fromString(regime).exists(supportedRegimes.contains)) {
      BadRequest(
        Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> s"regime must be one of: ${Regime.MGD}"
        )
      )
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)
      val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)

      statusCode match {

        case 400 =>
          BadRequest(
            Json.obj(
              "code"    -> "INVALID_REQUEST",
              "message" -> "Bad request"
            )
          )

        case 401 =>
          Unauthorized(
            Json.obj(
              "code"    -> "UNAUTHORIZED",
              "message" -> "Unauthorized to access this resource"
            )
          )

        case 404 =>
          NotFound(
            Json.obj(
              "code"    -> "NOT_FOUND",
              "message" -> "No OpenReturnPeriods found for the given registration number"
            )
          )

        case 500 =>
          InternalServerError(
            Json.obj(
              "code"    -> "UNEXPECTED_ERROR",
              "message" -> "Unexpected error occurred"
            )
          )

        case _ =>
          // 1=PERIOD , 2=DUE_DATE , 3=STATUS , else PERIOD
          val sort = sortBy.filter(s => s == 1 || s == 2 || s == 3).getOrElse(1)

          val (order, orderFunc) = orderBy.map(_.trim.toUpperCase()) match
            case Some("DESC") =>
              ("DESC",
               (leftE: OpenPeriod, rightE: OpenPeriod) =>
                 sort match {
                   case 1 => leftE.period > rightE.period
                   case 2 => leftE.dueDate.toEpochDay > rightE.dueDate.toEpochDay
                   case _ => leftE.status > rightE.status
                 }
              )
            case _ =>
              ("ASC",
               (leftE: OpenPeriod, rightE: OpenPeriod) =>
                 sort match {
                   case 1 => leftE.period < rightE.period
                   case 2 => leftE.dueDate.toEpochDay < rightE.dueDate.toEpochDay
                   case _ => leftE.status < rightE.status
                 }
              )

          logger.info(
            s"[getOpenPeriods] regime=$regime regNumber=$regNumber sortBy=$sortBy orderBy=$orderBy sort=$sort order=$order"
          )

          val allRecords = (1 to recordCount).map(getOpenPeriodItem)

          Ok(
            Json.toJson(
              OpenReturnPeriods(
                openPeriods = allRecords.sortWith(orderFunc)
              )
            )
          )
      }
    }
  }

  private def getOpenPeriodItem(consecNo: Int): OpenPeriod = {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val periodStart = LocalDate.now().plusMonths((consecNo - 1) * 3L).withDayOfMonth(1)
    val periodEnd = periodStart.plusMonths(3).minusDays(1)
    val dueDate = periodEnd.plusMonths(1)

    OpenPeriod(
      consecNo = consecNo,
      period   = s"${periodStart.format(formatter)} - ${periodEnd.format(formatter)}",
      dueDate  = dueDate,
      status   = if (consecNo % 2 == 0) 1 else 2
    )
  }
}
