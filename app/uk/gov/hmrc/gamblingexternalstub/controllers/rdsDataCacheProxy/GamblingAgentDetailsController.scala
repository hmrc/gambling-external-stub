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
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

class GamblingAgentDetailsController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc) {

  def getAgentDetails(agentReference: String): Action[AnyContent] = Action { _ =>
    agentReference.trim match {
      case s if s.nonEmpty =>
        Ok(
          Json.obj(
            "businessName"      -> "Gambling company 1",
            "addressLine1"      -> "1",
            "addressLine2"      -> "Example street",
            "addressLine3"      -> "Town",
            "addressLine4"      -> "County",
            "postcode"          -> "SW1A 1AA",
            "country"           -> "United Kingdom",
            "abroadSignal"      -> "N",
            "phoneNumber"       -> "02079460000",
            "mobilePhoneNumber" -> "07700900999",
            "faxNumber"         -> "02079460123",
            "email"             -> "user@example.com"
          )
        )
      case _ =>
        InternalServerError(Json.obj("code" -> "UNEXPECTED_ERROR", "message" -> "Unexpected error occurred"))
    }
  }
}
