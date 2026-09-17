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

class GamblingControlBodyControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingControlBodyController]

  "GamblingLicensesAndPremisesController#getControlBodyDetails" should {

    "return OK and full model for XGM00000001761" in {
      val result = controller.getControlBodyDetails("MGD", "XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ControlBodyDetails(
          mgdRegNumber           = "XGM00000001761",
          businessPartnerNumber  = Some("0100053091"),
          dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
          dateOfLeaving          = Some(LocalDate.of(2023, 3, 1)),
          solePropTitle          = Some("Mx"),
          solePropFirstName      = Some("solePropFirstName"),
          solePropMiddleName     = Some("solePropMiddleName"),
          solePropLastName       = Some("solePropLastName"),
          businessName           = Some("BRUCE HOPKINS LIMITED"),
          tradingName            = Some("Trading name 1"),
          dateOfBirth            = Some(LocalDate.of(1998, 6, 24)),
          nino                   = Some("AB123456C"),
          utr                    = Some(5202020208L),
          vrn                    = Some(127207785L),
          crn                    = Some("12345678"),
          dateOfIncorporation    = Some(LocalDate.of(2020, 2, 15)),
          countryOfIncorporation = Some("Spain"),
          foreignCorporateRef    = Some("foreignCorporateRef"),
          address1               = Some("Address 1"),
          address2               = Some("Address 2"),
          address3               = Some("Address 3"),
          address4               = Some("Address 4"),
          postcode               = Some("postcode"),
          country                = Some("Spain"),
          adi                    = Some("adi"),
          isIomOrCiFlag          = Some("0"),
          phoneNumber            = Some("phoneNumber"),
          mobilePhoneNumber      = Some("mobilePhoneNumber"),
          faxNumber              = Some("faxNumber"),
          emailAddr              = Some("emailAddr"),
          typeOfControllingBody  = Some(BusinessType.CorporateBody),
          isRepMemSameAsCb       = Some("0"),
          isUkIncorporated       = Some("0"),
          systemDate             = Some(LocalDate.now())
        )
      )
    }

    "return OK and partial model for XGM00000001762" in {
      val result = controller.getControlBodyDetails("MGD", "XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ControlBodyDetails(
          mgdRegNumber           = "XGM00000001762",
          businessPartnerNumber  = Some("0100053091"),
          dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
          dateOfLeaving          = Some(LocalDate.of(2023, 3, 1)),
          solePropTitle          = Some("Mx"),
          solePropFirstName      = Some("solePropFirstName"),
          solePropLastName       = Some("solePropLastName"),
          businessName           = Some("BRUCE HOPKINS LIMITED"),
          tradingName            = Some("Trading name 1"),
          dateOfBirth            = Some(LocalDate.of(1998, 6, 24)),
          nino                   = Some("AB123456C"),
          crn                    = Some("12345678"),
          dateOfIncorporation    = Some(LocalDate.of(2020, 2, 15)),
          countryOfIncorporation = Some("Spain"),
          foreignCorporateRef    = Some("foreignCorporateRef"),
          adi                    = Some("adi"),
          isIomOrCiFlag          = Some("0"),
          phoneNumber            = Some("phoneNumber"),
          mobilePhoneNumber      = Some("mobilePhoneNumber"),
          faxNumber              = Some("faxNumber"),
          emailAddr              = Some("emailAddr"),
          typeOfControllingBody  = Some(BusinessType.CorporateBody),
          isRepMemSameAsCb       = Some("0"),
          isUkIncorporated       = Some("0"),
          systemDate             = Some(LocalDate.now())
        )
      )
    }

    "return default response" in {
      val result = controller.getControlBodyDetails("MGD", "GAM999")(FakeRequest())

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(ControlBodyDetails(mgdRegNumber = ""))
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getControlBodyDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getControlBodyDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BadRequest for XGM00000000400" in {
      val result = controller.getControlBodyDetails("MGD", "XGM00000000400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XGM00000000401" in {
      val result = controller.getControlBodyDetails("MGD", "XGM00000000401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XGM00000000500" in {
      val result = controller.getControlBodyDetails("MGD", "XGM00000000500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }
  }

}
