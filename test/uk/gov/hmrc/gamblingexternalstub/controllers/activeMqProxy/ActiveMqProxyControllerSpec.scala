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

package uk.gov.hmrc.gamblingexternalstub.controllers.activeMqProxy

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class ActiveMqProxyControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val url = "/activemq-proxy/queue/send"

  private val fullRequest: JsValue = Json.obj(
    "queueIdentifier" -> "AGENT_Filing_APRQ",
    "payload"         -> "<?xml version=\"1.0\"?><agentPinRequest/>",
    "properties" -> Json.arr(
      Json.obj("key" -> "MESSAGE_CLASS", "value" -> "HMRC-AGENT-APR"),
      Json.obj("key" -> "LOB", "value"           -> "Agent")
    ),
    "correlationId" -> "54947df80e9e4471a2f99af509fb5889"
  )

  private def post(body: JsValue) =
    route(app, FakeRequest(POST, url).withJsonBody(body)).get

  "POST /activemq-proxy/queue/send" should {

    "accept a full request and echo the supplied correlationId" in {
      val result = post(fullRequest)

      status(result)                                       shouldBe OK
      (contentAsJson(result) \ "correlationId").as[String] shouldBe "54947df80e9e4471a2f99af509fb5889"
    }

    "generate a 32-character correlationId when none is supplied" in {
      val result = post(fullRequest.as[JsObject] - "correlationId")

      status(result) shouldBe OK
      val generated = (contentAsJson(result) \ "correlationId").as[String]
      generated.length shouldBe 32
      generated          should fullyMatch regex "[0-9a-f]{32}"
    }

    "accept a minimal request with no properties" in {
      val body = Json.obj("queueIdentifier" -> "SS_Filing_TrackingQ", "payload" -> "<tracking/>")
      val result = post(body)

      status(result)                                              shouldBe OK
      (contentAsJson(result) \ "correlationId").as[String].length shouldBe 32
    }

    "reject an unknown queueIdentifier with 400" in {
      val result = post(fullRequest.as[JsObject] ++ Json.obj("queueIdentifier" -> "NOT_A_QUEUE"))

      status(result)                                 shouldBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] shouldBe "Invalid request body"
    }

    "reject a request with a missing payload with 400" in {
      val result = post(fullRequest.as[JsObject] - "payload")

      status(result) shouldBe BAD_REQUEST
    }

    "reject a correlationId that is not exactly 32 characters with 400" in {
      val result = post(fullRequest.as[JsObject] ++ Json.obj("correlationId" -> "too-short"))

      status(result)                               shouldBe BAD_REQUEST
      (contentAsJson(result) \ "message").as[String] should include("32 characters")
    }
  }
}
