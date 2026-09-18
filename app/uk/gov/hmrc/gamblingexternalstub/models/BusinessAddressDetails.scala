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

  private lazy val fullData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/XGM00000001761.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  private lazy val partialData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/XGM00000001762.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  private lazy val nonUKData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/XGM00000001763.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  private lazy val iomCIUKData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/XGM00000001764.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  private lazy val nonMandatoryData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/XGM00000001765.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  private lazy val noData = Using
    .resource(
      getClass.getResourceAsStream("/data/business-address/business-address.json")
    )(Json.parse)
    .as[BusinessAddressDetails]

  def fullModel(mgdRegNumber: String): BusinessAddressDetails =
    fullData.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))

  def partialModel(mgdRegNumber: String): BusinessAddressDetails =
    partialData.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))

  def nonUKModel(mgdRegNumber: String): BusinessAddressDetails =
    nonUKData.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))

  def iomCIUKModel(mgdRegNumber: String): BusinessAddressDetails =
    iomCIUKData.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))

  def nonMandatoryModel(mgdRegNumber: String): BusinessAddressDetails =
    nonMandatoryData.copy(mgdRegNumber = mgdRegNumber, systemDate = Some(LocalDate.now()))

  def noDataModel(): BusinessAddressDetails = noData

}
