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

import java.time.LocalDate

class GamblingBusinessContactDetailsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingBusinessContactDetailsController]

  "GamblingBusinessContactDetailsController#getBusinessContactDetails" should {

    "route requests using the mgd path segment" in {
      val request = FakeRequest(GET, "/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000001761")
      val result = route(app, request).get

      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "mgdRegNumber").as[String] shouldBe "XGM00000001761"
    }

    "return business contact details for the return summary scenario registration numbers" in {
      Seq(
        "XGM00000001761",
        "GAM0000000001",
        "XGM00000001762",
        "GAM0000000010",
        "XGM00000001763",
        "GAM0000000012"
      ).foreach { mgdRegNumber =>
        val result = controller.getBusinessContactDetails(mgdRegNumber)(FakeRequest())

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.obj(
          "mgdRegNumber"      -> mgdRegNumber,
          "phoneNumber"       -> "07700900999",
          "mobilePhoneNumber" -> "07700900999",
          "faxNumber"         -> "07700900999",
          "emailAddr"         -> "viv@xyz.com",
          "systemDate"        -> LocalDate.now().toString
        )
      }
    }

    "return no data for XGM00000000200" in {
      val result = controller.getBusinessContactDetails("XGM00000000200")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.obj(
        "mgdRegNumber"      -> "",
        "phoneNumber"       -> "",
        "mobilePhoneNumber" -> "",
        "faxNumber"         -> "",
        "emailAddr"         -> "",
        "systemDate"        -> ""
      )
    }

    "return BAD_REQUEST for XGM00000000400" in {
      val result = controller.getBusinessContactDetails("XGM00000000400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )
    }

    "return UNAUTHORIZED for XGM00000000401" in {
      val result = controller.getBusinessContactDetails("XGM00000000401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )
    }

    "return NOT_FOUND for XGM00000000404" in {
      val result = controller.getBusinessContactDetails("XGM00000000404")(FakeRequest())

      status(result) shouldBe NOT_FOUND
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "NOT_FOUND",
        "message" -> "No business contact details found for the given registration number"
      )
    }

    "return INTERNAL_SERVER_ERROR for XGM00000000500" in {
      val result = controller.getBusinessContactDetails("XGM00000000500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getBusinessContactDetails("invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getBusinessContactDetails("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }
  }
}
