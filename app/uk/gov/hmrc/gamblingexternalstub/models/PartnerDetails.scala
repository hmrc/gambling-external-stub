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

import play.api.libs.json.*

import java.time.LocalDate

case class Partner(
  mgdRegNumber: String,
  dateOfJoining: Option[LocalDate],
  dateOfLeaving: Option[LocalDate],
  solePropTitle: Option[String],
  solePropFirstName: Option[String],
  solePropMiddleName: Option[String],
  solePropLastName: Option[String],
  businessName: Option[String],
  tradingName: Option[String],
  dateOfBirth: Option[LocalDate],
  nino: Option[String],
  utr: Option[String],
  vrn: Option[String],
  crn: Option[String],
  dateOfIncorporation: Option[LocalDate],
  countryOfIncorporation: Option[String],
  foreignCorporateRef: Option[String],
  address1: Option[String],
  address2: Option[String],
  address3: Option[String],
  address4: Option[String],
  postcode: Option[String],
  country: Option[String],
  adi: Option[String],
  iomOrCiFlag: Option[String],
  phoneNumber: Option[String],
  mobilePhoneNumber: Option[String],
  faxNumber: Option[String],
  emailAddr: Option[String],
  isFutureLeaveDate: Option[Int],
  isFutureJoinDate: Option[Int],
  businessType: Option[Int]
)

case class PartnerDetails(partners: List[Partner], systemDate: Option[LocalDate])

object PartnerFormats {
  implicit val partnerDetailsFormat: OFormat[Partner] = Json.format[Partner]
  implicit val partnerDetailsResponseFormat: OFormat[PartnerDetails] = Json.format[PartnerDetails]

  def fullModel(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber           = mgdRegNumber,
        dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
        dateOfLeaving          = Some(LocalDate.of(2025, 1, 1)),
        solePropTitle          = Some("Mx"),
        solePropFirstName      = Some("solePropFirstName"),
        solePropMiddleName     = Some("solePropMiddleName"),
        solePropLastName       = Some("solePropLastName"),
        businessName           = Some("Partner1"),
        tradingName            = Some("tradingName"),
        dateOfBirth            = Some(LocalDate.of(1999, 9, 9)),
        nino                   = Some("ni123456789no"),
        utr                    = Some("123456789"),
        vrn                    = Some("123456789"),
        crn                    = Some("123456789"),
        dateOfIncorporation    = Some(LocalDate.of(2024, 1, 1)),
        countryOfIncorporation = Some("countryOfIncorporation"),
        foreignCorporateRef    = Some("foreignCorporateRef"),
        address1               = Some("address1"),
        address2               = Some("address2"),
        address3               = Some("address3"),
        address4               = Some("address4"),
        postcode               = Some("postcode"),
        country                = Some("country"),
        adi                    = Some("adi"),
        iomOrCiFlag            = Some("false"),
        phoneNumber            = Some("phoneNumber"),
        mobilePhoneNumber      = Some("mobilePhoneNumber"),
        faxNumber              = Some("faxNumber"),
        emailAddr              = Some("emailAddr"),
        isFutureLeaveDate      = Some(0),
        isFutureJoinDate       = Some(0),
        businessType           = Some(2)
      )
    ),
    systemDate = Some(LocalDate.of(2026, 5, 31))
  )

  def partialModel(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber           = mgdRegNumber,
        dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
        dateOfLeaving          = Some(LocalDate.of(2025, 1, 1)),
        solePropTitle          = Some("Mx"),
        solePropFirstName      = Some("solePropFirstName"),
        solePropMiddleName     = Some(""),
        solePropLastName       = Some("solePropLastName"),
        businessName           = Some("Partner1"),
        tradingName            = Some("tradingName"),
        dateOfBirth            = Some(LocalDate.of(1999, 9, 9)),
        nino                   = Some("ni123456789no"),
        utr                    = Some(""),
        vrn                    = Some(""),
        crn                    = Some("123456789"),
        dateOfIncorporation    = Some(LocalDate.of(2024, 1, 1)),
        countryOfIncorporation = Some("countryOfIncorporation"),
        foreignCorporateRef    = Some("foreignCorporateRef"),
        address1               = Some(""),
        address2               = Some(""),
        address3               = Some(""),
        address4               = Some(""),
        postcode               = Some(""),
        country                = Some(""),
        adi                    = Some("adi"),
        iomOrCiFlag            = Some("false"),
        phoneNumber            = Some("phoneNumber"),
        mobilePhoneNumber      = Some("mobilePhoneNumber"),
        faxNumber              = Some("faxNumber"),
        emailAddr              = Some("emailAddr"),
        isFutureLeaveDate      = Some(0),
        isFutureJoinDate       = Some(0),
        businessType           = Some(2)
      )
    ),
    systemDate = Some(LocalDate.of(2026, 5, 31))
  )

  def noDataModel(): PartnerDetails = PartnerDetails(partners = Nil, systemDate = Some(LocalDate.of(2026, 5, 31)))

}
