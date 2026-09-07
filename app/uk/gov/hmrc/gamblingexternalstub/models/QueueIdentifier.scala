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

import play.api.libs.json.*

/** The fixed set of queues the real activemq-proxy accepts. Mirrored here so the stub validates queueIdentifier exactly as the real service does. The
  * case name is the physical queue name.
  */
enum QueueIdentifier {
  case AGENT_Filing_RCLQ
  case AGENT_Filing_UORQ
  case SS_Filing_TrackingQ
  case NOVA_AuditShipping_IncomingQ
  case AGENT_Filing_APRQ
  case AGENT_Filing_AARQ
}

object QueueIdentifier {

  extension (queue: QueueIdentifier) def queueName: String = queue.toString

  def fromString(value: String): Option[QueueIdentifier] =
    QueueIdentifier.values.find(_.toString == value)

  private val permitted: String = QueueIdentifier.values.map(_.toString).mkString(", ")

  given Reads[QueueIdentifier] = Reads {
    case JsString(value) =>
      fromString(value) match {
        case Some(queue) => JsSuccess(queue)
        case None        => JsError(s"'$value' is not a recognised queueIdentifier. Must be one of: $permitted")
      }
    case other =>
      JsError(s"queueIdentifier must be a string, but was: $other")
  }

  given Writes[QueueIdentifier] = Writes(queue => JsString(queue.toString))
}
