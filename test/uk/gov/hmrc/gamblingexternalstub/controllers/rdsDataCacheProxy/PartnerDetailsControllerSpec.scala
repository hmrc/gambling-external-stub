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
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class PartnerDetailsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[PartnerDetailsController]

  "PartnerDetailsController#getPartnerDetails" should {

    "return full partner details for XGM00000001761" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000001761")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String]              shouldBe "XGM00000001761"
      (json \ "partners" \ 0 \ "businessPartnerNumber").asOpt[String]  shouldBe Some("0100049899")
      (json \ "partners" \ 0 \ "businessName").as[String]              shouldBe "Partner1"
      (json \ "partners" \ 0 \ "countryOfIncorporation").asOpt[String] shouldBe Some("countryOfIncorporation")
    }

    "return partial partner details for XGM00000001762" in {
      val result = controller.getPartnerDetails("mGd", " xGM00000001762 ")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String]              shouldBe "XGM00000001762"
      (json \ "partners" \ 0 \ "businessPartnerNumber").asOpt[String]  shouldBe Some("0100049899")
      (json \ "partners" \ 0 \ "businessName").as[String]              shouldBe "Partner1"
      (json \ "partners" \ 0 \ "countryOfIncorporation").asOpt[String] shouldBe Some("countryOfIncorporation")
    }

    "return no partner details for XGM00000001763" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000001763")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String]    shouldBe "XGM00000001763"
      (json \ "partners" \ 0 \ "businessName").asOpt[String] shouldBe None
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getPartnerDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for PartnerDetails"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getPartnerDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for PartnerDetails"
      )
    }

    "return BadRequest for XGM00000000400" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000000400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XGM00000000401" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000000401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XGM00000000500" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000000500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }
  }

}
