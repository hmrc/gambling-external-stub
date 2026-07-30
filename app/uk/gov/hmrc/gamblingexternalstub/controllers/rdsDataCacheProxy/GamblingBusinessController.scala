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
import javax.inject.Inject

class GamblingBusinessController @Inject()(
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {


  def getBusinessAddressDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" => invalidResponse

      case "error" => errorResponse

      // Scenario 1 → Return Business Address Details
      case "XGM00000001761" | "GAM0000000001" =>
        Ok(
          Json.toJson(
            BusinessAddressDetails(
              mgdRegNumber,
              adi = Some("1st floor"),
              address1 = Some("address1"),
              address2 = Some("address2"),
              address3 =Some("address3"),
              address4 =Some("address4"),
              postcode =Some("L1 8YL"),
              country= Some("England"),
              iomOrCiFlag= Some(false),
              systemDate = Some(LocalDate.now())

            )
          )
        )

      // Scenario 2 → default
      case reg =>        Ok(
        Json.toJson(
          BusinessAddressDetails(
            mgdRegNumber,
            adi = Some(""),
            address1 = Some(""),
            address2 = Some(""),
            address3 = Some(""),
            address4 = Some(""),
            postcode = Some(""),
            country= Some(""),
            iomOrCiFlag= Some(false),
            systemDate = Some(LocalDate.now())

          )
        )
      )
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
