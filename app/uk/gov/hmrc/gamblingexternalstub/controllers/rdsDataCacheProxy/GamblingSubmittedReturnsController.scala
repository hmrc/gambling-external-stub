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
import scala.math.BigDecimal.RoundingMode

class GamblingSubmittedReturnsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  def getSubmittedReturns(
    regNumber: String,
    sortBy: Option[Int],
    orderBy: Option[String]
  ): Action[AnyContent] = Action { implicit request =>

    val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)
    val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)
    val sixthDigit = regNumber.takeRight(6).dropRight(5).toIntOption.getOrElse(0)

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
            "message" -> "No SubmittedReturns found for the given registration number"
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
        val sort = sortBy match { // 1=PERIOD_START_DATE , 2=SUBMITTED_DATE , else PERIOD_END_DATE
          case s @ (Some(1) | Some(2)) => s.get
          case _                       => 3
        }

        val (order, orderFunc) = orderBy.map(_.trim.toUpperCase()) match
          case Some("DESC") =>
            ("DESC",
             (leftE: SubmittedReturnsItem, rightE: SubmittedReturnsItem) => leftE.submitted_date.toEpochDay > rightE.submitted_date.toEpochDay
            )
          case _ =>
            ("ASC", (leftE: SubmittedReturnsItem, rightE: SubmittedReturnsItem) => leftE.submitted_date.toEpochDay < rightE.submitted_date.toEpochDay)

        logger.info(
          s"[getSubmittedReturns] regNumber=$regNumber sortBy=$sortBy orderBy=$orderBy sort=$sort order=$order"
        )

        val allRecords = (1 to recordCount).map { i =>
          val (mgd_period_start, mgd_period_end, submitted_date, ack_ref) = getSubmittedReturnItem(i, sixthDigit, sort, order)

          SubmittedReturnsItem(
            consec_no      = i,
            mgd_period     = s"$mgd_period_start - $mgd_period_end",
            submitted_date = submitted_date,
            ack_ref        = ack_ref
          )
        }

        Ok(
          Json.toJson(
            SubmittedReturns(
              items = allRecords.sortWith(orderFunc)
            )
          )
        )
    }
  }

  def getSubmittedReturnSingle(
    regNumber: String,
    consecNo: Option[Int]
  ): Action[AnyContent] = Action { implicit request =>

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
            "message" -> "No SubmittedReturnSingle found for the given registration number"
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
        logger.info(
          s"[getSubmittedReturnSingle] regNumber=$regNumber consecNo=$consecNo"
        )

        val consecNoInt = consecNo.getOrElse(0)
        val (mgd_period_start, mgd_period_end, submitted_date, ack_ref) = getSubmittedReturnItem(consecNoInt, 0, 0, "")

        Ok(
          Json.toJson(
            SubmittedReturnSingle(
              consecNo                     = consecNoInt,
              mgdPeriod                    = s"$mgd_period_start - $mgd_period_end",
              submittedDate                = submitted_date,
              ackRef                       = ack_ref,
              noOfMachines                 = 5 + consecNoInt,
              netTakingsHigherRate         = TwoDecimalPlace(100.10 * consecNoInt),
              netTakingsStdRate            = TwoDecimalPlace(20.00 * consecNoInt),
              netTakingsLowerRate          = TwoDecimalPlace(200.20 * consecNoInt),
              totalDueHigherRate           = TwoDecimalPlace(10.00 * consecNoInt),
              totalDueStdRate              = TwoDecimalPlace(300.30 * consecNoInt),
              totalDueLowerRate            = TwoDecimalPlace(5.00 * consecNoInt),
              dutyPayable                  = TwoDecimalPlace(35.00 * consecNoInt),
              underDeclaredDuty            = TwoDecimalPlace(40.00 * consecNoInt),
              previousReturnAmount         = TwoDecimalPlace(100.00 * consecNoInt),
              negativeAmountCarriedForward = TwoDecimalPlace(99.99 * consecNoInt),
              totalNetDutyPayable          = TwoDecimalPlace(75.49 * consecNoInt)
            )
          )
        )
    }
  }

  private def getSubmittedReturnItem(consecNo: Int, sixthDigit: Int, sort: Int, order: String) = {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val submitted_date = LocalDate
      .now()
      .minusMonths(consecNo)
      .minusYears(1)
      .withDayOfMonth(LocalDate.now().minusMonths(consecNo).minusYears(1).lengthOfMonth())
    val mgd_period_start = submitted_date.minusMonths(consecNo + 4).withDayOfMonth(1)
    val mgd_period_end = mgd_period_start.plusMonths(3).withDayOfMonth(mgd_period_start.plusMonths(3).lengthOfMonth())

    def rotateChar(c: Char, shift: Int, base: Char): Char = {
      (base + (c - base + shift) % 26).toChar
    }

    val iStr = (consecNo + 2).toString
    val ack_ref = sixthDigit match {
      case 9 => s"${consecNo}__sortBy=${sort}__orderBy=$order"
      case _ =>
        f"${iStr.charAt(iStr.length() - 1)}${rotateChar('J', consecNo, 'A')}${rotateChar('Q', consecNo, 'A')}${rotateChar('Z', consecNo, 'A')} ${rotateChar('J', consecNo, 'A')}${rotateChar('A', consecNo, 'A')}${rotateChar('Z', consecNo, 'A')}${rotateChar('E', consecNo, 'A')} ${rotateChar('I', consecNo, 'A')}${rotateChar('Y', consecNo, 'A')}${rotateChar('C', consecNo, 'A')}${rotateChar('M', consecNo, 'A')} TKM"
    }

    (mgd_period_start.format(formatter), mgd_period_end.format(formatter), submitted_date, ack_ref)
  }

  private def TwoDecimalPlace(b: Double) = BigDecimal(b).setScale(2, RoundingMode.HALF_EVEN)
}
