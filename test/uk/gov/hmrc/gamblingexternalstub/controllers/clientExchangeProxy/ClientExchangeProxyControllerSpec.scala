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

import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBaseWithAuth

import scala.concurrent.Future

class ClientExchangeProxyControllerSpec extends SpecBaseWithAuth with MockitoSugar {

  ".updateClientList" should {

    "returns 200 with response when agentReference is 200" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe OK
      contentType(res) mustBe Some("application/xml")
    }

    "accept all valid regimes (case-insensitive)" in new Setup {

      Seq("MGD", "mgd", "GTR_GBD", "gtr_gbd", "GTR_PBD", "gtr_pbd", "GTR_RGD", "gtr_rgd").foreach { regime =>
        val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/$regime/credentialId/agentId/clientlist")
        val res: Future[Result] =
          controller.updateClientList(serviceId = regime, credentialId = "credentialId", agentId = "agentId")(req)

        status(res) mustBe OK
        contentType(res) mustBe Some("application/xml")
      }
    }

    "returns 400 when regime is INVALID" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/INVALID/credentialId/400/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "INVALID", credentialId = "credentialId", agentId = "400")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "code").as[String] mustBe "INVALID_REGIME"
      (contentAsJson(res) \ "message").as[String] mustBe "Invalid Regime Code"
    }

    "returns 400 when agentReference = 400" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/400/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "400")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Invalid ServiceId"
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/500/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "500")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Server Error"
    }

    "returns 400 with error message when no agent ref is provided" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId//clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "agentReference not provided"
    }

  }

  private trait Setup {
    val controller = new ClientExchangeProxyController(fakeAuthAction, cc)()
  }
}
