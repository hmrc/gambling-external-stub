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

import play.api.libs.json.{Json, OFormat, Writes}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

final case class PartnerMember(
  namesOfPartMems: String,
  solePropTitle: Option[String],
  solePropFirstName: Option[String],
  solePropMiddleName: Option[String],
  solePropLastName: Option[String],
  typeOfBusiness: Int
)

object PartnerMember {
  implicit val format: OFormat[PartnerMember] = Json.format[PartnerMember]
}

final case class GroupMember(
  namesOfGroupMems: String
)

object GroupMember {
  implicit val format: OFormat[GroupMember] = Json.format[GroupMember]
}

final case class ReturnPeriodEndDate(
  returnPeriodEndDate: LocalDate
)

object ReturnPeriodEndDate {
  private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
  implicit val localDateWrites: Writes[LocalDate] =
    Writes.temporalWrites[LocalDate, DateTimeFormatter](fmt)

  implicit val format: OFormat[ReturnPeriodEndDate] = Json.format[ReturnPeriodEndDate]
}

final case class MgdCertificate(
  mgdRegNumber: String,
  registrationDate: Option[LocalDate],
  individualName: Option[String],
  businessName: Option[String],
  tradingName: Option[String],
  repMemName: Option[String],
  busAddrLine1: Option[String],
  busAddrLine2: Option[String],
  busAddrLine3: Option[String],
  busAddrLine4: Option[String],
  busPostcode: Option[String],
  busCountry: Option[String],
  busAdi: Option[String],
  repMemLine1: Option[String],
  repMemLine2: Option[String],
  repMemLine3: Option[String],
  repMemLine4: Option[String],
  repMemPostcode: Option[String],
  repMemAdi: Option[String],
  typeOfBusiness: Option[String],
  businessTradeClass: Option[Int],
  noOfPartners: Option[Int],
  groupReg: String,
  noOfGroupMems: Option[Int],
  dateCertIssued: Option[LocalDate],
  partMembers: Seq[PartnerMember],
  groupMembers: Seq[GroupMember],
  returnPeriodEndDates: Seq[ReturnPeriodEndDate]
)

object MgdCertificate {
  private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

  implicit val localDateWrites: Writes[LocalDate] =
    Writes.temporalWrites[LocalDate, DateTimeFormatter](fmt)

  implicit val format: OFormat[MgdCertificate] = Json.format[MgdCertificate]

  def sample1(reg: String): MgdCertificate =
    MgdCertificate(
      mgdRegNumber       = reg,
      registrationDate   = Some(LocalDate.parse("2023-01-15", fmt)),
      individualName     = Some("Mr John A Smith"),
      businessName       = Some("Acme Gaming Ltd"),
      tradingName        = Some("Acme Bets"),
      repMemName         = Some("Acme Rep Member Ltd"),
      busAddrLine1       = Some("1 High Street"),
      busAddrLine2       = Some("Newcastle"),
      busAddrLine3       = None,
      busAddrLine4       = None,
      busPostcode        = Some("NE1 1AA"),
      busCountry         = Some("United Kingdom"),
      busAdi             = Some("Some ADI Value"),
      repMemLine1        = Some("2 Low Street"),
      repMemLine2        = Some("Newcastle"),
      repMemLine3        = None,
      repMemLine4        = None,
      repMemPostcode     = Some("NE1 2BB"),
      repMemAdi          = Some("Rep ADI Value"),
      typeOfBusiness     = Some("Corporate Body"),
      businessTradeClass = Some(2),
      noOfPartners       = Some(2),
      groupReg           = "Y",
      noOfGroupMems      = Some(1),
      dateCertIssued     = Some(LocalDate.parse("2024-02-01", fmt)),
      partMembers = Seq(
        PartnerMember(
          namesOfPartMems    = "Partner Member One Ltd",
          solePropTitle      = None,
          solePropFirstName  = None,
          solePropMiddleName = None,
          solePropLastName   = None,
          typeOfBusiness     = 2
        ),
        PartnerMember(
          namesOfPartMems    = "Sole Prop Example",
          solePropTitle      = Some("Ms"),
          solePropFirstName  = Some("Jane"),
          solePropMiddleName = None,
          solePropLastName   = Some("Doe"),
          typeOfBusiness     = 1
        )
      ),
      groupMembers = Seq(
        GroupMember("Group Member One Ltd")
      ),
      returnPeriodEndDates = Seq(
        ReturnPeriodEndDate(LocalDate.parse("2026-03-31", fmt)),
        ReturnPeriodEndDate(LocalDate.parse("2026-06-30", fmt)),
        ReturnPeriodEndDate(LocalDate.parse("2026-09-30", fmt)),
        ReturnPeriodEndDate(LocalDate.parse("2026-12-31", fmt)),
        ReturnPeriodEndDate(LocalDate.parse("2027-03-31", fmt))
      )
    )
}
