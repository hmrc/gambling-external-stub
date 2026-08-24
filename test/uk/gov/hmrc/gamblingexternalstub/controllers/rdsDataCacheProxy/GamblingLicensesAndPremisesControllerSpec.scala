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

}
