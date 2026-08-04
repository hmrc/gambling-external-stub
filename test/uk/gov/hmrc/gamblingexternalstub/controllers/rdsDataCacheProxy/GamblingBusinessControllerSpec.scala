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
import uk.gov.hmrc.gamblingexternalstub.models.*

import java.time.LocalDate

class GamblingBusinessControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingBusinessController]

  "GamblingController#getBusinessAddressDetails" should {

    "return OK and full model for XGM00000001761" in {
      val result = controller.getBusinessAddressDetails("MGD", "XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        BusinessAddressDetails(
          "XGM00000001761",
          Some("1st floor"),
          Some("address1"),
          Some("address2"),
          Some("address3"),
          Some("address4"),
          Some("L1 8YL"),
          Some("England"),
          Some("FALSE"),
          Some(LocalDate.now())
        )
      )
    }

    "return OK and partial model for XGM00000001762" in {
      val result = controller.getBusinessAddressDetails("MGD", "XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        BusinessAddressDetails(
          mgdRegNumber = "XGM00000001762",
          adi          = Some("1st floor"),
          address1     = Some("address1"),
          postcode     = Some("L1 8YL"),
          country      = Some("England"),
          iomOrCiFlag  = Some("FALSE"),
          systemDate   = Some(LocalDate.now())
        )
      )
    }

    "return default response" in {
      val result = controller.getBusinessAddressDetails("MGD", "GAM999")(FakeRequest())

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.obj()
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getBusinessAddressDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getBusinessAddressDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BadRequest for XGM00000000400" in {
      val result = controller.getBusinessAddressDetails("MGD", "XGM00000000400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XGM00000000401" in {
      val result = controller.getBusinessAddressDetails("MGD", "XGM00000000401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XGM00000000500" in {
      val result = controller.getBusinessAddressDetails("MGD", "XGM00000000500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }
  }

}
