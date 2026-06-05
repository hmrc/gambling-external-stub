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
import uk.gov.hmrc.gamblingexternalstub.models.ActualRepaymentItem

class GamblingRepaymentsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingRepaymentsController]

  // Reg number convention: last 3 digits = HTTP status, 4th+5th from right = 2-digit record count
  // e.g. XWM00003100404 (404), XWM00003100500 (500), XWM00003103200 (200, 3 records), XWM00003150200 (200, 50 records)
  "GamblingRepaymentsController#getRepaymentsSummary" should {

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = controller.getRepaymentsSummary("INVALID", "XWM00003103200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getRepaymentsSummary(regime, "XWM00003100200")(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003100400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003100401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003100404")(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No repayments found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003100500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return correct totalRecords for XWM00003003200 (actualRepayments = 3 records)  (repaymentsInterestRepaid = 3 records) 6th from last = 0"in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003003200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "actualRepaymentsAmount").as[BigDecimal]         shouldBe BigDecimal(600.69)
      (json \ "repaymentsInterestRepaidAmount").as[BigDecimal] shouldBe BigDecimal(-600.33)
      (json \ "total").as[BigDecimal]                          shouldBe BigDecimal(0.36)
    }

    "return correct totalRecords for XWM00003103200 (actualRepayments = 0 records)  (repaymentsInterestRepaid = 3 records)6th from last = 1" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003103200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "actualRepaymentsAmount").as[BigDecimal] shouldBe BigDecimal(0.00)
      (json \ "repaymentsInterestRepaidAmount").as[BigDecimal] shouldBe BigDecimal(-600.33)
      (json \ "total").as[BigDecimal] shouldBe BigDecimal(-600.33)
    }

    "return correct totalRecords for XWM00003203200 (actualRepayments = 3 records)  (repaymentsInterestRepaid = 0 records) 6th from last = 2" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003203200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "actualRepaymentsAmount").as[BigDecimal] shouldBe BigDecimal(600.69)
      (json \ "repaymentsInterestRepaidAmount").as[BigDecimal] shouldBe BigDecimal(0.00)
      (json \ "total").as[BigDecimal] shouldBe BigDecimal(600.69)
    }

    "return correct totalRecords for XWM00003303200 (actualRepayments = 0 records)  (repaymentsInterestRepaid = 0 records) 6th from last = 3" in {
      val result = controller.getRepaymentsSummary("MGD", "XWM00003303200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "actualRepaymentsAmount").as[BigDecimal] shouldBe BigDecimal(0.00)
      (json \ "repaymentsInterestRepaidAmount").as[BigDecimal] shouldBe BigDecimal(0.00)
      (json \ "total").as[BigDecimal] shouldBe BigDecimal(0.00)
    }
  }

  "GamblingRepaymentsController#getActualRepayments" should {

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = controller.getActualRepayments("INVALID", "XWM00003103200", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getActualRepayments(regime, "XWM00003100200", 1, 10)(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003100400", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003100401", 1, 10)(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003100404", 1, 10)(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No repayments found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003100500", 1, 10)(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return correct totalRecords for XWM00003103200 (3 records)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003103200", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]                    shouldBe 3
      (json \ "total").as[BigDecimal]                    shouldBe BigDecimal(600.69)
      (json \ "items").as[Seq[ActualRepaymentItem]].size shouldBe 3
    }

    "return correct totalRecords for XWM00003100200 (0 records)" in {
      val result = controller.getActualRepayments("MGD", "XWM00003100200", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]               shouldBe 0
      (json \ "total").as[BigDecimal]               shouldBe BigDecimal(0)
      (json \ "items").as[Seq[ActualRepaymentItem]] shouldBe empty
    }
  }
}
