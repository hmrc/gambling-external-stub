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

package uk.gov.hmrc.gamblingexternalstub.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import uk.gov.hmrc.gamblingexternalstub.models.BusinessType.SoleProprietor

import java.time.LocalDate

class BusinessDetailsSpec extends AnyWordSpec with Matchers {

  "Business Name JSON format" should {

    "serialize to JSON when defined" in {
      val dateBusinessDetails = LocalDate.of(1991, 4, 7)
      val model = BusinessDetails(
        mgdRegNumber          = "GAM0000000001",
        businessType          = Some(SoleProprietor),
        currentlyRegistered   = 1,
        groupReg              = false,
        dateOfRegistration    = Some(LocalDate.of(1991, 4, 7)),
        businessPartnerNumber = Some("bar"),
        systemDate            = LocalDate.of(1991, 4, 7)
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber"          -> "GAM0000000001",
        "businessType"          -> 1,
        "currentlyRegistered"   -> 1,
        "groupReg"              -> false,
        "dateOfRegistration"    -> dateBusinessDetails,
        "businessPartnerNumber" -> "bar",
        "systemDate"            -> dateBusinessDetails
      )
    }

    "deserialize JSON" in {
      val dateBusinessDetails = LocalDate.of(1991, 4, 7)
      val json = Json.obj(
        "mgdRegNumber"          -> "GAM0000000001",
        "businessType"          -> 1,
        "currentlyRegistered"   -> 0,
        "groupReg"              -> false,
        "dateOfRegistration"    -> dateBusinessDetails,
        "businessPartnerNumber" -> "barbar",
        "systemDate"            -> dateBusinessDetails
      )

      val result = json.as[BusinessDetails]

      result shouldBe BusinessDetails(
        mgdRegNumber          = "GAM0000000001",
        businessType          = Some(SoleProprietor),
        currentlyRegistered   = 0,
        groupReg              = false,
        dateOfRegistration    = Some(LocalDate.of(1991, 4, 7)),
        businessPartnerNumber = Some("barbar"),
        systemDate            = LocalDate.of(1991, 4, 7)
      )

    }
  }
}
