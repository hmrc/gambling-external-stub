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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject

class GamblingReturnsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  // Last 3 digits of regNumber  = HTTP status code  (e.g. ...200, ...401, ...404, ...500)
  // 4th and 5th digits from right = 2-digit record count (00-99, max 50 in practice)
  //   e.g. XWM00003103200 -> 03 records, XWM00003150200 -> 50 records
  def getReturnsSubmitted(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>

    if (Regime.fromString(regime).isEmpty) {
      BadRequest(
        Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> s"regime must be one of: ${Regime.validCodes}"
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
              "message" -> "No returns found for the given registration number"
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
          val today          = LocalDate.now()
          val periodStart    = today.minusMonths(18).withDayOfMonth(1)
          val periodEnd      = today.withDayOfMonth(today.lengthOfMonth())
          val windowMonths   = (periodEnd.getYear - periodStart.getYear) * 12 +
                                 (periodEnd.getMonthValue - periodStart.getMonthValue) + 1

          val allRecords = (1 to recordCount).map { i =>
            val monthOffset = (i - 1) % windowMonths
            val start       = periodStart.plusMonths(monthOffset)
            AmountDeclared(
              descriptionCode = Some(i),
              periodStartDate = Some(start),
              periodEndDate   = Some(start.withDayOfMonth(start.lengthOfMonth())),
              amount          = Some(BigDecimal(i * 1000))
            )
          }
          val from = (pageNo - 1) * pageSize
          val page = allRecords.slice(from, from + pageSize)
          Ok(
            Json.toJson(
              ReturnsSubmitted(
                periodStartDate    = Some(periodStart),
                periodEndDate      = Some(periodEnd),
                total              = Some(allRecords.flatMap(_.amount).sum),
                totalPeriodRecords = Some(recordCount),
                amountDeclared     = page
              )
            )
          )
      }
    }
  }
}
