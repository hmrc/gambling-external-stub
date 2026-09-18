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
import scala.util.Using

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

  private lazy val fullData = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/full.json")
    )(Json.parse)
    .as[PartnerDetails]

  private lazy val partialData = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/XJM00000000570.json")
    )(Json.parse)
    .as[PartnerDetails]

  private lazy val noData = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/partner-details.json")
    )(Json.parse)
    .as[PartnerDetails]

  private lazy val xmm00000001177Data = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/XMM00000001177.json")
    )(Json.parse)
    .as[PartnerDetails]

  private lazy val xpm00000000985Data = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/XPM00000000985.json")
    )(Json.parse)
    .as[PartnerDetails]

  private lazy val hundredPartnersData = Using
    .resource(
      getClass.getResourceAsStream("/data/partner-details/XPM00000000600.json")
    )(Json.parse)
    .as[PartnerDetails]

  def fullModel(mgdRegNumber: String): PartnerDetails =
    fullData.copy(
      partners   = fullData.partners.map(_.copy(mgdRegNumber = mgdRegNumber)),
      systemDate = baseDate
    )

  def partialModel(mgdRegNumber: String): PartnerDetails =
    partialData.copy(
      partners   = partialData.partners.map(_.copy(mgdRegNumber = mgdRegNumber)),
      systemDate = baseDate
    )

  def noDataModel(mgdRegNumber: String): PartnerDetails =
    noData.copy(
      partners   = noData.partners.map(_.copy(mgdRegNumber = mgdRegNumber)),
      systemDate = baseDate
    )

  def `XMM00000001177`(mgdRegNumber: String): PartnerDetails = xmm00000001177Data

  def `XPM00000000985`(mgdRegNumber: String): PartnerDetails = xpm00000000985Data

  def `XPM00000000600`: PartnerDetails = mockPartnerDetails

  private val baseDate = Some(LocalDate.now())

  // Retain the original relative dates when loading the static partner data.
  private lazy val mockPartnerDetails: PartnerDetails = hundredPartnersData.copy(
    partners = hundredPartnersData.partners.zipWithIndex.map { case (partner, index) =>
      val days = index.toLong + 1
      partner.copy(
        dateOfJoining = baseDate.map(_.plusDays(days)),
        dateOfLeaving = partner.dateOfLeaving.flatMap(_ => baseDate.map(_.plusYears(1).plusDays(days)))
      )
    },
    systemDate = baseDate
  )
}
