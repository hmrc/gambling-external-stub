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

class GamblingOpenReturnsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingOpenReturnsController]

  // Reg number convention: last 3 digits = HTTP status, 4th+5th from right = 2-digit record count
  // e.g. XWM00003100404 (404), XWM00003100500 (500), XWM00003103200 (200, 3 records), XWM00003150200 (200, 50 records)
  "GamblingOpenReturnsController#getOpenPeriods" should {

    "return BAD_REQUEST for an unrecognised/unsupported regime" in {
      Seq("GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getOpenPeriods(regime, "XWM00003100200")(FakeRequest())

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> "regime must be one of: MGD"
        )
      }
    }

    "only accepts mgd as valid regime (case-insensitive)" in {
      Seq("MGD", "mgd").foreach { regime =>
        val result = controller.getOpenPeriods(regime, "XWM00003100200")(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100404")(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No OpenReturnPeriods found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return 0 records for XWM00003100200 (last 3 = 200, 4th+5th from right = 00)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "openPeriods").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200 (last 3 = 200, 4th+5th from right = 03)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200")(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "openPeriods").as[JsArray].value.length shouldBe 3

      val firstItem = (json \ "openPeriods")(0)
      (firstItem \ "consecNo").as[Int] shouldBe 1
    }
  }
}
