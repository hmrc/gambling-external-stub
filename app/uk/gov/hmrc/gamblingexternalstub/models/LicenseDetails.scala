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

final case class LicenseDetails(
  mgdRegNumber: String,
  haveGamblingLicenceNo: Option[String] = None,
  gamblingLicenceNo: Option[String] = None,
  heldByLandlord: Option[String] = None,
  localAuthority: Option[String] = None,
  familyEntertainment: Option[String] = None,
  clubGaming: Option[String] = None,
  clubLicence: Option[String] = None,
  prizeGaming: Option[String] = None,
  onPremises: Option[String] = None,
  clubPremises: Option[String] = None,
  regCert: Option[String] = None,
  bookmaking: Option[String] = None,
  bingo: Option[String] = None,
  amusement: Option[String] = None,
  serveAlcohol: Option[String] = None,
  premisesNotCovered: Option[String] = None,
  systemDate: Option[LocalDate] = None
)

object LicenseDetails {
  implicit val format: OFormat[LicenseDetails] = Json.format[LicenseDetails]

  def fullModel(mgdRegNumber: String): LicenseDetails = LicenseDetails(
    mgdRegNumber,
    haveGamblingLicenceNo = Some("1"),
    gamblingLicenceNo     = Some("123-456789-A-123456-789"),
    heldByLandlord        = Some("1"),
    localAuthority        = Some("1"),
    familyEntertainment   = Some("0"),
    clubGaming            = Some("0"),
    clubLicence           = Some("1"),
    prizeGaming           = Some("0"),
    onPremises            = Some("1"),
    clubPremises          = Some("0"),
    regCert               = Some("0"),
    bookmaking            = Some("0"),
    bingo                 = Some("0"),
    amusement             = Some("0"),
    serveAlcohol          = Some("0"),
    premisesNotCovered    = Some("0"),
    systemDate            = Some(LocalDate.now())
  )

  def partialModel(mgdRegNumber: String): LicenseDetails = LicenseDetails(
    mgdRegNumber,
    haveGamblingLicenceNo = Some("1"),
    gamblingLicenceNo     = Some("123-456789-A-123456-789"),
    heldByLandlord        = Some("1"),
    localAuthority        = Some("1"),
    systemDate            = Some(LocalDate.now())
  )

  def noDataModel(): LicenseDetails = LicenseDetails(mgdRegNumber = "")

}
