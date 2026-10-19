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

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

final case class ControlBodyDetails(
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
  utr: Option[Long] = None,
  vrn: Option[Long] = None,
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
  isIomOrCiFlag: Option[String] = None,
  phoneNumber: Option[String] = None,
  mobilePhoneNumber: Option[String] = None,
  faxNumber: Option[String] = None,
  emailAddr: Option[String] = None,
  typeOfControllingBody: Option[BusinessType] = None,
  isRepMemSameAsCb: Option[String] = None,
  isUkIncorporated: Option[String] = None,
  systemDate: Option[LocalDate] = None
)

object ControlBodyDetails {

  implicit val controlBodyDetailsFormat: OFormat[ControlBodyDetails] =
    Json.format[ControlBodyDetails]

  def fullModel(mgdRegNumber: String): ControlBodyDetails =
    ControlBodyDetails(
      mgdRegNumber           = mgdRegNumber,
      businessPartnerNumber  = Some("0100051761"),
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
      phoneNumber            = Some("9999"),
      mobilePhoneNumber      = Some("8888"),
      faxNumber              = Some("7777"),
      emailAddr              = Some("emailAddr@example.com"),
      typeOfControllingBody  = Some(BusinessType.SoleProprietor),
      isRepMemSameAsCb       = Some("0"),
      isUkIncorporated       = Some("0"),
      systemDate             = Some(LocalDate.now())
    )

  def partialModel(mgdRegNumber: String): ControlBodyDetails =
    ControlBodyDetails(
      mgdRegNumber           = mgdRegNumber,
      businessPartnerNumber  = Some("0100051762"),
      dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
      dateOfLeaving          = Some(LocalDate.of(2027, 3, 1)),
      solePropTitle          = Some("Mx"),
      solePropFirstName      = Some("solePropFirstName"),
      solePropLastName       = Some("solePropLastName"),
      businessName           = Some("Corporate Body business Name"),
      tradingName            = Some("Corporate Body Trading name"),
      dateOfBirth            = Some(LocalDate.of(1998, 6, 24)),
      nino                   = Some("AB123456C"),
      crn                    = Some("12345678"),
      dateOfIncorporation    = Some(LocalDate.of(2020, 2, 15)),
      countryOfIncorporation = Some("Spain"),
      foreignCorporateRef    = Some("foreignCorporateRef"),
      adi                    = Some("adi"),
      isIomOrCiFlag          = Some("0"),
      phoneNumber            = Some("9999"),
      mobilePhoneNumber      = Some("8888"),
      faxNumber              = Some("7777"),
      emailAddr              = Some("emailAddr@example.com"),
      typeOfControllingBody  = Some(BusinessType.CorporateBody),
      isRepMemSameAsCb       = Some("0"),
      isUkIncorporated       = None,
      systemDate             = Some(LocalDate.now())
    )

  def morePartialModel(mgdRegNumber: String): ControlBodyDetails =
    ControlBodyDetails(
      mgdRegNumber           = mgdRegNumber,
      businessPartnerNumber  = Some("0100051763"),
      dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
      dateOfLeaving          = Some(LocalDate.of(2027, 3, 1)),
      solePropTitle          = Some("Mrs"),
      solePropFirstName      = Some("solePropFirstName"),
      solePropLastName       = Some("solePropLastName"),
      businessName           = Some("Unincorporated business Name"),
      tradingName            = Some("Unincorporated Trading name"),
      dateOfBirth            = None,
      nino                   = None,
      crn                    = None,
      dateOfIncorporation    = None,
      countryOfIncorporation = None,
      foreignCorporateRef    = None,
      adi                    = None,
      isIomOrCiFlag          = Some("0"),
      phoneNumber            = None,
      mobilePhoneNumber      = None,
      faxNumber              = None,
      emailAddr              = None,
      typeOfControllingBody  = Some(BusinessType.CorporateBody),
      isRepMemSameAsCb       = Some("0"),
      isUkIncorporated       = None,
      systemDate             = Some(LocalDate.now())
    )

  def otherModel(mgdRegNumber: String): ControlBodyDetails =
    ControlBodyDetails(
      mgdRegNumber           = mgdRegNumber,
      businessPartnerNumber  = Some("0100051764"),
      dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
      dateOfLeaving          = Some(LocalDate.of(2027, 3, 1)),
      solePropTitle          = Some("Mx"),
      solePropFirstName      = Some("solePropFirstName"),
      solePropLastName       = Some("solePropLastName"),
      businessName           = Some("Partnership business Name"),
      tradingName            = Some("Partnership Trading name"),
      dateOfBirth            = Some(LocalDate.of(1998, 6, 24)),
      nino                   = Some("AB123456C"),
      crn                    = Some("12345678"),
      dateOfIncorporation    = Some(LocalDate.of(2020, 2, 15)),
      countryOfIncorporation = Some("Spain"),
      foreignCorporateRef    = Some("foreignCorporateRef"),
      adi                    = Some("adi"),
      isIomOrCiFlag          = Some("0"),
      phoneNumber            = Some("666 6666"),
      mobilePhoneNumber      = Some("5555 555 555"),
      faxNumber              = Some("4444 444 444"),
      emailAddr              = Some("emailAddr@example.com"),
      typeOfControllingBody  = Some(BusinessType.Partnership),
      isRepMemSameAsCb       = Some("0"),
      isUkIncorporated       = Some("1"),
      systemDate             = Some(LocalDate.now())
    )

  def otherModels(mgdRegNumber: String): ControlBodyDetails =
    ControlBodyDetails(
      mgdRegNumber           = mgdRegNumber,
      businessPartnerNumber  = Some("0100051765"),
      dateOfJoining          = Some(LocalDate.of(2013, 2, 1)),
      dateOfLeaving          = Some(LocalDate.of(2026, 3, 1)),
      solePropTitle          = Some("Mx"),
      solePropFirstName      = Some("solePropFirstName"),
      solePropLastName       = Some("solePropLastName"),
      businessName           = Some("LLP Body business Name"),
      tradingName            = Some("LLP Body Trading name"),
      dateOfBirth            = Some(LocalDate.of(1998, 6, 24)),
      nino                   = Some("AB123456C"),
      crn                    = Some("12345678"),
      dateOfIncorporation    = Some(LocalDate.of(2020, 2, 15)),
      countryOfIncorporation = Some("United Kingdom"),
      foreignCorporateRef    = Some("foreignCorporateRef"),
      adi                    = Some("adi"),
      isIomOrCiFlag          = Some("1"),
      phoneNumber            = Some("666 6666"),
      mobilePhoneNumber      = Some("5555 555 555"),
      faxNumber              = Some("4444 444 444"),
      emailAddr              = Some("emailAddr@example.com"),
      typeOfControllingBody  = Some(BusinessType.LimitedLiabilityPartnership),
      isRepMemSameAsCb       = Some("0"),
      isUkIncorporated       = Some("1"),
      systemDate             = Some(LocalDate.now())
    )

  def noDataModel(): ControlBodyDetails = ControlBodyDetails(mgdRegNumber = "")

}
