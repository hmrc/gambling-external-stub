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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import java.time.LocalDate

class GroupMembersDetailsSpec extends AnyWordSpec with Matchers {

  private val fixedDate = LocalDate.parse("2026-01-01")

  "GroupMembers Details JSON format" should {

    "serialize to JSON when defined" in {

      val model = GroupMembers(
        "XRM00000000574",
        businessPartnerNumber = Some("0"),
        businessName        = Some("0"),
        CRN        = Some("0"),
        foreignCorporateRef   = Some("0"),
        isRepMemberOfGroup            = Some("1"),
        dateOfJoining           = Some(fixedDate),
        dateOfLeaving           = Some(fixedDate),
        emailAddr            = Some("0"),
        systemDate   = Some(fixedDate)
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber" -> "XRM00000000574",
        "businessPartnerNumber"     -> "0",
        "businessName"     -> "0",
        "CRN"     -> "0",
        "foreignCorporateRef"     -> "0",
        "isRepMemberOfGroup"     -> "1",
        "dateOfJoining"     -> Some(fixedDate),
        "dateOfLeaving"     -> Some(fixedDate),
        "emailAddr"     -> "0",
        "systemDate"   -> Some(fixedDate)
      )
    }

    "deserialize JSON" in {

      val json = Json.obj(
        "mgdRegNumber" -> "XRM00000000574",
        "businessPartnerNumber"     -> "4",
        "businessName"     -> "4",
        "CRN"     -> "4",
        "foreignCorporateRef"     -> "4",
        "isRepMemberOfGroup"     -> "1",
        "dateOfJoining"     -> Some(fixedDate),
        "dateOfLeaving"     -> Some(fixedDate),
        "emailAddr"     -> "1",
        "systemDate"   -> Some(fixedDate)
      )

      val result = json.as[GroupMembers]

      result shouldBe GroupMembers(
        "XRM00000000574",
        businessPartnerNumber = Some("4"),
        businessName        = Some("4"),
        CRN        = Some("4"),
        foreignCorporateRef   = Some("4"),
        isRepMemberOfGroup            = Some("1"),
        dateOfJoining           = Some(fixedDate),
        dateOfLeaving           = Some(fixedDate),
        emailAddr            = Some("1"),
        systemDate   = Some(fixedDate)
      )
    }

    "deserialize missing optional fields as None" in {

      val json = Json.obj(
        "mgdRegNumber" -> "XRM00000000574"
      )

      val result = json.as[GroupMembers]

      result shouldBe GroupMembers(
        mgdRegNumber = "XRM00000000574",
        businessPartnerNumber = None,
        businessName        = None,
        CRN        = None,
        foreignCorporateRef   = None,
        isRepMemberOfGroup            = None,
        dateOfJoining           = None,
        dateOfLeaving           = None,
        emailAddr            = None,
        systemDate   = None
      )
    }
  }
}
