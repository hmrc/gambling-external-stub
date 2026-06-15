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

class GamblingInterestController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  private val interestOffset = BigDecimal(0.55)
  private val interestAccruingOffset = BigDecimal(0.35)

  private val descCodes = Seq(2640, 2650, 2655, 2680, 2685, 2690, 2695, 2660, 2670)

  def getInterestOverview(
    regime: String,
    regNumber: String
  ): Action[AnyContent] = Action { _ =>
    if (Regime.fromString(regime).isEmpty) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"regime must be one of: ${Regime.validCodes}"))
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)

      statusCode match {
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No interest overview found for this registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _ =>
          val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)
          val sixthDigit = regNumber.takeRight(6).dropRight(5).toIntOption.getOrElse(0)

          val (interestDetailsRecordCount, interestAccruingRecordCount, repaymentInterestRecordCount) = (sixthDigit, regime.toLowerCase) match {
            case (0, "mgd") => (recordCount, recordCount, recordCount)
            case (1, _)     => (recordCount, 0, 0)
            case (2, _)     => (0, recordCount, 0)
            case (3, "mgd") => (0, 0, recordCount)
            case (4, _)     => (0, 0, 0)
            case (5, _)     => (recordCount, recordCount, 0)
            case (6, "mgd") => (0, recordCount, recordCount)
            case (7, "mgd") => (recordCount, 0, recordCount)
            case _          => (0, 0, 0)
          }

          val interestDetails = createInterestDetails(interestDetailsRecordCount, 1, 10, 0.11)
//          val interestAccruing = createInterestAccruing(interestAccruingRecordCount, 1, 10, 0.22)      TODO!!!
//          val repaymentInterest = createRepaymentInterest(repaymentInterestRecordCount, 1, 10, 0.33)   TODO!!!

          Ok(
            Json.toJson(
              InterestOverview(
                periodStartDate         = interestDetails.periodStartDate,
                periodEndDate           = interestDetails.periodEndDate,
                interestAmount          = interestDetails.total,
                interestAccruingAmount  = 200.22 * interestAccruingRecordCount,
                repaymentInterestAmount = 200.33 * repaymentInterestRecordCount,
                total                   = interestDetails.total + (200.22 * interestAccruingRecordCount) + (200.33 * repaymentInterestRecordCount)
              )
            )
          )
      }
    }
  }

  def getInterestDetails(
    regime: String,
    regNumber: String,
    pageNo: Int = 1,
    pageSize: Int = 10
  ): Action[AnyContent] = Action { _ =>
    if (Regime.fromString(regime).isEmpty) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"regime must be one of: ${Regime.validCodes}"))
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)
      val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)

      statusCode match {
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No interest details found for this registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _   => Ok(Json.toJson(createInterestDetails(recordCount, pageNo, pageSize, 0.11)))
      }
    }
  }

  def getRepaymentInterestDetails(
    regime: String,
    regNumber: String,
    pageNo: Int = 1,
    pageSize: Int = 10
  ): Action[AnyContent] = Action { _ =>
    if (Regime.fromString(regime).isEmpty) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"regime must be one of: ${Regime.validCodes}"))
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)
      val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)

      statusCode match {
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No repayment interest details found for this registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _   => Ok(Json.toJson(createInterestDetails(recordCount, pageNo, pageSize, 0.33)))
      }
    }
  }

  private def createInterestDetails(recordCount: Int, pageNo: Int, pageSize: Int, offset: BigDecimal) = {
    val today = LocalDate.now()
    val periodStart = today.minusMonths(36).withDayOfMonth(1)
    val periodEnd = today.withDayOfMonth(today.lengthOfMonth())
    val periodStartItem = today.minusMonths(35).withDayOfMonth(1)
    val periodEndItem = today.withDayOfMonth(today.lengthOfMonth())

    val allRecords = (1 to recordCount).map { i =>
      val amount = (BigDecimal(i * 100) + offset) * -1
      val code = descCodes((i - 1) % descCodes.size)

      InterestDetailItem(
        descriptionCode = code,
        amount          = amount,
        interestId      = f"SAFE-CHG-${i + 2}%05d",
        periodStartDate = periodStartItem,
        periodEndDate   = periodEndItem
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allRecords.slice(from, from + pageSize)

    InterestDetails(
      periodStartDate = Some(periodStart),
      periodEndDate   = Some(periodEnd),
      total           = allRecords.map(_.amount).sum,
      totalRecords    = recordCount,
      items           = page
    )
  }

  def getInterestDrilldown(
    regime: String,
    regNumber: String,
    interestId: String,
    pageNo: Int,
    pageSize: Int
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
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No interest accruing drilldown found for the given registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _   => Ok(Json.toJson(createInterestDrilldown(recordCount, pageNo, pageSize)))
      }
    }
  }

  def getInterestAccruingDrilldown(
    regime: String,
    regNumber: String,
    interestId: String,
    pageNo: Int,
    pageSize: Int
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
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No interest accruing drilldown found for the given registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _   => Ok(Json.toJson(createInterestAccruingDrilldown(recordCount, pageNo, pageSize)))
      }
    }
  }

  private def createInterestAccruingDrilldown(recordCount: Int, pageNo: Int, pageSize: Int): InterestAccruingDrilldown = {
    val today = LocalDate.now()
    val periodStart = today.minusMonths(18).withDayOfMonth(1)
    val periodEnd = today.plusMonths(3).withDayOfMonth(today.lengthOfMonth())

    val allItems = (1 to recordCount).map { i =>
      val dateFrom = periodStart.plusMonths((i - 1) % 21)
      val dateTo = dateFrom.plusDays(30)
      val amount = BigDecimal(i * 100) + interestAccruingOffset

      InterestAccruingDrilldownItem(
        interestOn = BigDecimal(i * 1000),
        dateFrom   = dateFrom,
        dateTo     = dateTo,
        noOfDays   = BigDecimal(30),
        rate       = BigDecimal(2.6),
        amount     = amount
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allItems.slice(from, from + pageSize)

    InterestAccruingDrilldown(
      periodStartDate = Some(periodStart),
      periodEndDate   = Some(periodEnd),
      total           = allItems.map(_.amount).sum,
      totalRecords    = recordCount,
      descriptionCode = Option(2660),
      items           = page
    )
  }

  private def createInterestDrilldown(recordCount: Int, pageNo: Int, pageSize: Int): InterestDrilldown = {
    val today = LocalDate.now()
    val periodStart = today.minusMonths(18).withDayOfMonth(1)
    val periodEnd = today.plusMonths(3).withDayOfMonth(today.lengthOfMonth())

    val allItems = (1 to recordCount).map { i =>
      val dateFrom = periodStart.plusMonths((i - 1) % 21)
      val dateTo = dateFrom.plusDays(30)
      val amount = BigDecimal(i * 100) + interestOffset

      InterestDrilldownItem(
        interestOn = BigDecimal(i * 1000),
        dateFrom   = dateFrom,
        dateTo     = dateTo,
        noOfDays   = BigDecimal(30),
        rate       = BigDecimal(2.6),
        amount     = amount
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allItems.slice(from, from + pageSize)

    InterestDrilldown(
      periodStartDate = Some(periodStart),
      periodEndDate   = Some(periodEnd),
      total           = allItems.map(_.amount).sum,
      totalRecords    = recordCount,
      descriptionCode = Option(2660),
      items           = page
    )
  }

}
