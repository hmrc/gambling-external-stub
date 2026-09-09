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

import play.api.libs.json.*
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class GamblingFilingController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def updateStatusPeriod(
    regime: String,
    registrationNo: String,
    consecNo: Int
  ): Action[JsValue] = Action(parse.json) { request =>

    if (Regime.fromString(regime).isEmpty) {
      BadRequest(
        Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> s"regime must be one of: ${Regime.validCodes}"
        )
      )
    } else {
      request.body.validate[UpdateStatusPeriodRequest] match {
        case JsError(_) =>
          BadRequest(
            Json.obj(
              "code"    -> "INVALID_REQUEST",
              "message" -> "Invalid request body"
            )
          )

        case JsSuccess(_, _) =>
          registrationNo.takeRight(3).toIntOption.getOrElse(200) match {

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
                  "message" -> "No period found for the given registration number"
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
              NoContent
          }
      }
    }
  }
}
