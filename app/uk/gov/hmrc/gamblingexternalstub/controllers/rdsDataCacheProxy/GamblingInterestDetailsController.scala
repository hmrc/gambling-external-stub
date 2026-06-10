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

class GamblingInterestDetailsController @Inject() (
                                             cc: ControllerComponents
                                           ) extends BackendController(cc) {

  private val descCodes = Seq(2640, 2650, 2655, 2680, 2685, 2690, 2695, 2660, 2670)

  def getInterestDetails(
                          regime: String,
                          regNumber: String,
                          pageNo: Int = 1,
                          pageSize: Int = 10
                        ): Action[AnyContent] = Action { _ =>
    if (Regime.fromString(regime).isEmpty) {
      BadRequest(
        Json.obj(
          "code" -> "INVALID_REGIME",
          "message" -> s"regime must be one of: ${Regime.validCodes}"
        )
      )
    } else {
      val statusCode = regNumber.takeRight(3).toIntOption.getOrElse(200)
      val recordCount = regNumber.takeRight(5).dropRight(3).toIntOption.getOrElse(0)

      statusCode match {
        case 400 => BadRequest(Json.obj("code" -> "INVALID_REQUEST", "message" -> "Bad request"))
        case 401 => Unauthorized(Json.obj("code" -> "UNAUTHORIZED", "message" -> "Unauthorized to access this resource"))
        case 404 => NotFound(Json.obj("code" -> "NOT_FOUND", "message" -> "No interest details found for this registration number"))
        case 500 => InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
        case _ =>
          val today = LocalDate.now()
          val periodStart = today.minusMonths(36).withDayOfMonth(1)
          val periodEnd = today.withDayOfMonth(today.lengthOfMonth())
          val periodStartItem = today.minusMonths(35).withDayOfMonth(1)
          val periodEndItem = today.withDayOfMonth(today.lengthOfMonth())


          val allRecords = (1 to recordCount).map { i =>
            val amount = BigDecimal(i * 100) * -1
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

          Ok(
            Json.toJson(
              InterestDetails(
                periodStartDate = Some(periodStart),
                periodEndDate   = Some(periodEnd),
                total           = allRecords.map(_.amount).sum,
                totalRecords    = recordCount,
                items           = page
              )
            )
          )
      }
    }
  }
}