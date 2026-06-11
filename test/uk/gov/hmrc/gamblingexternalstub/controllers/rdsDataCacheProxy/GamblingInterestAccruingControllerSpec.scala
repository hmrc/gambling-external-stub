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
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class GamblingInterestAccruingControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingInterestAccruingController]

  // Reg number convention: last 3 digits = HTTP status, 4th+5th from right = 2-digit record count
  // e.g. XWM00003100404 (404), XWM00003100500 (500), XWM00003103200 (200, 3 records), XWM00003150200 (200, 50 records)
  "GamblingInterestAccruingController#getInterestAccruingDrilldown" should {

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = controller.getInterestAccruingDrilldown("INVALID", "XWM00003103200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getInterestAccruingDrilldown(regime, "XWM00003100200", "INT-001", 1, 10)(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003100400", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003100401", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003100404", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No interest accruing drilldown found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003100500", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return 0 records for XWM00003100200 (last 3 = 200, 4th+5th from right = 00)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003100200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 0
      (json \ "total").as[BigDecimal]           shouldBe BigDecimal(0)
      (json \ "items").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200 (last 3 = 200, 4th+5th from right = 03)" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003103200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 3
      (json \ "items").as[JsArray].value.length shouldBe 3
      (json \ "total").as[BigDecimal]           shouldBe BigDecimal(601.65)
    }

    "return correct item fields for first record" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003103200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val firstItem = (json \ "items")(0)
      (firstItem \ "interestOn").as[BigDecimal] shouldBe BigDecimal(1000)
      (firstItem \ "noOfDays").as[BigDecimal]   shouldBe BigDecimal(30)
      (firstItem \ "rate").as[BigDecimal]       shouldBe BigDecimal(2.6)
      (firstItem \ "amount").as[BigDecimal]     shouldBe BigDecimal(100.55)
    }

    "return first page for XWM00003109200 (9 records) with pageNo=1 pageSize=5" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003109200", "INT-001", 1, 5)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 9
      (json \ "items").as[JsArray].value.length shouldBe 5
    }

    "return second page for XWM00003109200 (9 records) with pageNo=2 pageSize=5" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003109200", "INT-001", 2, 5)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 9
      (json \ "items").as[JsArray].value.length shouldBe 4
    }

    "return 50 total records for XWM00003150200 with pageNo=1 pageSize=10" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003150200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 50
      (json \ "items").as[JsArray].value.length shouldBe 10
    }

    "return last page for XWM00003150200 with pageNo=5 pageSize=10" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003150200", "INT-001", 5, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "totalRecords").as[Int]           shouldBe 50
      (json \ "items").as[JsArray].value.length shouldBe 10
    }

    "return total reflecting all records regardless of page" in {
      val page1 = controller.getInterestAccruingDrilldown("MGD", "XWM00003150200", "INT-001", 1, 10)(FakeRequest())
      val page2 = controller.getInterestAccruingDrilldown("MGD", "XWM00003150200", "INT-001", 2, 10)(FakeRequest())

      (contentAsJson(page1) \ "total").as[BigDecimal] shouldBe (contentAsJson(page2) \ "total").as[BigDecimal]
    }

    "include periodStartDate and periodEndDate in response" in {
      val result = controller.getInterestAccruingDrilldown("MGD", "XWM00003103200", "INT-001", 1, 10)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "periodStartDate").asOpt[String] shouldBe defined
      (json \ "periodEndDate").asOpt[String]   shouldBe defined
    }

    "return the same result for different interestId values" in {
      val result1 = controller.getInterestAccruingDrilldown("MGD", "XWM00003103200", "INT-001", 1, 10)(FakeRequest())
      val result2 = controller.getInterestAccruingDrilldown("MGD", "XWM00003103200", "INT-999", 1, 10)(FakeRequest())

      status(result1)                                   shouldBe OK
      status(result2)                                   shouldBe OK
      (contentAsJson(result1) \ "totalRecords").as[Int] shouldBe (contentAsJson(result2) \ "totalRecords").as[Int]
    }
  }
}
