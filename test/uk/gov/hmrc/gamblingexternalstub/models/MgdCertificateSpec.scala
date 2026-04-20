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

class MgdCertificateSpec extends AnyWordSpec with Matchers {

  "PartnerMember" should {
    "serialize and deserialize correctly" in {
      val model = PartnerMember(
        namesOfPartMems    = "Test Partner Ltd",
        solePropTitle      = Some("Mr"),
        solePropFirstName  = Some("John"),
        solePropMiddleName = None,
        solePropLastName   = Some("Doe"),
        typeOfBusiness     = 2
      )

      val json = Json.toJson(model)
      json.as[PartnerMember] shouldBe model
    }
  }

  "GroupMember" should {
    "serialize and deserialize correctly" in {
      val model = GroupMember("Group Ltd")

      val json = Json.toJson(model)
      json.as[GroupMember] shouldBe model
    }
  }

  "ReturnPeriodEndDate" should {
    "serialize LocalDate in ISO format" in {
      val model = ReturnPeriodEndDate(LocalDate.parse("2026-03-31"))

      val json = Json.toJson(model)

      (json \ "returnPeriodEndDate").as[String] shouldBe "2026-03-31"
    }

    "deserialize correctly" in {
      val json = Json.parse(
        """
          |{
          |  "returnPeriodEndDate": "2026-03-31"
          |}
          |""".stripMargin
      )

      json.as[ReturnPeriodEndDate] shouldBe
        ReturnPeriodEndDate(LocalDate.parse("2026-03-31"))
    }
  }

  "MgdCertificate" should {

    "serialize and deserialize correctly" in {
      val model = MgdCertificate.sample1("GAM0000000001")

      val json = Json.toJson(model)
      json.as[MgdCertificate] shouldBe model
    }

    "serialize dates in ISO format" in {
      val model = MgdCertificate.sample1("GAM0000000001")

      val json = Json.toJson(model)

      (json \ "registrationDate").as[String] shouldBe "2023-01-15"
      (json \ "dateCertIssued").as[String]   shouldBe "2024-02-01"
    }

    "have correct sample1 structure" in {
      val model = MgdCertificate.sample1("GAM0000000001")

      model.mgdRegNumber shouldBe "GAM0000000001"

      // FIXED: Option[Int]
      model.noOfPartners shouldBe Some(2)
      model.noOfGroupMems shouldBe Some(1)

      model.groupReg shouldBe "Y"

      model.partMembers.size shouldBe 2
      model.groupMembers.size shouldBe 1
      model.returnPeriodEndDates.size shouldBe 5
    }
  }
}