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
import scala.util.Using

final case class MemberDetails(
  mgdRegNumber: String,
  businessPartnerNumber: Option[String],
  businessName: Option[String],
  tradingName: Option[String],
  crn: Option[String],
  dateOfIncorporation: Option[LocalDate],
  countryOfIncorporation: Option[String],
  foreignCorporateRef: Option[String],
  utr: Option[Long],
  vrn: Option[Long],
  tradeClass: Option[Long],
  busActDescription: Option[String],
  phoneNumber: Option[String],
  mobilePhoneNumber: Option[String],
  faxNumber: Option[String],
  emailAddr: Option[String],
  adi: Option[String],
  address1: Option[String],
  address2: Option[String],
  address3: Option[String],
  address4: Option[String],
  postcode: Option[String],
  isIomOrCiFlag: Option[String],
  haveGamblingLicenceNo: Option[String],
  gamblingLicenceNo: Option[String],
  heldByLandlord: Option[String],
  localAuthority: Option[String],
  familyEntertainment: Option[String],
  clubGaming: Option[String],
  clubLicence: Option[String],
  prizeGaming: Option[String],
  onPremises: Option[String],
  clubPremises: Option[String],
  regCert: Option[String],
  bookmaking: Option[String],
  bingo: Option[String],
  amusement: Option[String],
  serveAlcohol: Option[String],
  premisesNotCovered: Option[String],
  previousMgdrn1: Option[String],
  previousMgdrn2: Option[String],
  previousMgdrn3: Option[String],
  associatedMgdrn1: Option[String],
  associatedMgdrn2: Option[String],
  associatedMgdrn3: Option[String],
  dateOfJoining: Option[LocalDate],
  dateOfLeaving: Option[LocalDate],
  isRepMemSameAsCb: Option[String],
  isUkIncorporated: Option[String],
  isRepMemberOfGroup: Option[String],
  systemDate: Option[LocalDate],
  premises: Seq[PremisesDetails]
)

object MemberDetails {
  implicit val format: OFormat[MemberDetails] = Json.format[MemberDetails]

  private lazy val xgm00000001761Data = Using
    .resource(
      getClass.getResourceAsStream("/data/member-details/XGM00000001761.json")
    )(Json.parse)
    .as[MemberDetails]
  

  private lazy val xgm00000001764Data = Using
    .resource(
      getClass.getResourceAsStream("/data/member-details/XGM00000001764.json")
    )(Json.parse)
    .as[MemberDetails]
  

  private lazy val noData = Using
    .resource(
      getClass.getResourceAsStream("/data/member-details/member-details.json")
    )(Json.parse)
    .as[MemberDetails]

  def xgm00000001761Model(mgdRegNumber: String): MemberDetails =
    xgm00000001761Data.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))
  

  def xgm00000001764Model(mgdRegNumber: String): MemberDetails =
    xgm00000001764Data.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))
  

  def noDataModel(): MemberDetails = noData

}
