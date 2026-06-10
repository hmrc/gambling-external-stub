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

class GamblingStatementOverviewControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingStatementOverviewController]

  //  7th from right = overview variant (0 = all zeros, 1 = total > 0, 2 = total < 0)
  "GamblingStatementOverviewController#getStatementOverview" should {

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = controller.getStatementOverview("INVALID", "XWM001001200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getStatementOverview(regime, "XWM001001200")(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for last 3 digits = 400" in {
      val result = controller.getStatementOverview("GBD", "XWM001001400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for last 3 digits = 401" in {
      val result = controller.getStatementOverview("GBD", "XWM001001401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for last 3 digits = 404" in {
      val result = controller.getStatementOverview("GBD", "XWM001001404")(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No statement overview found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for last 3 digits = 500" in {
      val result = controller.getStatementOverview("GBD", "XWM001001500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return 200 with all zero fields when 7th digit from right = 0 (variant=0, seed=1)" in {
      val result = controller.getStatementOverview("GBD", "XWM000001200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "total").as[BigDecimal]            shouldBe BigDecimal(0)
      (json \ "balance").as[BigDecimal]          shouldBe BigDecimal(0)
      (json \ "amountDeclared").as[BigDecimal]   shouldBe BigDecimal(0)
      (json \ "assessments").as[BigDecimal]      shouldBe BigDecimal(0)
      (json \ "penalties").as[BigDecimal]        shouldBe BigDecimal(0)
      (json \ "adjustments").as[BigDecimal]      shouldBe BigDecimal(0)
      (json \ "reallocations").as[BigDecimal]    shouldBe BigDecimal(0)
      (json \ "otherAssessments").as[BigDecimal] shouldBe BigDecimal(0)
      (json \ "interest").as[BigDecimal]         shouldBe BigDecimal(0)
      (json \ "payments").as[BigDecimal]         shouldBe BigDecimal(0)
      (json \ "repayments").as[BigDecimal]       shouldBe BigDecimal(0)
      (json \ "gtrPeriodStartDate").isDefined    shouldBe true
      (json \ "gtrPeriodEndDate").isDefined      shouldBe true
    }

    "return 200 with total > 0 when 7th digit from right = 1 (variant=1, seed=1)" in {
      val result = controller.getStatementOverview("GBD", "XWM001001200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "amountDeclared").as[BigDecimal]   shouldBe BigDecimal(1000)
      (json \ "assessments").as[BigDecimal]      shouldBe BigDecimal(50)
      (json \ "penalties").as[BigDecimal]        shouldBe BigDecimal(25)
      (json \ "adjustments").as[BigDecimal]      shouldBe BigDecimal(10)
      (json \ "reallocations").as[BigDecimal]    shouldBe BigDecimal(5)
      (json \ "otherAssessments").as[BigDecimal] shouldBe BigDecimal(15)
      (json \ "interest").as[BigDecimal]         shouldBe BigDecimal(8)
      (json \ "payments").as[BigDecimal]         shouldBe BigDecimal(200)
      (json \ "repayments").as[BigDecimal]       shouldBe BigDecimal(100)
      (json \ "total").as[BigDecimal]            shouldBe BigDecimal(813)
      (json \ "balance").as[BigDecimal]          shouldBe BigDecimal(613)
      (json \ "gtrPeriodStartDate").isDefined    shouldBe true
      (json \ "gtrPeriodEndDate").isDefined      shouldBe true
    }

    "return 200 with total < 0 when 7th digit from right = 2 (variant=2, seed=1)" in {
      val result = controller.getStatementOverview("GBD", "XWM002001200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "amountDeclared").as[BigDecimal]   shouldBe BigDecimal(1000)
      (json \ "assessments").as[BigDecimal]      shouldBe BigDecimal(50)
      (json \ "penalties").as[BigDecimal]        shouldBe BigDecimal(25)
      (json \ "adjustments").as[BigDecimal]      shouldBe BigDecimal(10)
      (json \ "reallocations").as[BigDecimal]    shouldBe BigDecimal(5)
      (json \ "otherAssessments").as[BigDecimal] shouldBe BigDecimal(15)
      (json \ "interest").as[BigDecimal]         shouldBe BigDecimal(8)
      (json \ "payments").as[BigDecimal]         shouldBe BigDecimal(2000)
      (json \ "repayments").as[BigDecimal]       shouldBe BigDecimal(100)
      (json \ "total").as[BigDecimal]            shouldBe BigDecimal(-987)
      (json \ "balance").as[BigDecimal]          shouldBe BigDecimal(-2987)
      (json \ "gtrPeriodStartDate").isDefined    shouldBe true
      (json \ "gtrPeriodEndDate").isDefined      shouldBe true
    }

    "return 200 with correct fields for seed=3 variant=1" in {
      val result = controller.getStatementOverview("MGD", "XWM001003200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "amountDeclared").as[BigDecimal] shouldBe BigDecimal(3000)
      (json \ "assessments").as[BigDecimal]    shouldBe BigDecimal(150)
      (json \ "penalties").as[BigDecimal]      shouldBe BigDecimal(75)
      (json \ "payments").as[BigDecimal]       shouldBe BigDecimal(600)
      (json \ "repayments").as[BigDecimal]     shouldBe BigDecimal(300)
    }
  }
}
