/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.gamblingexternalstub.controllers.clientExchangeProxy

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.actions.AuthAction
import uk.gov.hmrc.gamblingexternalstub.controllers.BaseController
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.xml.Elem

@Singleton()
class ClientExchangeProxyController @Inject() (
  authorise: AuthAction,
  cc: ControllerComponents
)() extends BackendController(cc)
    with Logging
    with BaseController {

  def updateClientList(serviceId: String, credentialId: String, agentId: String): Action[AnyContent] = authorise { implicit request =>

    val responseXML: Elem =
      <gwe:AsynchronousProcessWaitTime browserInterval="8000" xmlns:gwe="gwe">
          <BusinessServiceInterval>1000</BusinessServiceInterval>
          <BusinessServiceInterval>2000</BusinessServiceInterval>
        </gwe:AsynchronousProcessWaitTime>

    val result =
      if (!List("MGD", "GTR_GBD", "GTR_PBD", "GTR_RGD").contains(serviceId.toUpperCase)) {
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_REGIME",
            "message" -> "Invalid Regime Code"
          )
        )
      } else if (credentialId.trim().isEmpty) {
        BadRequest(Json.obj("error" -> "credentialId must be provided"))
      } else {
        logger.info(s"ClientExchangeProxy updateClientList: agentReference is $agentId")
        agentId.takeRight(3) match {
          case "400" => BadRequest(Json.obj("error" -> "Invalid ServiceId"))
          case "500" => InternalServerError(Json.obj("error" -> "Server Error"))
          case ""    => BadRequest(Json.obj("error" -> "agentReference not provided"))
          case _     => Ok(responseXML)
        }
      }

    logResult(s"ClientExchangeProxy updateClientList for $agentId returns ", result)
    result
  }
}
