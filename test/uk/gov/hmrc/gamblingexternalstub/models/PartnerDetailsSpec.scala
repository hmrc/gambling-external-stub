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
import play.api.libs.json.*
import uk.gov.hmrc.gamblingexternalstub.models.PartnerFormats.*

import java.time.LocalDate

class PartnerDetailsSpec extends AnyWordSpec with Matchers {

  "PartnerResponse parsing" should {

    "successfully parse a fully populated 200 OK response" in {
      val populatedJson =
        """{
          |  "partners": [
          |    {
          |      "mgdRegNumber": "XWM00000001770",
          |      "dateOfJoining": "2024-01-01",
          |      "dateOfLeaving": "2025-03-01",
          |      "solePropTitle": "Mr",
          |      "solePropFirstName": "John",
          |      "solePropMiddleName": "A",
          |      "solePropLastName": "Doe",
          |      "businessName": "Partner1",
          |      "tradingName": "Trading name 1",
          |      "dateOfBirth": "1990-06-24",
          |      "nino": "QQ123456C",
          |      "utr": 1234567890,
          |      "vrn": 987654321,
          |      "crn": "12345678",
          |      "dateOfIncorporation": "2023-02-15",
          |      "countryOfIncorporation": "Spain",
          |      "foreignCorporateRef": "REF123",
          |      "address1": "Address 1",
          |      "address2": "Address 2",
          |      "address3": "Address 3",
          |      "address4": "Address 4",
          |      "postcode": "AB12 3CD",
          |      "country": "Spain",
          |      "adi": "ADI Info",
          |      "iomOrCiFlag": "false",
          |      "phoneNumber": "0123456789",
          |      "mobilePhoneNumber": "0712345678",
          |      "faxNumber": "0123456780",
          |      "emailAddr": "test@example.com",
          |      "isFutureLeaveDate": 0,
          |      "isFutureJoinDate": 0,
          |      "businessType": 2
          |    }
          |  ],
          |  "systemDate": "2026-05-31"
          |}""".stripMargin

      val json: JsValue = Json.parse(populatedJson)
      val result = Json.fromJson[PartnerResponse](json)

      result.isSuccess shouldBe true
      val response = result.get

      response.systemDate      shouldBe Some(LocalDate.of(2026, 5, 31))
      response.partners.length shouldBe 1

      val partner = response.partners.head
      partner.mgdRegNumber  shouldBe "XWM00000001770"
      partner.dateOfJoining shouldBe Some(LocalDate.of(2024, 1, 1))
      partner.utr           shouldBe Some(BigDecimal(1234567890))
      partner.iomOrCiFlag   shouldBe Some("false")
      partner.businessType  shouldBe Some(2)
    }

    "successfully serialize back to JSON string" in {
      val model: PartnerResponse = PartnerResponse(
        partners = List(
          Partner(
            mgdRegNumber           = "XWM00000001770",
            dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
            dateOfLeaving          = None,
            solePropTitle          = None,
            solePropFirstName      = None,
            solePropMiddleName     = None,
            solePropLastName       = None,
            businessName           = Some("Partner1"),
            tradingName            = None,
            dateOfBirth            = None,
            nino                   = None,
            utr                    = Some(BigDecimal(123456789)),
            vrn                    = None,
            crn                    = None,
            dateOfIncorporation    = None,
            countryOfIncorporation = None,
            foreignCorporateRef    = None,
            address1               = None,
            address2               = None,
            address3               = None,
            address4               = None,
            postcode               = None,
            country                = None,
            adi                    = None,
            iomOrCiFlag            = Some("false"),
            phoneNumber            = None,
            mobilePhoneNumber      = None,
            faxNumber              = None,
            emailAddr              = None,
            isFutureLeaveDate      = Some(0),
            isFutureJoinDate       = Some(0),
            businessType           = Some(2)
          )
        ),
        systemDate = Some(LocalDate.of(2026, 5, 31))
      )

      val json = Json.toJson(model)
      (json \ "systemDate").as[String]                    shouldBe "2026-05-31"
      (json \ "partners" \ 0 \ "mgdRegNumber").as[String] shouldBe "XWM00000001770"
    }
  }
}
