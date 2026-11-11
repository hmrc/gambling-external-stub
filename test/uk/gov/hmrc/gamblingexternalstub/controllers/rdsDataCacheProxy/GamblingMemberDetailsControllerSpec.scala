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

class GamblingMemberDetailsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingMemberDetailsController]

  "GamblingMemberDetailsController#getMemberDetails" should {

    "return OK and full model for XGM00000001761" in {
      val result = controller.getMemberDetails("MGD", "XGM00000001761", "")(FakeRequest())
      

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        MemberDetails(
          "XGM00000001761",
          businessPartnerNumber = Some("0100053091"), businessName = Some("BRUCE HOPKINS LIMITED"), tradingName = Some(" Trading name 1"), crn = Some(""),
          dateOfIncorporation = Some(LocalDate.of(2020,2,15)), countryOfIncorporation = Some("Spain"), foreignCorporateRef = Some(""), utr = Some(5202020208L), vrn = Some(127207785L), tradeClass = Some(1L),
          busActDescription = Some(""), phoneNumber = Some(""), mobilePhoneNumber = Some(""), faxNumber = Some(""), emailAddr = Some(""), adi = Some(""),
          address1 = Some("Address 1"), address2 = Some("Address 2"), address3 = Some("Address 3"), address4 = Some(""), postcode = Some(""), isIomOrCiFlag = Some("0"), haveGamblingLicenceNo = Some("1"),
          gamblingLicenceNo = Some("123-456789-A-123456-789"), heldByLandlord = Some("1"), localAuthority = Some("1"), familyEntertainment = Some("0"), clubGaming = Some("0"), clubLicence = Some("1"),
          prizeGaming = Some("0"), onPremises = Some("1"), clubPremises = Some("0"), regCert = Some("0"), bookmaking = Some("0"), bingo = Some("0"), amusement = Some("0"),
          serveAlcohol = Some("0"), premisesNotCovered = Some("0"), previousMgdrn1 = Some("XAM00000000327"), previousMgdrn2 = Some("XNM00000000457"), previousMgdrn3 = Some("XHM00000000177"), associatedMgdrn1 = Some("XYM00000000287"),
          associatedMgdrn2 = Some(""), associatedMgdrn3 = Some(""), dateOfJoining = Some(LocalDate.of(2013,2,1)), dateOfLeaving = Some(LocalDate.of(2023,3,1)), isRepMemSameAsCb = Some("0"),
          isUkIncorporated = Some("0"), isRepMemberOfGroup = Some("1"), systemDate = Some(LocalDate.of(2026,11,11)),
          premises = Seq(PremisesDetails("XCM00000001236", Some(" 1.Dalford court "),
            Some(" TeLford "), Some(" Shropshire "),
            Some(" "),
            Some("TF3 2BP"), Some(LocalDate.of(2026,6,30))),PremisesDetails("XCM00000001236", Some(" 2.Dalford court "),
            Some(" TeLford "), Some(" Shropshire "),
            Some(" "),
            Some("TF3 2BP"), Some(LocalDate.of(2026,6,30)))
          )
        )
      )
    }

    "return OK and partial model for XGM00000001764" in {
      val result = controller.getMemberDetails("MGD", "XGM00000001764", "")(FakeRequest())

      status(result) shouldBe OK
      val response = contentAsJson(result).as[MemberDetails]
      response.utr                shouldBe Some(5202020208L)
      response.associatedMgdrn1   shouldBe Some("XYM00000000287")
      response.amusement shouldBe Some("0")
      response.clubPremises   shouldBe Some("0")
    }


    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getMemberDetails(regime, "XWM00003100200", "")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getMemberDetails(regime, "XWM00003100200", "")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BadRequest for XGM00000000400" in {
      val result = controller.getMemberDetails("MGD", "XGM00000000400", "")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XGM00000000401" in {
      val result = controller.getMemberDetails("MGD", "XGM00000000401", "")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XGM00000000500" in {
      val result = controller.getMemberDetails("MGD", "XGM00000000500", "")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }
  }

}
