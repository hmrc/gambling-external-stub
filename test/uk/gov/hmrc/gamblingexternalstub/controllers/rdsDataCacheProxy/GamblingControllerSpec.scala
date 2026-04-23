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
import uk.gov.hmrc.gamblingexternalstub.models.{MgdCertificate, ReturnPeriodEndDate, ReturnSummary}

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
        ReturnSummary("XGM00000001762", 1, 0)
      )
    }

    "return both returnsDue and returnsOverDue for XGM00000001763" in {
      val result = controller.getReturnSummary("XGM00000001763")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary("XGM00000001763", 1, 2)
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

  "GamblingController#getMgdCertificate" should {

    "return OK for XGM00000001761" in {
      val result = controller.getMgdCertificate("XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        MgdCertificate.sample1("XGM00000001761")
      )
    }

    "return OK for XGM00000001762" in {
      val result = controller.getMgdCertificate("XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        MgdCertificate(
          mgdRegNumber       = "XGM00000001762",
          registrationDate   = Some(LocalDate.parse("2022-10-05")),
          individualName     = None,
          businessName       = Some("Example Sole Trader"),
          tradingName        = None,
          repMemName         = None,
          busAddrLine1       = Some("10 Market Road"),
          busAddrLine2       = Some("Gateshead"),
          busAddrLine3       = None,
          busAddrLine4       = None,
          busPostcode        = Some("NE8 1ZZ"),
          busCountry         = Some("United Kingdom"),
          busAdi             = None,
          repMemLine1        = None,
          repMemLine2        = None,
          repMemLine3        = None,
          repMemLine4        = None,
          repMemPostcode     = None,
          repMemAdi          = None,
          typeOfBusiness     = Some("Sole proprietor"),
          businessTradeClass = Some(1),

          // FIXED: Option[Int]
          noOfPartners   = Some(0),
          groupReg       = "N",
          noOfGroupMems  = Some(0),
          dateCertIssued = Some(LocalDate.parse("2024-01-10")),
          partMembers    = Seq.empty,
          groupMembers   = Seq.empty,
          returnPeriodEndDates = Seq(
            ReturnPeriodEndDate(LocalDate.parse("2026-03-31")),
            ReturnPeriodEndDate(LocalDate.parse("2026-06-30"))
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
          businessName       = Some("Business for GAM9999999999"),
          tradingName        = None,
          repMemName         = None,
          busAddrLine1       = Some("Unknown Address Line 1"),
          busAddrLine2       = Some("Unknown Address Line 2"),
          busAddrLine3       = None,
          busAddrLine4       = None,
          busPostcode        = Some("AA1 1AA"),
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
          noOfPartners       = Some(0),
          groupReg           = "N",
          noOfGroupMems      = Some(0),
          dateCertIssued     = Some(LocalDate.parse("2024-01-01")),
          partMembers        = Seq.empty,
          groupMembers       = Seq.empty,
          returnPeriodEndDates = Seq(
            ReturnPeriodEndDate(LocalDate.parse("2026-03-31"))
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
}
