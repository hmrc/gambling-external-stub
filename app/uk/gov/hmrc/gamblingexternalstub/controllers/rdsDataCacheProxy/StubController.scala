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
import play.api.mvc.Results.BadRequest
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject
import scala.collection.immutable.HashMap

class StubController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with ReturnsSubmittedT
    with OtherAssessmentsT
    with GamblingReallocationsController
    with GamblingAssessmentsInAbsenceOfReturnsController
    with Logging {

  def getReturnsSubmitted(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "returns-submitted")
  }

  def getAssessmentsInAbsenceOfReturns(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "assessments-without-returns")
  }

  def getReallocationsIn(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "reallocations-in")
  }

  def getReallocationsOut(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "reallocations-out")
  }

  def getReallocationsDetails(regime: String, regNumber: String): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, 0, 0, "reallocations-details")
  }

  def getOtherAssessments(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "other-assessments")
  }

  private def validateAndExecute(regime: String, regNumber: String, pageNo: Int, pageSize: Int, routeURL: String): Result = {

    val requestTypes = HashMap(
      "01" -> "returns-submitted",
      "02" -> "assessments-without-returns",
      "03" -> "penalties",
      "04" -> "adjustments",
      "05" -> "reallocations",
      "06" -> "other-assessments",
      "07" -> "interest",
      "08" -> "payments",
      "09" -> "repayment"
    )

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
          val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)
          val requestType = regNumber.take(5).takeRight(2)
          val customisation = regNumber.takeRight(6).dropRight(5).toIntOption.getOrElse(0)
          val reqTypeString = requestTypes.getOrElse(requestType, "does not exist")
          logger.info(
            s"[validateAndExecute] statusCode=$statusCode  recordCount=$recordCount  routeURL=$routeURL  requestType=$requestType($reqTypeString)  customisation=$customisation"
          )
          System.out.println(s"[validateAndExecute] statusCode=$statusCode  recordCount=$recordCount  routeURL=$routeURL  requestType=$requestType($reqTypeString)  customisation=$customisation")

          (requestType, routeURL) match {

            case ("01", "returns-submitted") => Ok(getReturnsSubmitted2(regNumber, pageNo, pageSize, recordCount))

            case ("02", "assessments-without-returns") => Ok(getAssessmentsInAbsenceOfReturns(regNumber, pageNo, pageSize, recordCount))

            case ("05", "reallocations-in" | "reallocations-out" | "reallocations-details") =>
              (customisation, routeURL) match {
                case (0, "reallocations-details") | (1, "reallocations-in") | (2, "reallocations-out") =>
                  Ok(getReallocations(regNumber, pageNo, pageSize, recordCount, customisation))
                case _ =>
                  BadRequest(
                    Json.obj(
                      "code"    -> "INVALID_REQUEST",
                      "message" -> s"routeURL ($routeURL) does not match customisation ($customisation)"
                    )
                  )
              }

            case ("06", "other-assessments") => Ok(getOtherAssessments(regNumber, pageNo, pageSize, recordCount))

            case _ =>
              BadRequest(
                Json.obj(
                  "code"    -> "INVALID_REQUEST",
                  "message" -> s"routeURL ($routeURL) does not match requestType ($requestType)($reqTypeString)"
                )
              )
          }
      }
    }
  }
}

trait defaultDates {
  val today: LocalDate = LocalDate.now()
  val periodStart: LocalDate = today.minusMonths(18).withDayOfMonth(1)
  val periodEnd: LocalDate = today.withDayOfMonth(today.lengthOfMonth())
  val windowMonths: Int = (periodEnd.getYear - periodStart.getYear) * 12 +
    (periodEnd.getMonthValue - periodStart.getMonthValue) + 1
}

trait itemDates extends defaultDates {
  val periodStartItem: LocalDate = today.minusMonths(15).withDayOfMonth(1)
  val periodEndItem: LocalDate = today.withDayOfMonth(today.lengthOfMonth())
}
