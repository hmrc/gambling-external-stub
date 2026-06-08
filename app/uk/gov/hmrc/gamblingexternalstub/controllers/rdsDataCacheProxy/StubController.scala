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
  with GamblingAssessmentsInAbsenceOfReturnsController
  with GamblingPenaltiesController
  with GamblingReallocationsController
  with OtherAssessmentsT
  with GamblingPaymentsController
  with Logging {

  def getGeneric(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { req =>

    // TODO: extract routeURL from req.uri
    // TODO: for manual testing, the testers need to be able to use 1 regNo that works with all endpoints. The below code needs a different regNo for each endpoint

    System.out.println(s">>>> req.uri=${req.uri}")
    System.out.println(s">>>> req.toString=${req.toString}")
    validateAndExecute(regime, regNumber, pageNo, pageSize, req.uri)
  }

  def getReturnsSubmitted(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { req =>
//    validateAndExecute(regime, regNumber, pageNo, pageSize, "returns-submitted")
    BadRequest(
      Json.obj(
        "code" -> "INVALID_REQUEST",
        "message" -> s"!!!!!!!!!!!!!!!!"
      )
    )
  }

  def getAssessmentsInAbsenceOfReturns(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "assessments-without-returns")
  }

  def getPenalties(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "penalties")
  }

  def getReallocationsDetails(regime: String, regNumber: String): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, 0, 0, "reallocations-details")
  }

  def getReallocationsIn(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "reallocations-in")
  }

  def getReallocationsOut(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "reallocations-out")
  }

  def getOtherAssessments(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "other-assessments")
  }

  def getPayments(regime: String, regNumber: String, pageNo: Int, pageSize: Int): Action[AnyContent] = Action { _ =>
    validateAndExecute(regime, regNumber, pageNo, pageSize, "payments")
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

            case ("01", "returns-submitted") => Ok(getReturnsSubmitted(regNumber, pageNo, pageSize, recordCount))

            case ("02", "assessments-without-returns") => Ok(getAssessmentsInAbsenceOfReturns(regNumber, pageNo, pageSize, recordCount))

            case ("03", "penalties") => Ok(getPenalties(regNumber, pageNo, pageSize, recordCount))

            case ("05", "reallocations-in" | "reallocations-out" | "reallocations-details") =>
              (customisation, routeURL) match {
                case (0 | 1 | 2 | 3, "reallocations-details") | (4, "reallocations-in") | (5, "reallocations-out") =>
                  Ok(getReallocations(regNumber, pageNo, pageSize, recordCount, customisation))
                case _ =>
                  BadRequest(
                    Json.obj(
                      "code"    -> "INVALID_REQUEST",
                      "message" -> s"routeURL ($routeURL) has an invalid customisation ($customisation)"
                    )
                  )
              }

            case ("06", "other-assessments") => Ok(getOtherAssessments(regNumber, pageNo, pageSize, recordCount))

            case ("08", "payments") => Ok(getPayments(regNumber, pageNo, pageSize, recordCount))

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
  def monthOffset(i: Int): Int = (i - 1) % windowMonths
}

trait itemDates extends defaultDates {
  val periodStartItem: LocalDate = today.minusMonths(15).withDayOfMonth(1)
  val periodEndItem: LocalDate = today.withDayOfMonth(today.lengthOfMonth())
}
