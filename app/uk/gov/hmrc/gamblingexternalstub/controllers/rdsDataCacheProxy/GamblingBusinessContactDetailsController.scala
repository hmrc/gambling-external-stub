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
import uk.gov.hmrc.gamblingexternalstub.models.BusinessContactDetails
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject

class GamblingBusinessContactDetailsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def getBusinessContactDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {
      // Overdue
      case "XGM00000001761" | "GAM0000000001" =>
        Ok(Json.toJson(businessContactDetails(mgdRegNumber)))

      // Returns Due
      case "XGM00000001762" | "GAM0000000010" =>
        Ok(Json.toJson(businessContactDetails(mgdRegNumber)))

      // Both returns due and overdue exists
      case "XGM00000001763" | "GAM0000000012" =>
        Ok(Json.toJson(businessContactDetails(mgdRegNumber)))

      case "XGM00000000200" =>
        Ok(Json.toJson(BusinessContactDetails.noData))

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

      case "XGM00000000404" =>
        NotFound(
          Json.obj(
            "code"    -> "NOT_FOUND",
            "message" -> "No business contact details found for the given registration number"
          )
        )

      case "XGM00000000500" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case _ =>
        Ok(Json.toJson(businessContactDetails(mgdRegNumber)))
    }
  }

  private def businessContactDetails(mgdRegNumber: String): BusinessContactDetails =
    BusinessContactDetails(
      mgdRegNumber      = mgdRegNumber,
      phoneNumber       = "07700900999",
      mobilePhoneNumber = "07700900999",
      faxNumber         = "07700900999",
      emailAddr         = "viv@xyz.com",
      systemDate        = LocalDate.now().toString
    )
}
