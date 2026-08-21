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
import uk.gov.hmrc.gamblingexternalstub.models.{PremisesDetails, *}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject

class GamblingLicensesAndPremisesController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val supportedRegimes = List(Regime.MGD)

  def getPremisesDetails(regime: String, mgdRegNumber: String): Action[AnyContent] = Action { _ =>
    if (!Regime.fromString(regime.trim.toLowerCase()).exists(supportedRegimes.contains)) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"Regime $regime is not supported"))
    } else {
      mgdRegNumber match {

        case "invalid" => invalidResponse

        case "error" => errorResponse

        case "XGM00000001763" =>
          Ok(
            Json.toJson(
              Response(
                totalRows = Some(1000),
                premises = Seq(
                  PremisesDetails(
                    mgdRegNumber = "XGM00000001763",
                    address1     = Some("Flat 55"),
                    address2     = Some("10 Random Road"),
                    address3     = Some("Gateshead"),
                    address4     = None,
                    postcode     = None,
                    Some(fixedDate)
                  )
                )
              )
            )
          )

        case "XGM00000001764" =>
          Ok(
            Json.toJson(
              Response(
                totalRows = Some(0),
                premises = Seq(
                )
              )
            )
          )

        case reg =>
          Ok(
            Json.toJson(
              Response(
                totalRows = Some(1000),
                premises = Seq(
                  PremisesDetails(
                    mgdRegNumber = mgdRegNumber,
                    address1     = Some("Flat 55"),
                    address2     = Some("20 Market Calle"),
                    address3     = Some("Barcelona"),
                    address4     = None,
                    postcode     = Some("08001"),
                    Some(fixedDate)
                  ),
                  PremisesDetails(
                    mgdRegNumber = mgdRegNumber,
                    address1     = Some("Flat 1"),
                    address2     = Some("10 Market Calle"),
                    address3     = Some("Madrid"),
                    address4     = None,
                    postcode     = Some("28058"),
                    Some(fixedDate)
                  )
                )
              )
            )
          )
      }
    }
  }

  private val fixedDate = LocalDate.parse("2026-01-01")

  private val invalidResponse =
    BadRequest(
      Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    )

  private val errorResponse =
    InternalServerError(
      Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    )

}
