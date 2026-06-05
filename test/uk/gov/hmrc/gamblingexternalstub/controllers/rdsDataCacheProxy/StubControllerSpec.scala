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

class StubControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[StubController]

  // Reg number convention: last 3 digits = HTTP status, 4th+5th from right = 2-digit record count
  // e.g. XWM01003100404 (404), XWM01003100500 (500), XWM01003103200 (200, 3 records), XWM01003150200 (200, 50 records)
  "StubController#getReturnsSubmitted" should {

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = controller.getReturnsSubmitted("INVALID", "XWM01003103200", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = controller.getReturnsSubmitted(regime, "XWM01003100200", 1, 10)(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM01003100400 (last 3 digits = 400)" in {
      val result = controller.getReturnsSubmitted("MGD", "XWM01003100400", 1, 10)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM01003100401 (last 3 digits = 401)" in {
      val result = controller.getReturnsSubmitted("MGD", "XWM01003100401", 1, 10)(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM01003100404 (last 3 digits = 404)" in {
      val result = controller.getReturnsSubmitted("MGD", "XWM01003100404", 1, 10)(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No returns found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM01003100500 (last 3 digits = 500)" in {
      val result = controller.getReturnsSubmitted("MGD", "XWM01003100500", 1, 10)(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }
}
