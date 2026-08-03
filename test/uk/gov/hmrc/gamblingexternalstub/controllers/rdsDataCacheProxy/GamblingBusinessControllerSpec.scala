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
import uk.gov.hmrc.gamblingexternalstub.models.BusinessType.SoleProprietor

import java.time.LocalDate

class GamblingBusinessControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingBusinessController]

  "GamblingController#getBusinessAddressDetails" should {

    "return OK for XGM00000001761" in {
      val result = controller.getBusinessAddressDetails("XGM00000001761")(FakeRequest())

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
          Some(LocalDate.now().toString),
        )
      )
    }


    "return BAD_REQUEST for invalid" in {
      val result = controller.getBusinessAddressDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getBusinessAddressDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return default response" in {
      val result = controller.getBusinessAddressDetails("GAM999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.obj(
        "mgdRegNumber" -> "",
        "adi"          -> "",
        "address1"     -> "",
        "address2"     -> "",
        "address3"     -> "",
        "address4"     -> "",
        "postcode"     -> "",
        "country"      -> "",
        "iomOrCiFlag"  -> "",
        "systemDate"   -> ""
      )
    }

  }

}
