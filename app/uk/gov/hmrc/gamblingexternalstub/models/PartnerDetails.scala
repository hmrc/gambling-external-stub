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
  businessPartnerNumber: Option[String] = None,
  dateOfJoining: Option[LocalDate] = None,
  dateOfLeaving: Option[LocalDate] = None,
  solePropTitle: Option[String] = None,
  solePropFirstName: Option[String] = None,
  solePropMiddleName: Option[String] = None,
  solePropLastName: Option[String] = None,
  businessName: Option[String] = None,
  tradingName: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  nino: Option[String] = None,
  utr: Option[String] = None,
  vrn: Option[String] = None,
  crn: Option[String] = None,
  dateOfIncorporation: Option[LocalDate] = None,
  countryOfIncorporation: Option[String] = None,
  foreignCorporateRef: Option[String] = None,
  address1: Option[String] = None,
  address2: Option[String] = None,
  address3: Option[String] = None,
  address4: Option[String] = None,
  postcode: Option[String] = None,
  country: Option[String] = None,
  adi: Option[String] = None,
  iomOrCiFlag: Option[String] = None,
  phoneNumber: Option[String] = None,
  mobilePhoneNumber: Option[String] = None,
  faxNumber: Option[String] = None,
  emailAddr: Option[String] = None,
  isFutureLeaveDate: Option[Int] = None,
  isFutureJoinDate: Option[Int] = None,
  businessType: Option[Int] = None
)

case class PartnerDetails(partners: List[Partner], systemDate: Option[LocalDate])

object PartnerFormats {
  implicit val partnerDetailsFormat: OFormat[Partner] = Json.format[Partner]
  implicit val partnerDetailsResponseFormat: OFormat[PartnerDetails] = Json.format[PartnerDetails]

  def fullModel(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber           = mgdRegNumber,
        businessPartnerNumber  = Some("0100049899"),
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
        businessPartnerNumber  = Some("0100049899"),
        dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
        dateOfLeaving          = Some(LocalDate.of(2025, 1, 1)),
        solePropTitle          = Some("Mx"),
        solePropFirstName      = Some("solePropFirstName"),
        solePropLastName       = Some("solePropLastName"),
        businessName           = Some("Partner1"),
        tradingName            = Some("tradingName"),
        dateOfBirth            = Some(LocalDate.of(1999, 9, 9)),
        nino                   = Some("ni123456789no"),
        crn                    = Some("123456789"),
        dateOfIncorporation    = Some(LocalDate.of(2024, 1, 1)),
        countryOfIncorporation = Some("countryOfIncorporation"),
        foreignCorporateRef    = Some("foreignCorporateRef"),
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

  def noDataModel(mgdRegNumber: String): PartnerDetails =
    PartnerDetails(partners = List(Partner(mgdRegNumber)), systemDate = Some(LocalDate.of(2026, 5, 31)))

}
