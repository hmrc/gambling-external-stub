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

import java.time.LocalDate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import uk.gov.hmrc.gamblingexternalstub.models.BusinessAddressDetails


class BusinessAddressDetailsSpec extends AnyWordSpec with Matchers {

  "Business Address Details JSON format" should {

    "serialize to JSON when defined" in {


      val model = BusinessAddressDetails(
        mgdRegNumber = "XRM00000000574",
        adi = Some("1st floor"),
        address1 = Some("address1"),
        address2 = Some("address2"),
        address3 = Some("address3"),
        address4 = Some("address4"),
        postcode = Some("L1 8YL"),
        country = Some("England"),
        iomOrCiFlag = Some(false),
        systemDate = Some(LocalDate.now())
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber" -> "XRM00000000574",
        "adi"          -> "1st floor",
        "address1"     -> "address1",
        "address2"     -> "address2",
        "address3"     -> "address3",
        "address4"     -> "address4",
        "postcode"     -> "L1 8YL",
        "country"      -> "England",
        "iomOrCiFlag"  -> false,
        "systemDate"   -> LocalDate.now()
      )
    }

    "deserialize JSON" in {

      val json = Json.obj(
        "mgdRegNumber" -> "XRM00000000574",
        "adi"          -> "1st floor",
        "address1"     -> "address1",
        "address2"     -> "address2",
        "address3"     -> "address3",
        "address4"     -> "address4",
        "postcode"     -> "L1 8YL",
        "country"      -> "England",
        "iomOrCiFlag"  -> false,
        "systemDate"   -> LocalDate.now()
      )

      val result = json.as[BusinessAddressDetails]

      result shouldBe BusinessAddressDetails(
        mgdRegNumber = "XRM00000000574",
        adi = Some("1st floor"),
        address1 = Some("address1"),
        address2 = Some("address2"),
        address3 = Some("address3"),
        address4 = Some("address4"),
        postcode = Some("L1 8YL"),
        country = Some("England"),
        iomOrCiFlag = Some(false),
        systemDate = Some(LocalDate.now())
      )
    }

    "deserialize missing optional fields as None" in {

      val json = Json.obj(
        "mgdRegNumber" -> "XRM00000000574"
      )

      val result = json.as[BusinessAddressDetails]

      result shouldBe BusinessAddressDetails(
        mgdRegNumber = "XRM00000000574",
        adi = None,
        address1 = None,
        address2 = None,
        address3 = None,
        address4 = None,
        postcode = None,
        country = None,
        iomOrCiFlag = None,
        systemDate = None
      )
    }
  }
}