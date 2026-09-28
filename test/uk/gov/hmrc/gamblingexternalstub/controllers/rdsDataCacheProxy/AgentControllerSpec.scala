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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBaseWithAuth
import uk.gov.hmrc.gamblingexternalstub.models.agent.{AgentClient, AgentClientListResponse}
import uk.gov.hmrc.gamblingexternalstub.utils.{EnrolmentsHelper, ResourceHelper}

import scala.concurrent.Future

class AgentControllerSpec extends SpecBaseWithAuth with MockitoSugar {

  ".getClientListDownloadStatus" should {

    "accept all valid regimes (case-insensitive)" in new Setup {
      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("InDown"))

      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val req: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(GET, s"/client-list-status?credentialId=cred-123&regime=$regime&gracePeriod=14400")
        val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", regime)(req)

        status(res) mustBe OK
        contentType(res) mustBe Some(JSON)
        (contentAsJson(res) \ "status").as[String] mustBe "InitiateDownload"
      }
    }

    "returns 200 with status 'InitiateDownload' when agentReference = InDown" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("InDown"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "MGD")(req)

      status(res) mustBe OK
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "status").as[String] mustBe "InitiateDownload"
    }

    "returns 200 with status 'InProgress' when agentReference = InProg" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("InProg"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "MGD")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "InProgress"
    }

    "returns 200 with status 'Succeeded' when agentReference = Success" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("Success"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "MGD")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "Succeeded"
    }

    "returns 200 with status 'Failed' when agentReference = Failed" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("Failed"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "MGD")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "status").as[String] mustBe "Failed"
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "MGD")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Could not map client list download status"
    }

    "returns 400 when credentialId is empty" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=&regime=MGD&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("", "MGD")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and regime must be provided"
    }

    "returns 400 when regime is empty" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list-status?credentialId=cred-123&regime=&gracePeriod=14400")
      val res: Future[Result] = controller.getClientListDownloadStatus("cred-123", "")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and regime must be provided"
    }
  }

  ".getAllClients" should {

    "accept all valid regimes (case-insensitive)" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("200"))

      when(mockResourceHelper.resourceAsString(any())).thenReturn(Json.toJson(clientList).toString)

      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val req: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(GET, s"/client-list?credentialId=CRED-ABC-123&regime=$regime")
        val res: Future[Result] = controller.getAllClients(credentialId = "CRED-ABC-123", regime = regime)(req)

        status(res) mustBe OK
        contentAsJson(res) mustBe Json.toJson(clientList)
      }
    }

    "returns 200 with client list when agentReference is 200" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("200"))

      when(mockResourceHelper.resourceAsString(any())).thenReturn(Json.toJson(clientList).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?credentialId=CRED-ABC-123&regime=MGD")
      val res: Future[Result] = controller.getAllClients(credentialId = "CRED-ABC-123", regime = "MGD")(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(clientList)
    }

    "returns 200 with client list when agentReference is 000123" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("000123"))

      when(mockResourceHelper.resourceAsString(any())).thenReturn(Json.toJson(clientList).toString)

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?credentialId=CRED-ABC-123&regime=MGD")
      val res: Future[Result] = controller.getAllClients(credentialId = "CRED-ABC-123", regime = "MGD")(req)

      status(res) mustBe OK
      contentAsJson(res) mustBe Json.toJson(clientList)
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?credentialId=CRED-ABC-123&regime=MGD")
      val res: Future[Result] = controller.getAllClients(credentialId = "CRED-ABC-123", regime = "MGD")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Could not get client list"
    }

    "returns 400 when regime is missing" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?credentialId=CRED-ABC-123&regime=")
      val res: Future[Result] = controller.getAllClients(credentialId = "CRED-ABC-123", regime = "")(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and regime must be provided"
    }

    "returns 400 when credentialId is missing" in new Setup {

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/client-list?credentialId=&regime=MGD")
      val res: Future[Result] = controller.getAllClients(credentialId = "", regime = "MGD")(req)

      status(res) mustBe BAD_REQUEST
      (contentAsJson(res) \ "error").as[String] mustBe "credentialId and regime must be provided"
    }

  }

  ".hasClient" should {

    "accept all valid regimes (case-insensitive)" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("agent-ref-123"))

      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val req: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(
            GET,
            s"/has-client/$regime/XWM00000001770?credentialId=CRED-ABC-123"
          )
        val res: Future[Result] = controller.hasClient(regime, "XWM00000001770", "CRED-ABC-123")(req)

        status(res) mustBe OK
        (contentAsJson(res) \ "hasClient").as[Boolean] mustBe true
      }
    }

    "returns 200 with hasClient true when regime is 'MGD' and regNumber is in allowed list" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("agent-ref-123"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(
          GET,
          "/has-client/MGD/XWM00000001770?credentialId=CRED-ABC-123"
        )
      val res: Future[Result] = controller.hasClient("MGD", "XWM00000001770", "CRED-ABC-123")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "hasClient").as[Boolean] mustBe true
    }

    "returns 200 with hasClient true when regime is not 'MGD'" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("agent-ref-123"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(
          GET,
          "/has-client/GBD/XMM00003101200?credentialId=CRED-ABC-123"
        )
      val res: Future[Result] = controller.hasClient("GBD", "XMM00003101200", "CRED-ABC-123")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "hasClient").as[Boolean] mustBe true
    }

    "returns 200 with hasClient false when no match conditions are met" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("agent-ref-123"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(
          GET,
          "/has-client/MGD/XMM01234567890?credentialId=CRED-ABC-123"
        )
      val res: Future[Result] = controller.hasClient("MGD", "XMM01234567890", "CRED-ABC-123")(req)

      status(res) mustBe OK
      (contentAsJson(res) \ "hasClient").as[Boolean] mustBe false
    }

    "returns 400 with error message when agentReference = 400" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("400"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/has-client/MGD/XWM00000001770?credentialId=CRED-ABC-123")
      val res: Future[Result] = controller.hasClient("MGD", "XWM00000001770", "CRED-ABC-123")(req)

      status(res) mustBe BAD_REQUEST
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "regime, regNumber and credentialId must be provided"
    }

    "returns 500 with error message when agentReference = 500" in new Setup {

      when(mockEnrolmentsHelper.agentEnrolmentsOpt(any())).thenReturn(Some("500"))

      val req: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, "/has-client/MGD/XWM00000001770?credentialId=CRED-ABC-123")
      val res: Future[Result] = controller.hasClient("MGD", "XWM00000001770", "CRED-ABC-123")(req)

      status(res) mustBe INTERNAL_SERVER_ERROR
      contentType(res) mustBe Some(JSON)
      (contentAsJson(res) \ "error").as[String] mustBe "Could not check hasClient"
    }
  }

  private trait Setup {
    val mockEnrolmentsHelper: EnrolmentsHelper = mock[EnrolmentsHelper]
    val mockResourceHelper: ResourceHelper = mock[ResourceHelper]
    val controller = new AgentController(fakeAuthAction, mockResourceHelper, mockEnrolmentsHelper, cc)(using ec)

    val clientList: AgentClientListResponse =
      AgentClientListResponse(
        clients = List(
          AgentClient(
            regNumber   = "123",
            clientName  = "ABC Ltd",
            agentOwnRef = "123ABC"
          ),
          AgentClient(
            regNumber   = "456",
            clientName  = "XYZ Builders",
            agentOwnRef = "456XYZ"
          )
        ),
        totalCount                   = 2,
        clientNameStartingCharacters = List("A", "X")
      )
  }
}
