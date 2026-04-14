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
import uk.gov.hmrc.gamblingexternalstub.models.ReturnSummary
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class GamblingController @Inject() (
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  def getReturnSummary(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      // simulate service-level InvalidMgdRegNumber
      case "invalid" =>
        logger.warn("[Gambling Stub] Invalid MGD reg number")
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      // simulate service-level UnexpectedError
      case "error" =>
        logger.error("[Gambling Stub] Unexpected error")
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      // scenario 1
      case "GAM0000000001" =>
        Ok(Json.toJson(ReturnSummary("GAM0000000001", 0, 1)))

      // scenario 2
      case "GAM0000000002" =>
        Ok(Json.toJson(ReturnSummary("GAM0000000002", 0, 0)))

      // default
      case reg =>
        Ok(Json.toJson(ReturnSummary(reg, 2, 1)))
    }
  }
}
