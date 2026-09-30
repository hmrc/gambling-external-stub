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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBaseWithAuth
import uk.gov.hmrc.gamblingexternalstub.utils.EnrolmentsHelper

import scala.concurrent.Future

class ClientExchangeProxyControllerSpec extends SpecBaseWithAuth with MockitoSugar {

  ".updateClientList" should {

    "returns 200 with response when agentReference is 200" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any(), any())).thenReturn(Some("200"))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe OK
      contentType(res) mustBe Some("application/xml")
    }

    "returns 200 with response when agentReference is empty" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any(), any())).thenReturn(Some(""))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe OK
      contentType(res) mustBe Some("application/xml")
    }

    "returns 400 when agentReference = 400" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any(), any())).thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Invalid ServiceId"
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any(), any())).thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Server Error"
    }

    "returns 500 with error message when no agent enrolment found" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any(), any())).thenReturn(None)

      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "/GTR_GBD/credentialId/agentId/clientlist")
      val res: Future[Result] =
        controller.updateClientList(serviceId = "GTR_GBD", credentialId = "credentialId", agentId = "agentId")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Server Error"
    }

  }

  private trait Setup {
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]
    val controller = new ClientExchangeProxyController(fakeAuthAction, mockEnrolmentsHelper, cc)()
  }
}
