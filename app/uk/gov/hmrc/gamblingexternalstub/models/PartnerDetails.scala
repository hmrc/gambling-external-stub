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
  utr: Option[BigDecimal],
  vrn: Option[BigDecimal],
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

case class PartnerResponse(
  partners: List[Partner],
  systemDate: Option[LocalDate]
)

object PartnerFormats {
  implicit val partnerFormat: OFormat[Partner] = Json.format[Partner]
  implicit val partnerResponseFormat: OFormat[PartnerResponse] = Json.format[PartnerResponse]
}
