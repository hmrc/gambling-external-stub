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

class GamblingSubmittedReturnsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingSubmittedReturnsController]

  "GamblingSubmittedReturnsController#getSubmittedReturns" should {

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getSubmittedReturns("XWM00003100400", None, None)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getSubmittedReturns("XWM00003100401", None, None)(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getSubmittedReturns("XWM00003100404", None, None)(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No SubmittedReturns found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getSubmittedReturns("XWM00003100500", None, None)(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return 0 records for XWM00003100200 (last 3 = 200, 4th+5th from right = 00)" in {
      val result = controller.getSubmittedReturns("XWM00003100200", None, None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "items").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200 (last 3 = 200, 4th+5th from right = 03)" in {
      val result = controller.getSubmittedReturns("XWM00003103200", None, None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "items").as[JsArray].value.length shouldBe 3
    }

    "return 1 records for XWM00003101200 (last 3 = 200, 4th+5th from right = 01) with correct sortBy & orderBy" in {
      val result = controller.getSubmittedReturns("XWM00003101200", Some(1), Some("DESC"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "items").as[JsArray].value.length shouldBe 1
      val items1 = (json \ "items")(0)
      (items1 \ "ack_ref").as[String] shouldBe "1100__sortBy=1__orderBy=DESC"

    }

    "return 1 records for XWM00003101200 (last 3 = 200, 4th+5th from right = 01) with DEFAULT sortBy & orderBy" in {
      val result = controller.getSubmittedReturns("XWM00003101200", Some(100), Some("WRONG"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "items").as[JsArray].value.length shouldBe 1
      val items1 = (json \ "items")(0)
      (items1 \ "ack_ref").as[String] shouldBe "1100__sortBy=3__orderBy=ASC"

    }
  }
}
