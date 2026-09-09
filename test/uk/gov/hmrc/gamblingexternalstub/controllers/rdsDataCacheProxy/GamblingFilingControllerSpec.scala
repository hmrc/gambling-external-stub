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
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class GamblingFilingControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()

  private val validBody: JsValue = Json.obj("status" -> 1)

  private def url(regime: String, registrationNo: String, consecNo: Int) =
    s"/rds-datacache-proxy/gambling/update-status-period/$regime/$registrationNo/$consecNo"

  private def put(regime: String, registrationNo: String, consecNo: Int, body: JsValue) =
    route(app, FakeRequest(PUT, url(regime, registrationNo, consecNo)).withJsonBody(body)).get

  "PUT /gambling/update-status-period/:regime/:registrationNo/:consecNo" should {

    "return NO_CONTENT for a valid regime, body and registrationNo" in {
      val result = put("MGD", "XWM00003100200", 1, validBody)

      status(result) shouldBe NO_CONTENT
    }

    "accept all valid regimes (case-insensitive)" in {
      Seq("MGD", "mgd", "GBD", "gbd", "PBD", "pbd", "RGD", "rgd").foreach { regime =>
        val result = put(regime, "XWM00003100200", 1, validBody)
        status(result) shouldBe NO_CONTENT
      }
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val result = put("INVALID", "XWM00003100200", 1, validBody)

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> "regime must be one of: gbd, pbd, rgd, mgd"
      )
    }

    "return BAD_REQUEST for a missing status field" in {
      val result = put("MGD", "XWM00003100200", 1, Json.obj())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Invalid request body"
      )
    }

    "return BAD_REQUEST for a non-integer status field" in {
      val result = put("MGD", "XWM00003100200", 1, Json.obj("status" -> "not-a-number"))

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Invalid request body"
      )
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = put("MGD", "XWM00003100400", 1, validBody)

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = put("MGD", "XWM00003100401", 1, validBody)

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = put("MGD", "XWM00003100404", 1, validBody)

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No period found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = put("MGD", "XWM00003100500", 1, validBody)

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }
}
