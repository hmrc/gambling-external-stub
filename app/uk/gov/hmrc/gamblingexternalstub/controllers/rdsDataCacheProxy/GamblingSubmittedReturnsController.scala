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
          s"[getSubmittedReturns] regNumber=$regNumber sortByOption=$sortBy orderByOption=$orderBy sort=$sort order=$order"
        )

        val allRecords = (1 to recordCount).map { i =>
          val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
          val mgd_period_month = LocalDate.now().minusMonths(i)
          val mgd_period = mgd_period_month.withDayOfMonth(mgd_period_month.lengthOfMonth()).format(formatter)

          SubmittedReturnsItem(
            consec_no      = 1000 + (i * 100),
            mgd_period     = mgd_period,
            submitted_date = mgd_period_month.plusMonths(1),
            ack_ref        = s"${1000 + (i * 100)}__sortBy=${sort}__orderBy=$order"
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
}
