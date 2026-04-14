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
import scala.concurrent.ExecutionContext

class MgdController @Inject() (
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging {

  def getReturnSummary(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "" =>
        logger.warn("[MGD Stub] Missing mgdRegNumber")
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        logger.error("[MGD Stub] Forced unexpected error")
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      // Scenario 1
      case "GAM0000000001" =>
        Ok(
          Json.toJson(
            ReturnSummary(
              mgdRegNumber   = "GAM0000000001",
              returnsDue     = 0,
              returnsOverdue = 1
            )
          )
        )

      // Scenario 2
      case "GAM0000000002" =>
        Ok(
          Json.toJson(
            ReturnSummary(
              mgdRegNumber   = "GAM0000000002",
              returnsDue     = 0,
              returnsOverdue = 0
            )
          )
        )

      // Default fallback
      case reg =>
        logger.info(s"[MGD Stub] Default response for $reg")
        Ok(
          Json.toJson(
            ReturnSummary(
              mgdRegNumber   = reg,
              returnsDue     = 2,
              returnsOverdue = 1
            )
          )
        )
    }
  }
}
