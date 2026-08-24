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

class GamblingLicensesAndPremisesControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[GamblingLicensesAndPremisesController]

  private val fixedDate = LocalDate.parse("2026-01-01")

  "GamblingLicensesAndPremisesController#getPremisesDetails" should {

    "return total rows for XGM00000001763" in {
      val result = controller.getPremisesDetails("MGD", "XGM00000001763")(FakeRequest())

      status(result)                                shouldBe OK
      (contentAsJson(result) \ "totalRows").as[Int] shouldBe 1000
    }

    "return premises details" in {
      val result = controller.getPremisesDetails("MGD", "GAM999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        Response(
          totalRows = Some(1000),
          premises = Seq(
            PremisesDetails(
              mgdRegNumber = "GAM999",
              address1     = Some("Flat 55"),
              address2     = Some("20 Market Calle"),
              address3     = Some("Barcelona"),
              address4     = None,
              postcode     = Some("08001"),
              Some(fixedDate)
            ),
            PremisesDetails(
              mgdRegNumber = "GAM999",
              address1     = Some("Flat 1"),
              address2     = Some("10 Market Calle"),
              address3     = Some("Madrid"),
              address4     = None,
              postcode     = Some("28058"),
              Some(fixedDate)
            )
          )
        )
      )
    }

    "return nothing for XGM00000001764" in {
      val result = controller.getPremisesDetails("MGD", "XGM00000001764")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        Response(
          totalRows = Some(0),
          premises = Seq(
          )
        )
      )
    }

    "return BAD_REQUEST for invalid" in {
      val result = controller.getPremisesDetails("MGD", "invalid")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
    }

    "return INTERNAL_SERVER_ERROR for error" in {
      val result = controller.getPremisesDetails("MGD", "error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "return BAD_REQUEST for error" in {
      val result = controller.getPremisesDetails("GTR", "XGM00000001764")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
    }
  }

  "GamblingLicensesAndPremisesController#getLicenseDetails" should {

    "return OK and full model for XGM00000001761" in {
      val result = controller.getLicenseDetails("MGD", "XGM00000001761")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        LicenseDetails(
          "XGM00000001761",
          haveGamblingLicenceNo = Some("1"),
          gamblingLicenceNo = Some("123-456789-A-123456-789"),
          heldByLandlord = Some("1"),
          localAuthority = Some("1"),
          familyEntertainment = Some("0"),
          clubGaming = Some("0"),
          clubLicence = Some("1"),
          prizeGaming = Some("0"),
          onPremises = Some("1"),
          clubPremises = Some("0"),
          regCert = Some("0"),
          bookmaking = Some("0"),
          bingo = Some("0"),
          amusement = Some("0"),
          serveAlcohol = Some("0"),
          premisesNotCovered = Some("0"),
          systemDate  = Some(LocalDate.now())
        )
      )
    }

    "return OK and partial model for XGM00000001762" in {
      val result = controller.getLicenseDetails("MGD", "XGM00000001762")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        LicenseDetails(
          mgdRegNumber = "XGM00000001762",
          haveGamblingLicenceNo = Some("1"),
          gamblingLicenceNo = Some("123-456789-A-123456-789"),
          heldByLandlord = Some("1"),
          localAuthority = Some("1"),
          systemDate  = Some(LocalDate.now())
        )
      )
    }

    "return default response" in {
      val result = controller.getLicenseDetails("MGD", "GAM999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(LicenseDetails(mgdRegNumber = ""))
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getLicenseDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getLicenseDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported"
      )
    }

    "return BadRequest for XGM00000000400" in {
      val result = controller.getLicenseDetails("MGD", "XGM00000000400")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XGM00000000401" in {
      val result = controller.getLicenseDetails("MGD", "XGM00000000401")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XGM00000000500" in {
      val result = controller.getLicenseDetails("MGD", "XGM00000000500")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code" -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }
  }

}
