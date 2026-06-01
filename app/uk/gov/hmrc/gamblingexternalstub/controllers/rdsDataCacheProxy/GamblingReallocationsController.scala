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

class GamblingReallocationsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def getReallocationsIn(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = {
    getReallocations(regime, regNumber, pageNo, pageSize, 1, 0)
  }

  def getReallocationsOut(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = {
    getReallocations(regime, regNumber, pageNo, pageSize, -1, 33.33)
  }

  def getReallocationsDetails(regime: String, regNumber: String): Action[AnyContent] = {
    reallocationsDetails(regime, regNumber)
  }

  private def getReallocations(
    regime: String,
    regNumber: String,
    pageNo: Int,
    pageSize: Int,
    amountSign: Int,
    offset: BigDecimal
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

        case _ => Ok(Json.toJson(createReallocations(regNumber, pageNo, pageSize, amountSign, offset)))
      }
    }
  }

  private def reallocationsDetails(regime: String, regNumber: String): Action[AnyContent] = Action { _ =>
    if (Regime.fromString(regime).isEmpty) {
      BadRequest(
        Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> s"regime must be one of: ${Regime.validCodes}"
        )
      )
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)

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
          val reallocationsIn = createReallocations(regNumber, 1, 10, 1, 0)
          val reallocationsOut = createReallocations(regNumber, 1, 10, -1, 33.33)

          Ok(
            Json.toJson(
              ReallocationsDetails(
                periodStartDate        = reallocationsIn.periodStartDate,
                periodEndDate          = reallocationsIn.periodEndDate,
                reallocationsInAmount  = reallocationsIn.total.getOrElse(0),
                reallocationsOutAmount = reallocationsOut.total.getOrElse(0),
                total                  = (reallocationsIn.total.getOrElse(BigDecimal(0)) + reallocationsOut.total.getOrElse(BigDecimal(0))).abs * -1
              )
            )
          )
      }
    }
  }

  private def createReallocations(regNumber: String, pageNo: Int, pageSize: Int, amountSign: Int, offset: BigDecimal): Reallocations = {
    val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)

    val today = LocalDate.now()
    val periodStart = today.minusMonths(18).withDayOfMonth(1)
    val periodEnd = today.withDayOfMonth(today.lengthOfMonth())
    val windowMonths = (periodEnd.getYear - periodStart.getYear) * 12 +
      (periodEnd.getMonthValue - periodStart.getMonthValue) + 1

    val allRecords = (1 to recordCount).map { i =>
      val monthOffset = (i - 1) % windowMonths
      val dateProcessed = periodStart.plusMonths(monthOffset)
      val amount = (BigDecimal(i * 100) + offset) * amountSign

      ReallocationItem(
        dateProcessed = Some(dateProcessed),
        amount        = Some(amount)
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allRecords.slice(from, from + pageSize)

    Reallocations(
      periodStartDate = Some(periodStart),
      periodEndDate   = Some(periodEnd),
      total           = Some(allRecords.flatMap(_.amount).sum),
      totalRecords    = Some(recordCount),
      items           = page
    )
  }
}
