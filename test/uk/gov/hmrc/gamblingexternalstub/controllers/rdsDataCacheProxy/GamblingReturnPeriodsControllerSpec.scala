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
import play.api.libs.json.{JsNull, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class GamblingReturnPeriodsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app =
    applicationBuilder().build()

  private val controller =
    app.injector.instanceOf[GamblingReturnPeriodsController]

  "GamblingReturnPeriodsController#getReturnPeriods" should {

    "return return periods from the JSON fixture for XGM00000001761" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XGM00000001761"
        )(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "mgdRegNumber").as[String] shouldBe "XGM00000001761"

      // The fixture has no NSTP dates.
      (json \ "nstpEndDate1").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate2").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate3").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate4").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate5").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate6").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate7").toOption shouldBe Some(JsNull)
      (json \ "nstpEndDate8").toOption shouldBe Some(JsNull)

      (json \ "hasExistingNstpValues").as[String] shouldBe "0"

      (json \ "systemDate").as[String] shouldBe
        java.time.LocalDate
          .now()
          .format(
            java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yy")
          )
          .toUpperCase(java.util.Locale.ENGLISH)
    }

    "return return periods from the JSON fixture for XGM00000001762" in {

      val result =
        controller.getReturnPeriods(
          "mGd",
          " XGM00000001762 "
        )(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "mgdRegNumber").as[String] shouldBe "XGM00000001762"

      // Values come from XGM00000001762.json.
      (json \ "nstpEndDate1").as[String] shouldBe "31-MAY-26"

      (json \ "hasExistingNstpValues").as[String] shouldBe "1"

      (json \ "systemDate").as[String] shouldBe
        java.time.LocalDate
          .now()
          .format(
            java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yy")
          )
          .toUpperCase(java.util.Locale.ENGLISH)
    }

    "return return periods from the JSON fixture for XGM00000001763" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XGM00000001763"
        )(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "mgdRegNumber").as[String] shouldBe "XGM00000001763"

      (json \ "returnPeriodsId").as[Int] shouldBe 3

      (json \ "isInLastNstp").as[String] shouldBe "1"

      (json \ "hasExistingNstpValues").as[String] shouldBe "1"
    }

    "return the default fixture for an unknown registration number" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XGM00000009999"
        )(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      // The default fixture is returned unchanged by noDataModel().
      (json \ "mgdRegNumber").as[String] shouldBe ""

      (json \ "nstpEndDate1").toOption shouldBe Some(JsNull)
    }

    "return BAD_REQUEST for an unrecognised regime" in {

      val regime = "nope"

      val result =
        controller.getReturnPeriods(
          regime,
          "XGM00000001761"
        )(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for Return Periods"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {

      val regime = "PBD"

      val result =
        controller.getReturnPeriods(
          regime,
          "XGM00000001761"
        )(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for Return Periods"
      )
    }

    "return BAD_REQUEST for XGM00000000560" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XGM00000000560"
        )(FakeRequest())

      status(result) shouldBe BAD_REQUEST

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XMM00000000580" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XMM00000000580"
        )(FakeRequest())

      status(result) shouldBe UNAUTHORIZED

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return INTERNAL_SERVER_ERROR for XAM00000001090" in {

      val result =
        controller.getReturnPeriods(
          "MGD",
          "XAM00000001090"
        )(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR

      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }
}
