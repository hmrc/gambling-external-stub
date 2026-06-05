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

class GamblingReallocationsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[StubController]

  "GamblingReallocationsController#getReallocationsDetails" should {

    "return INVALID_REQUEST for XWM00003100200 - 4th+5th digits are 00" in {
      val result = controller.getReallocationsDetails("mgd", "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "routeURL (reallocations-details) does not match requestType (00)(does not exist)"
      )
    }

    "return INVALID_REQUEST for XWM05003300200 - 4th+5th digits are 05, 6th from last is 3" in {
      val result = controller.getReallocationsDetails("mgd", "XWM05003300200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "routeURL (reallocations-details) has an invalid customisation (3)"
      )
    }

    "return 0.00  XWM05003100200" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003000200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "reallocationsInAmount").as[BigDecimal]  shouldBe 0.00
      (json \ "reallocationsOutAmount").as[BigDecimal] shouldBe 0.00
      (json \ "total").as[BigDecimal]                  shouldBe 0.00
    }

    "return 3 records for XWM05003103200" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003103200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "totalRecords").as[Int]           shouldBe 3
      (json \ "items").as[JsArray].value.length shouldBe 3
    }

    "return first page for XWM05003109200 with pageNo=1 pageSize=5" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003109200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "totalRecords").as[Int]           shouldBe 9
      (json \ "items").as[JsArray].value.length shouldBe 5
    }

    "return second page for XWM05003109200 with pageNo=2 pageSize=5" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003109200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "totalRecords").as[Int]           shouldBe 9
      (json \ "items").as[JsArray].value.length shouldBe 4
    }

    "return 50 total records for XWM05003150200 with pageNo=1 pageSize=10" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003150200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "totalRecords").as[Int]           shouldBe 50
      (json \ "items").as[JsArray].value.length shouldBe 10
    }

    "return last page for XWM05003150200 with pageNo=5 pageSize=10" in {
      val result = controller.getReallocationsDetails("MGD", "XWM05003150200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)

      (json \ "totalRecords").as[Int]           shouldBe 50
      (json \ "items").as[JsArray].value.length shouldBe 10
    }
  }

//  "GamblingReallocationsController#getReallocations IN" should {
//
//    "return 0 records for XWM05003100200" in {
//      val result = controller.getReallocations("XWM05003100200", 1, 10, 0, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 0
//      (json \ "items").as[JsArray].value.length shouldBe 0
//    }
//
//    "return 3 records for XWM05003103200" in {
//      val result = controller.getReallocations("XWM05003103200", 1, 10, 3, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 3
//      (json \ "items").as[JsArray].value.length shouldBe 3
//    }
//
//    "return first page for XWM05003109200 with pageNo=1 pageSize=5" in {
//      val result = controller.getReallocations("XWM05003109200", 1, 5, 9, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 9
//      (json \ "items").as[JsArray].value.length shouldBe 5
//    }
//
//    "return second page for XWM05003109200 with pageNo=2 pageSize=5" in {
//      val result = controller.getReallocations("XWM05003109200", 2, 5, 9, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 9
//      (json \ "items").as[JsArray].value.length shouldBe 4
//    }
//
//    "return 50 total records for XWM05003150200 with pageNo=1 pageSize=10" in {
//      val result = controller.getReallocations("XWM05003150200", 1, 10, 50, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 50
//      (json \ "items").as[JsArray].value.length shouldBe 10
//    }
//
//    "return last page for XWM05003150200 with pageNo=5 pageSize=10" in {
//      val result = controller.getReallocations("XWM05003150200", 5, 10, 50, 1)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 50
//      (json \ "items").as[JsArray].value.length shouldBe 10
//    }
//  }
//
//  "GamblingReallocationsController#getReallocations OUT" should {
//
//    "return 0 records for XWM05003100200" in {
//      val result = controller.getReallocations("XWM05003100200", 1, 10, 0, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 0
//      (json \ "items").as[JsArray].value.length shouldBe 0
//    }
//
//    "return 3 records for XWM05003103200" in {
//      val result = controller.getReallocations("XWM05003103200", 1, 10, 3, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 3
//      (json \ "items").as[JsArray].value.length shouldBe 3
//    }
//
//    "return first page for XWM05003109200 with pageNo=1 pageSize=5" in {
//      val result = controller.getReallocations("XWM05003109200", 1, 5, 9, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 9
//      (json \ "items").as[JsArray].value.length shouldBe 5
//    }
//
//    "return second page for XWM05003109200 with pageNo=2 pageSize=5" in {
//      val result = controller.getReallocations("XWM05003109200", 2, 5, 9, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 9
//      (json \ "items").as[JsArray].value.length shouldBe 4
//    }
//
//    "return 50 total records for XWM05003150200 with pageNo=1 pageSize=10" in {
//      val result = controller.getReallocations("XWM05003150200", 1, 10, 50, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 50
//      (json \ "items").as[JsArray].value.length shouldBe 10
//    }
//
//    "return last page for XWM05003150200 with pageNo=5 pageSize=10" in {
//      val result = controller.getReallocations("XWM05003150200", 5, 10, 50, 2)(FakeRequest())
//
//      (json \ "totalRecords").as[Int]           shouldBe 50
//      (json \ "items").as[JsArray].value.length shouldBe 10
//    }
//  }
}
