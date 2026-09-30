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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.actions.AuthAction
import uk.gov.hmrc.gamblingexternalstub.utils.EnrolmentsHelper
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.xml.Elem

@Singleton()
class ClientExchangeProxyController @Inject() (
  authorise: AuthAction,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)() extends BackendController(cc) {

  def updateClientList(serviceId: String, credentialId: String, agentId: String): Action[AnyContent] = authorise { implicit request =>

    val responseXML: Elem =
      <gwe:AsynchronousProcessWaitTime browserInterval="8000" xmlns:gwe="gwe">
          <BusinessServiceInterval>1000</BusinessServiceInterval>
          <BusinessServiceInterval>2000</BusinessServiceInterval>
        </gwe:AsynchronousProcessWaitTime>

    val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
    identifier match {
      case Some(agentReference) =>
        agentReference.takeRight(3).toIntOption.getOrElse(200) match {
          case 400 => BadRequest(Json.obj("error" -> "Invalid ServiceId"))
          case 500 => InternalServerError(Json.obj("error" -> "Server Error"))
          case _   => Ok(responseXML)
        }
      case None => InternalServerError(Json.obj("error" -> "Server Error"))
    }
  }
}
