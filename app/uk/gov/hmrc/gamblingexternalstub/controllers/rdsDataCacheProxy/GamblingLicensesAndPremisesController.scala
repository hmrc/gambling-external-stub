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
import uk.gov.hmrc.gamblingexternalstub.models.LicenseDetails.{noDataModel, xgm00000001761Model, xgm00000001763Model, xgm00000001764Model, xgm00000001765Model}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.util.Using

class GamblingLicensesAndPremisesController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private val supportedRegimes = List(Regime.MGD)

  private lazy val xgm00000001761 = Using
    .resource(
      getClass.getResourceAsStream("/data/premises-details/XGM00000001761.json")
    )(Json.parse)
    .as[Response]

  private lazy val xgm00000001763 = Using
    .resource(
      getClass.getResourceAsStream("/data/premises-details/XGM00000001763.json")
    )(Json.parse)
    .as[Response]

  private lazy val xgm00000001764 = Using
    .resource(
      getClass.getResourceAsStream("/data/premises-details/XGM00000001764.json")
    )(Json.parse)
    .as[Response]

  private lazy val xgm00000001765 = Using
    .resource(
      getClass.getResourceAsStream("/data/premises-details/XGM00000001765.json")
    )(Json.parse)
    .as[Response]

  private lazy val defaultPremisesDetails = Using
    .resource(
      getClass.getResourceAsStream("/data/premises-details/premises-details.json")
    )(Json.parse)
    .as[Response]

  def getPremisesDetails(regime: String, mgdRegNumber: String): Action[AnyContent] = Action { _ =>
    if (!Regime.fromString(regime.trim.toLowerCase()).exists(supportedRegimes.contains)) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"Regime $regime is not supported"))
    } else {
      mgdRegNumber match {

        case "invalid" => invalidResponse

        case "error" => errorResponse

        case "XGM00000001761" => Ok(Json.toJson(xgm00000001761))

        case "XGM00000001763" => Ok(Json.toJson(xgm00000001763))

        case "XGM00000001764" => Ok(Json.toJson(xgm00000001764))

        case "XGM00000001765" => Ok(Json.toJson(xgm00000001765))

        case reg =>
          Ok(
            Json.toJson(
              defaultPremisesDetails.copy(
                premises = defaultPremisesDetails.premises.map(_.copy(mgdRegNumber = mgdRegNumber))
              )
            )
          )
      }
    }
  }

  def getLicenseDetails(regime: String, mgdRegNumber: String): Action[AnyContent] = Action { _ =>
    if (!Regime.fromString(regime.trim.toLowerCase()).exists(supportedRegimes.contains)) {
      BadRequest(Json.obj("code" -> "INVALID_REGIME", "message" -> s"Regime $regime is not supported"))
    } else {
      val sanitized = mgdRegNumber.trim.toUpperCase()
      sanitized match {

        case "XGM00000001761" =>
          Ok(Json.toJson(xgm00000001761Model(sanitized)))

        case "XGM00000001763" =>
          Ok(Json.toJson(xgm00000001763Model(sanitized)))

        case "XGM00000001764" =>
          Ok(Json.toJson(xgm00000001764Model(sanitized)))

        case "XGM00000001765" =>
          Ok(Json.toJson(xgm00000001765Model(sanitized)))

        case "XGM00000000400" =>
          BadRequest(
            Json.obj(
              "code"    -> "INVALID_REQUEST",
              "message" -> "Bad request"
            )
          )

        case "XGM00000000401" =>
          Unauthorized(
            Json.obj(
              "code"    -> "UNAUTHORIZED",
              "message" -> "Unauthorized to access this resource"
            )
          )

        case "XGM00000000500" =>
          InternalServerError(
            Json.obj(
              "code"    -> "UNEXPECTED_ERROR",
              "message" -> "Unexpected error occurred"
            )
          )

        // no data
        case reg =>
          Ok(Json.toJson(noDataModel()))
      }

    }
  }

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
