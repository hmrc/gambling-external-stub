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

class GamblingStatementOverviewController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def getStatementOverview(
    regime: String,
    regNumber: String
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
              "message" -> "No statement overview found for the given registration number"
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
          val seed = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(1)
          val seventhDigit = regNumber.takeRight(7).dropRight(6).toIntOption.getOrElse(1)

          Ok(Json.toJson(buildStatementOverview(seed, seventhDigit)))
      }
    }
  }

  private def buildStatementOverview(seed: Int, seventhDigit: Int): StatementOverview = {
    val today = LocalDate.now()
    val periodStart = today.minusMonths(12).withDayOfMonth(1)
    val periodEnd = today.withDayOfMonth(today.lengthOfMonth())

    seventhDigit match {
      // Variant 0: all fields are zero, total = £0
      case 0 =>
        StatementOverview(
          gtrPeriodStartDate = Some(periodStart),
          gtrPeriodEndDate   = Some(periodEnd),
          total              = BigDecimal(0),
          balance            = BigDecimal(0),
          amountDeclared     = BigDecimal(0),
          assessments        = BigDecimal(0),
          penalties          = BigDecimal(0),
          adjustments        = BigDecimal(0),
          reallocations      = BigDecimal(0),
          otherAssessments   = BigDecimal(0),
          interest           = BigDecimal(0),
          payments           = BigDecimal(0),
          repayments         = Some(BigDecimal(0))
        )

      // Variant 2: non-zero fields with high payments, resulting in total < 0
      case 2 =>
        val amountDeclared = BigDecimal(seed * 1000)
        val assessments = BigDecimal(seed * 50)
        val penalties = BigDecimal(seed * 25)
        val adjustments = BigDecimal(seed * 10)
        val reallocations = BigDecimal(seed * 5)
        val otherAssessments = BigDecimal(seed * 15)
        val interest = BigDecimal(seed * 8)
        val payments = BigDecimal(seed * 2000)
        val repayments = BigDecimal(seed * 100)

        val total = amountDeclared + assessments + penalties + adjustments + reallocations + otherAssessments + interest - payments - repayments
        val balance = total - payments

        StatementOverview(
          gtrPeriodStartDate = Some(periodStart),
          gtrPeriodEndDate   = Some(periodEnd),
          total              = total,
          balance            = balance,
          amountDeclared     = amountDeclared,
          assessments        = assessments,
          penalties          = penalties,
          adjustments        = adjustments,
          reallocations      = reallocations,
          otherAssessments   = otherAssessments,
          interest           = interest,
          payments           = payments,
          repayments         = Some(repayments)
        )

      // Variant 1 (default): non-zero fields with low payments, resulting in total > 0
      case _ =>
        val amountDeclared = BigDecimal(seed * 1000)
        val assessments = BigDecimal(seed * 50)
        val penalties = BigDecimal(seed * 25)
        val adjustments = BigDecimal(seed * 10)
        val reallocations = BigDecimal(seed * 5)
        val otherAssessments = BigDecimal(seed * 15)
        val interest = BigDecimal(seed * 8)
        val payments = BigDecimal(seed * 200)
        val repayments = BigDecimal(seed * 100)

        val total = amountDeclared + assessments + penalties + adjustments + reallocations + otherAssessments + interest - payments - repayments
        val balance = total - payments

        StatementOverview(
          gtrPeriodStartDate = Some(periodStart),
          gtrPeriodEndDate   = Some(periodEnd),
          total              = total,
          balance            = balance,
          amountDeclared     = amountDeclared,
          assessments        = assessments,
          penalties          = penalties,
          adjustments        = adjustments,
          reallocations      = reallocations,
          otherAssessments   = otherAssessments,
          interest           = interest,
          payments           = payments,
          repayments         = Some(repayments)
        )
    }
  }
}
