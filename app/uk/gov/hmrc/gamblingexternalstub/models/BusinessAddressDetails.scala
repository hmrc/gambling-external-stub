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

final case class BusinessAddressDetails(
  mgdRegNumber: String,
  adi: Option[String] = None,
  address1: Option[String] = None,
  address2: Option[String] = None,
  address3: Option[String] = None,
  address4: Option[String] = None,
  postcode: Option[String] = None,
  country: Option[String] = None,
  iomOrCiFlag: Option[String] = None,
  systemDate: Option[LocalDate] = None
)

object BusinessAddressDetails {
  implicit val format: OFormat[BusinessAddressDetails] = Json.format[BusinessAddressDetails]

  def fullModel(mgdRegNumber: String): BusinessAddressDetails = BusinessAddressDetails(
    mgdRegNumber,
    adi         = Some("Additional information for business address is Building number 123 opposite to Riverside Mills"),
    address1    = Some("Address's Line 1.1-1,1/1&1"),
    address2    = Some("Address's Line 2.2-2,2/2&2"),
    address3    = Some("Address's Line 3.3-3,3/3&3"),
    address4    = Some("County 4.4-4,4/4&4"),
    postcode    = Some("ZZ99 9ZZ"),
    country     = Some("United Kingdom"),
    iomOrCiFlag = Some("FALSE"),
    systemDate  = Some(LocalDate.now())
  )

  def partialModel(mgdRegNumber: String): BusinessAddressDetails = BusinessAddressDetails(
    mgdRegNumber,
    adi         = Some("1st floor"),
    address1    = Some("address1"),
    postcode    = Some("L1 8YL"),
    country     = Some("IRELAND"),
    iomOrCiFlag = Some("TRUE"),
    systemDate  = Some(LocalDate.now())
  )

  def noDataModel(): BusinessAddressDetails = BusinessAddressDetails(mgdRegNumber = "")

}
