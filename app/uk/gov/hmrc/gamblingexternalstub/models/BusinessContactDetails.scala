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

import scala.util.Using

final case class BusinessContactDetails(
  mgdRegNumber: String,
  phoneNumber: String,
  mobilePhoneNumber: String,
  faxNumber: String,
  emailAddr: String,
  systemDate: String
)

object BusinessContactDetails {
  implicit val format: OFormat[BusinessContactDetails] = Json.format[BusinessContactDetails]

  lazy val noData: BusinessContactDetails = Using
    .resource(
      getClass.getResourceAsStream("/data/business-contact-details/XGM00000000200.json")
    )(Json.parse)
    .as[BusinessContactDetails]
}
