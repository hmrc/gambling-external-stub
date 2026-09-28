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

package uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.actions.AuthAction
import uk.gov.hmrc.gamblingexternalstub.models.agent.ClientListDownloadStatus.{Failed, InProgress, InitiateDownload, Succeeded}
import uk.gov.hmrc.gamblingexternalstub.utils.{EnrolmentsHelper, ResourceHelper}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AgentController @Inject() (
  authorise: AuthAction,
  resourceHelper: ResourceHelper,
  enrolmentHelper: EnrolmentsHelper,
  cc: ControllerComponents
)(using ExecutionContext)
    extends BackendController(cc)
    with Logging {

  private val responsePath = "/data/agent"
  private val getAllClients_200_ResponsePath = s"$responsePath/getAllClients-200-response.json"
  private val getAllClients_200_Alt_ResponsePath = s"$responsePath/getAllClients-200-alt-response.json"

  def getClientListDownloadStatus(
    credentialId: String,
    regime: String,
    gracePeriod: Int = 14400
  ): Action[AnyContent] = authorise.async { implicit request =>
    val result = if (regime.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and regime must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(agentReference) =>
          logger.info(s"getClientListDownloadStatus: agentReference is $agentReference")
          agentReference match {
            case "500"    => InternalServerError(Json.obj("error" -> "Could not map client list download status"))
            case "InDown" => Ok(Json.obj("status" -> InitiateDownload.toString))
            case "InProg" => Ok(Json.obj("status" -> InProgress.toString))
            case "Failed" => Ok(Json.obj("status" -> Failed.toString))
            case _        => Ok(Json.obj("status" -> Succeeded.toString))
          }
        case None => InternalServerError
      }
    }
    Future.successful(result)
  }

  def getAllClients(
    credentialId: String,
    regime: String,
    start: Int = 0,
    count: Int = -1,
    sort: Int = 0,
    ascending: Boolean = true
  ): Action[AnyContent] = authorise.async { implicit request =>
    val result = if (regime.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "credentialId and regime must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(agentReference) =>
          logger.info(s"getAllClients: agentReference is $agentReference")
          agentReference match {
            case "500"    => InternalServerError(Json.obj("error" -> "Could not get client list"))
            case "000123" => Ok(resourceHelper.resourceAsString(getAllClients_200_Alt_ResponsePath))
            case _        => Ok(resourceHelper.resourceAsString(getAllClients_200_ResponsePath))
          }
        case None => InternalServerError
      }
    }
    Future.successful(result)
  }

  def hasClient(
    regime: String,
    regNumber: String,
    credentialId: String
  ): Action[AnyContent] = authorise.async { implicit request =>
    val result = if (regime.trim().isEmpty || regNumber.trim().isEmpty || credentialId.trim().isEmpty) {
      BadRequest(Json.obj("error" -> "regime, regNumber and credentialId must be provided"))
    } else {
      val identifier = enrolmentHelper.agentEnrolmentsOpt(request)
      identifier match {
        case Some(agentReference) =>
          logger.info(s"hasClient: agentReference is $agentReference")
          agentReference match {
            case "400" => BadRequest(Json.obj("error" -> "regime, regNumber and credentialId must be provided"))
            case "500" => InternalServerError(Json.obj("error" -> "Could not check hasClient"))
            case "agent-ref-123" if regime.equals("MGD") && List("XWM00000001770", "XMM00000000975").contains(regNumber) =>
              Ok(Json.obj("hasClient" -> true))
            case _ if regime != "MGD" => Ok(Json.obj("hasClient" -> true))
            case _                    => Ok(Json.obj("hasClient" -> false))
          }
        case None => InternalServerError
      }
    }
    Future.successful(result)
  }
}
