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

package uk.gov.hmrc.gamblingexternalstub.models

import play.api.libs.json.{Json, Reads}

/** The inbound request to the stubbed activemq-proxy `POST /queue/send`. */
final case class SendMessageRequest(
  queueIdentifier: QueueIdentifier,
  payload: String,
  properties: Option[List[MessageProperty]],
  correlationId: Option[String]
) {
  def propertiesOrEmpty: List[MessageProperty] = properties.getOrElse(Nil)
}

object SendMessageRequest {
  given Reads[SendMessageRequest] = Json.reads[SendMessageRequest]
}
