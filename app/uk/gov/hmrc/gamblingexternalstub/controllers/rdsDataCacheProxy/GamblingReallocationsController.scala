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
import scala.util.Random

class GamblingReallocationsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def getReallocationsIn(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = {
    getReallocations(regime, regNumber, pageNo, pageSize, 1)
  }

  def getReallocationsOut(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = {
    getReallocations(regime, regNumber, pageNo, pageSize, -1)
  }

  private def getReallocations(
    regime: String,
    regNumber: String,
    pageNo: Int,
    pageSize: Int,
    amountSign: Int
  ): Action[AnyContent] = Action { _ =>
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
              "message" -> "No reallocations found for the given registration number"
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
          val today = LocalDate.now()
          val periodStart = today.minusMonths(18).withDayOfMonth(1)
          val periodEnd = today.withDayOfMonth(today.lengthOfMonth())
          val windowMonths = (periodEnd.getYear - periodStart.getYear) * 12 +
            (periodEnd.getMonthValue - periodStart.getMonthValue) + 1

          val allRecords = (1 to recordCount).map { i =>
            val monthOffset = (i - 1) % windowMonths
            val dateProcessed = periodStart.plusMonths(monthOffset)
            val baseAmount = BigDecimal(i * Random.nextInt(100))
            val randomPennies = BigDecimal(Random.between(0.00, 0.99))
            val amountWithPennies = (baseAmount + randomPennies) * amountSign

            ReallocationItem(
              dateProcessed = Some(dateProcessed),
              amount        = Some(amountWithPennies)
            )
          }

          val from = (pageNo - 1) * pageSize
          val page = allRecords.slice(from, from + pageSize)

          Ok(
            Json.toJson(
              Reallocations(
                periodStartDate = Some(periodStart),
                periodEndDate   = Some(periodEnd),
                total           = Some(allRecords.flatMap(_.amount).sum),
                totalRecords    = Some(recordCount),
                items           = page
              )
            )
          )
      }
    }
  }
}
