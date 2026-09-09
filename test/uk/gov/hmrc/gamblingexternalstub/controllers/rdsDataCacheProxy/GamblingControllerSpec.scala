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

package uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.gamblingexternalstub.models.BusinessType.SoleProprietor

import java.time.LocalDate

class GamblingControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingController]

  "GamblingController#getReturnSummary" should {

    "return returnsOverDue for XGM00000001761" in {
      val result = controller.getReturnSummary("XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary("XGM00000001761", 0, 1)
      )
    }

    "return returnsDue for XGM00000001762" in {
      val result = controller.getReturnSummary("XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary("XGM00000001762", 1, 2)
      )
    }

    "return both returnsDue and returnsOverDue for XGM00000001763" in {
      val result = controller.getReturnSummary("XGM00000001763")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary("XGM00000001763", 0, 11)
      )
    }

    "return default response" in {
      val result = controller.getReturnSummary("GAM9999999999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary("GAM9999999999", 0, 0)
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getReturnSummary("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getReturnSummary("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }

  "GamblingController#getTradeClassDetails" should {

    "return trade class 1 for XGM00000001761" in {
      val result =
        controller.getTradeClassDetails("XGM00000001761")(FakeRequest())

      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.toJson(
        TradeClassDetails(
          mgdRegNumber         = "XGM00000001761",
          businessTradeClass   = Some(9),
          businessActivityDesc = "Others Business Activity",
          systemDate           = Some(LocalDate.parse("2026-06-02"))
        )
      )
    }

    "return trade class 2 for XGM00000001762" in {
      val result =
        controller.getTradeClassDetails("XGM00000001762")(FakeRequest())

      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.toJson(
        TradeClassDetails(
          mgdRegNumber         = "",
          businessTradeClass   = None,
          businessActivityDesc = "",
          systemDate           = None
        )
      )
    }

    "return empty response for XMM00000000993" in {
      val result =
        controller.getTradeClassDetails("XMM00000000993")(FakeRequest())

      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.toJson(
        TradeClassDetails(
          mgdRegNumber         = "",
          businessTradeClass   = None,
          businessActivityDesc = "",
          systemDate           = None
        )
      )
    }

    "return default response for unknown reg number" in {
      val result =
        controller.getTradeClassDetails("GAM9999999999")(FakeRequest())

      status(result) shouldBe OK

      contentAsJson(result) shouldBe Json.toJson(
        TradeClassDetails(
          mgdRegNumber         = "",
          businessTradeClass   = None,
          businessActivityDesc = "",
          systemDate           = None
        )
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result =
        controller.getTradeClassDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result =
        controller.getTradeClassDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }

  "GamblingController#getBusinessName" should {

    "return OK for XGM00000001761" in {
      val result = controller.getBusinessName("XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        BusinessName(
          "XGM00000001761",
          Some("Mr"),
          Some("SoleFN"),
          Some("MN"),
          Some("Proprietor LN"),
          Some("SP Business Name"),
          Some(1),
          Some("SP Trading Name"),
          Some(LocalDate.of(2026, 1, 1))
        )
      )
    }

    "return OK for XGM00000001762" in {
      val result = controller.getBusinessName("XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        BusinessName(
          "XGM00000001762",
          Some("Mrs"),
          Some("Corporate"),
          None,
          Some("Body"),
          Some("CB Business Name"),
          Some(2),
          Some("CB Trading Name"),
          Some(LocalDate.of(2026, 1, 1))
        )
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getBusinessName("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getBusinessName("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }

  "GamblingController#getBusinessDetails" should {

    "return OK for XGM00000001761" in {
      val result = controller.getBusinessDetails("XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        BusinessDetails(
          "XGM00000001761",
          Some(SoleProprietor),
          1,
          false,
          Some(LocalDate.of(1991, 1, 1)),
          Some("bar"),
          LocalDate.of(1991, 1, 1)
        )
      )
    }

    "return sole trader for XGM00000001762" in {
      val result = controller.getBusinessDetails("XGM00000001762")(FakeRequest())

      status(result)                                   shouldBe OK
      (contentAsJson(result) \ "businessType").as[Int] shouldBe 2
      (contentAsJson(result) \ "groupReg").as[Boolean] shouldBe false
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getBusinessDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getBusinessDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return default response" in {
      val result = controller.getBusinessDetails("GAM999")(FakeRequest())

      status(result)                                          shouldBe OK
      (contentAsJson(result) \ "currentlyRegistered").as[Int] shouldBe 1
    }

  }

  "GamblingController#getMgdCertificate" should {

//    "return OK for XGM00000001761" in {
//      val result = controller.getMgdCertificate("XGM00000001761")(FakeRequest())
//
//      status(result) shouldBe OK
//      contentAsJson(result) shouldBe Json.toJson(
//        MgdCertificate.sample1("XGM00000001761")
//      )
//    }

    "return OK for XGM00000001762" in {
      val result = controller.getMgdCertificate("XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        MgdCertificate(
          mgdRegNumber       = "XGM00000001762",
          registrationDate   = Some(LocalDate.parse("2022-02-02")),
          individualName     = Some("Mrs Corporate Body"),
          businessName       = Some("CB Business Name"),
          tradingName        = Some("CB Trading Name"),
          repMemName         = Some("Some Name"),
          busAddrLine1       = Some("2 Quicksilver Way"),
          busAddrLine2       = Some("Cobalt Business Park"),
          busAddrLine3       = None,
          busAddrLine4       = None,
          busPostcode        = Some("BB2 2BB"),
          busCountry         = Some("United Kingdom"),
          busAdi             = None,
          repMemLine1        = None,
          repMemLine2        = None,
          repMemLine3        = None,
          repMemLine4        = None,
          repMemPostcode     = None,
          repMemAdi          = None,
          typeOfBusiness     = Some("Corporate Body"),
          businessTradeClass = Some(2),

          // FIXED: Option[Int]
          noOfPartners   = Some(0),
          groupReg       = "N",
          noOfGroupMems  = Some(0),
          dateCertIssued = Some(LocalDate.parse("2022-02-02")),
          partMembers    = Seq.empty,
          groupMembers   = Seq.empty,
          returnPeriodEndDates = Seq(
            ReturnPeriodEndDate(LocalDate.parse("2026-09-30")),
            ReturnPeriodEndDate(LocalDate.parse("2026-12-31"))
          )
        )
      )
    }

    "return default response" in {
      val result = controller.getMgdCertificate("GAM9999999999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        MgdCertificate(
          mgdRegNumber       = "GAM9999999999",
          registrationDate   = Some(LocalDate.parse("2021-01-01")),
          individualName     = None,
          businessName       = Some("Default Business Name for GAM9999999999"),
          tradingName        = None,
          repMemName         = None,
          busAddrLine1       = Some("Default Address Line 1"),
          busAddrLine2       = Some("Default Address Line 2"),
          busAddrLine3       = None,
          busAddrLine4       = None,
          busPostcode        = None,
          busCountry         = Some("Ireland"),
          busAdi             = None,
          repMemLine1        = None,
          repMemLine2        = None,
          repMemLine3        = None,
          repMemLine4        = None,
          repMemPostcode     = None,
          repMemAdi          = None,
          typeOfBusiness     = Some("Default Corporate Body"),
          businessTradeClass = Some(2),
          noOfPartners       = Some(0),
          groupReg           = "N",
          noOfGroupMems      = Some(0),
          dateCertIssued     = Some(LocalDate.parse("2026-12-31")),
          partMembers        = Seq.empty,
          groupMembers       = Seq.empty,
          returnPeriodEndDates = Seq(
            ReturnPeriodEndDate(LocalDate.parse("2026-12-31"))
          )
        )
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getMgdCertificate("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getMgdCertificate("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }

  "GamblingController#getMgdDetails" should {

    "return multiple previous and associated registrations for XGM00000001761" in {
      val result = controller.getMgdDetails("XGM00000001761")(FakeRequest())
      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "mgdRegNumber").as[String]        shouldBe "XGM00000001761"
      (json \ "isBusinessSeasonal").as[Int]     shouldBe 1
      (json \ "previousMgdrn1").as[String]      shouldBe "XMM00000000448"
      (json \ "previousMgdrn2").asOpt[String]   shouldBe None
      (json \ "previousMgdrn3").asOpt[String]   shouldBe None
      (json \ "associatedMgdrn1").as[String]    shouldBe "XZM00000000469"
      (json \ "associatedMgdrn2").asOpt[String] shouldBe None
      (json \ "associatedMgdrn3").asOpt[String] shouldBe None
      (json \ "systemDate").as[String]          shouldBe "2026-06-06"
    }

    "return full linked history for default reg number (XGM00000001762)" in {
      val result = controller.getMgdDetails("XGM00000001762")(FakeRequest())
      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "mgdRegNumber").as[String]        shouldBe "XGM00000001762"
      (json \ "isBusinessSeasonal").asOpt[Int]  shouldBe None
      (json \ "previousMgdrn1").as[String]      shouldBe "XMM00000000448"
      (json \ "previousMgdrn2").as[String]      shouldBe "XBM00000000451"
      (json \ "previousMgdrn3").as[String]      shouldBe "XYM00000000466"
      (json \ "associatedMgdrn1").asOpt[String] shouldBe None
      (json \ "associatedMgdrn2").asOpt[String] shouldBe None
      (json \ "associatedMgdrn3").asOpt[String] shouldBe None
      (json \ "systemDate").as[String]          shouldBe "2026-06-06"
    }

    "return empty data response for XMM00000000993" in {
      val result = controller.getMgdDetails("XMM00000000993")(FakeRequest())
      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "mgdRegNumber").as[String]        shouldBe "XMM00000000993"
      (json \ "isBusinessSeasonal").asOpt[Int]  shouldBe None
      (json \ "previousMgdrn1").asOpt[String]   shouldBe None
      (json \ "previousMgdrn2").asOpt[String]   shouldBe None
      (json \ "previousMgdrn3").asOpt[String]   shouldBe None
      (json \ "associatedMgdrn1").asOpt[String] shouldBe None
      (json \ "associatedMgdrn2").asOpt[String] shouldBe None
      (json \ "associatedMgdrn3").asOpt[String] shouldBe None
    }

    "return BAD_REQUEST for invalid mgdRegNumber" in {
      val result = controller.getMgdDetails("invalid")(FakeRequest())
      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getMgdDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }

  "GamblingController#getOperatorDetails" should {

    "return corporate operator for XGM00000001761" in {
      val result = controller.getOperatorDetails("XGM00000001761")(FakeRequest())

      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "businessName").as[String] shouldBe "Acme Gaming Ltd"
      (contentAsJson(result) \ "tradingName").as[String]  shouldBe "Acme Bets"
      (contentAsJson(result) \ "businessType").as[Int]    shouldBe 2
    }

    "return sole proprietor for XGM00000001762" in {
      val result = controller.getOperatorDetails("XGM00000001762")(FakeRequest())

      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "solePropName").as[String] shouldBe "Jane Doe"
      (contentAsJson(result) \ "businessType").as[Int]    shouldBe 1
    }

    "return overseas operator for XGM00000001763" in {
      val result = controller.getOperatorDetails("XGM00000001763")(FakeRequest())

      status(result)                                   shouldBe OK
      (contentAsJson(result) \ "country").as[String]   shouldBe "Ireland"
      (contentAsJson(result) \ "abroadSig").as[String] shouldBe "Y"
    }

    "return partnership operator for XGM00000001764" in {
      val result = controller.getOperatorDetails("XGM00000001764")(FakeRequest())

      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "businessType").as[Int]    shouldBe 4
      (contentAsJson(result) \ "businessName").as[String] shouldBe "ABC Partnership"
    }

    "return default operator" in {
      val result = controller.getOperatorDetails("GAM999")(FakeRequest())

      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "businessName").as[String] shouldBe "Business for GAM999"
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getOperatorDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getOperatorDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "GamblingController#getCorrespondenceDetails" should {

    "return corporate operator for XGM00000001761" in {
      val result = controller.getCorrespondenceDetails("XGM00000001763")(FakeRequest())

      status(result)                                   shouldBe OK
      (contentAsJson(result) \ "nameLine1").as[String] shouldBe "Madrid"
    }

    "return default operator" in {
      val result = controller.getCorrespondenceDetails("GAM999")(FakeRequest())

      status(result)                                   shouldBe OK
      (contentAsJson(result) \ "nameLine1").as[String] shouldBe "Gateshead"
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getCorrespondenceDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getCorrespondenceDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

}
