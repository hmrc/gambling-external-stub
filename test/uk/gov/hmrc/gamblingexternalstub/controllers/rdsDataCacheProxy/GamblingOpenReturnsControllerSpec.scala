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
        val result = controller.getOpenPeriods(regime, "XWM00003100200", None, None)(FakeRequest())

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.obj(
          "code"    -> "INVALID_REGIME",
          "message" -> "regime must be one of: MGD"
        )
      }
    }

    "only accepts mgd as valid regime (case-insensitive)" in {
      Seq("MGD", "mgd").foreach { regime =>
        val result = controller.getOpenPeriods(regime, "XWM00003100200", None, None)(FakeRequest())
        status(result) shouldBe OK
      }
    }

    "return BAD_REQUEST for XWM00003100400 (last 3 digits = 400)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100400", None, None)(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XWM00003100401 (last 3 digits = 401)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100401", None, None)(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XWM00003100404 (last 3 digits = 404)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100404", None, None)(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No OpenReturnPeriods found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XWM00003100500 (last 3 digits = 500)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100500", None, None)(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return 0 records for XWM00003100200 (last 3 = 200, 4th+5th from right = 00)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003100200", None, None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "openPeriods").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200 (last 3 = 200, 4th+5th from right = 03)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", Some(2), None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      (json \ "openPeriods").as[JsArray].value.length shouldBe 3

      val firstItem = (json \ "openPeriods")(0)
      (firstItem \ "consecNo").as[Int] shouldBe 1
    }

    "default to sortBy=1 (period) ASC when sortBy/orderBy are not provided" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", None, None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val periods = items.map(item => (item \ "period").as[String])
      periods shouldBe periods.sorted
    }

    "sort by period ASC when sortBy=1 and orderBy is an unrecognised value" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", Some(1), Some("WRONG"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val periods = items.map(item => (item \ "period").as[String])
      periods shouldBe periods.sorted
    }

    "sort by period DESC when sortBy=1 and orderBy=DESC" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", Some(1), Some("DESC"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val periods = items.map(item => (item \ "period").as[String])
      periods shouldBe periods.sorted.reverse
    }

    "sort by dueDate DESC when sortBy=2 and orderBy=DESC" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", Some(2), Some("DESC"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val dueDates = items.map(item => (item \ "dueDate").as[String])
      dueDates shouldBe dueDates.sorted.reverse
    }

    "sort by dueDate ASC when sortBy=2 and orderBy is lower-case asc (case-insensitive)" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003103200", Some(2), Some("asc"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val dueDates = items.map(item => (item \ "dueDate").as[String])
      dueDates shouldBe dueDates.sorted
    }

    "sort by status DESC when sortBy=3 and orderBy=DESC" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003109200", Some(3), Some("DESC"))(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value
      val statuses = items.map(item => (item \ "status").as[Int])
      statuses shouldBe statuses.sorted.reverse
    }

    "assign status 1 to even consecNo and status 2 to odd consecNo" in {
      val result = controller.getOpenPeriods("MGD", "XWM00003104200", Some(2), None)(FakeRequest())

      status(result) shouldBe OK
      val json = contentAsJson(result)
      val items = (json \ "openPeriods").as[JsArray].value

      items.foreach { item =>
        val consecNo = (item \ "consecNo").as[Int]
        val expectedStatus = if (consecNo % 2 == 0) 1 else 2
        (item \ "status").as[Int] shouldBe expectedStatus
      }
    }
  }
}
