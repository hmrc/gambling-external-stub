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

package uk.gov.hmrc.gamblingexternalstub.models.agent

import play.api.libs.json.{Json, OWrites}
import uk.gov.hmrc.gamblingexternalstub.models.agent.StatementError.InvalidClientListStatus

sealed trait ClientListDownloadStatus

object ClientListDownloadStatus {
  case object InitiateDownload extends ClientListDownloadStatus with WithName("InitiateDownload")
  case object InProgress       extends ClientListDownloadStatus with WithName("InProgress")
  case object Succeeded        extends ClientListDownloadStatus with WithName("Succeeded")
  case object Failed           extends ClientListDownloadStatus with WithName("Failed")

  def fromInt(status: Int): Either[StatementError, ClientListDownloadStatus] = status match {
    case -1 => Right(InitiateDownload)
    case 0  => Right(InProgress)
    case 1  => Right(Succeeded)
    case 2  => Right(Failed)
    case _  => Left(InvalidClientListStatus)
  }
}

enum StatementError(val code: String, val message: String) {
  case InvalidRegNumber  extends StatementError("INVALID_REG_NUMBER", "regNumber has invalid format")
  case UnexpectedError   extends StatementError("UNEXPECTED_ERROR", "Unexpected error occurred")
  case InvalidRegimeCode extends StatementError("INVALID_REGIME_CODE", "Invalid Regime Code")
  case StatementNotFound
      extends StatementError(
        "NOT_FOUND",
        "No statement overview found for the given registration number"
      )
  case InvalidStatus           extends StatementError("INVALID_STATUS", "status must be 0 (open) or 1 (closed)")
  case InvalidClientListStatus extends StatementError("INVALID_CLIENT_LIST_STATUS", "Could not map client list download status")
}

object StatementError {
  given OWrites[StatementError] = error => Json.obj("code" -> error.code, "message" -> error.message)
}

trait WithName(string: String) {
  override val toString: String = string
}
