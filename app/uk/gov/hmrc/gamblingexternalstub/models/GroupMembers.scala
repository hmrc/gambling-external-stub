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

final case class GroupMembers(
  mgdRegNumber: String,
  businessPartnerNumber: Option[String],
  businessName: Option[String],
  crn: Option[String],
  foreignCorporateRef: Option[String],
  isRepMemberOfGroup: Option[String],
  dateOfJoining: Option[LocalDate],
  dateOfLeaving: Option[LocalDate],
  emailAddr: Option[String],
  systemDate: Option[LocalDate]
)

final case class GroupMemberRecords (
  groupMembers: Seq[GroupMembers]
)

object GroupMembers {
  implicit val format: OFormat[GroupMembers] = Json.format[GroupMembers]
}

object GroupMemberRecords {
  implicit val format: OFormat[GroupMemberRecords] = Json.format[GroupMemberRecords]

  private lazy val xgm00000001761Data = Using
    .resource(
      getClass.getResourceAsStream("/data/group-members-details/XGM00000001761.json")
    )(Json.parse)
    .as[GroupMemberRecords]


  def xgm00000001761Model(mgdRegNumber: String): GroupMemberRecords =
    xgm00000001761Data


  private lazy val xgm00000001764Data = Using
    .resource(
      getClass.getResourceAsStream("/data/group-members-details/XGM00000001764.json")
    )(Json.parse)
    .as[GroupMemberRecords]


  private lazy val noData = Using
    .resource(
      getClass.getResourceAsStream("/data/group-members-details/group-members-details.json")
    )(Json.parse)
    .as[GroupMemberRecords]


  def xgm00000001764Model(mgdRegNumber: String): GroupMemberRecords =
    xgm00000001764Data


  def noDataModel(): GroupMemberRecords = noData
}
