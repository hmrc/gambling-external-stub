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
import play.api.libs.json.{JsResult, JsSuccess, Json}

import java.time.LocalDate

class InterestOverviewSpec extends AnyWordSpec with Matchers {

  val validResponseInterestOverview = InterestOverview(
    periodStartDate         = Some(LocalDate.of(2013, 3, 1)),
    periodEndDate           = Some(LocalDate.of(2014, 3, 11)),
    interestAmount          = BigDecimal(-81.84),
    interestAccruingAmount  = BigDecimal(-25.76),
    repaymentInterestAmount = BigDecimal(41.23),
    total                   = BigDecimal(66.37)
  )

  "InterestOverview JSON format" should {

    "serialize to JSON correctly" in {
      val json = Json.toJson(validResponseInterestOverview)

      (json \ "periodStartDate").as[String]         shouldBe "2013-03-01"
      (json \ "periodEndDate").as[String]           shouldBe "2014-03-11"
      (json \ "interestAmount").as[Double]          shouldBe -81.84
      (json \ "interestAccruingAmount").as[Double]  shouldBe -25.76
      (json \ "repaymentInterestAmount").as[Double] shouldBe 41.23
      (json \ "total").as[Double]                   shouldBe 66.37
    }

    "deserialize from JSON correctly" in {
      val json = Json.parse(
        s"""{
           |  "periodStartDate":"2013-03-01",
           |  "periodEndDate":"2014-03-11",
           |  "interestAmount":-81.84,
           |  "interestAccruingAmount":-25.76,
           |  "repaymentInterestAmount":41.23,
           |  "total":66.37
           |}""".stripMargin
      )

      val result: JsResult[InterestOverview] = json.validate[InterestOverview]

      result shouldBe JsSuccess(validResponseInterestOverview)
    }

    "round-trip write then read should return same object" in {
      val json = Json.toJson(validResponseInterestOverview)
      val parsed = json.as[InterestOverview]

      parsed shouldBe validResponseInterestOverview
    }

    "fail to deserialize when required fields are missing" in {
      val json = Json.parse(
        """
          |{
          |  "periodStartDate":"2013-03-01"
          |}
          |""".stripMargin
      )

      val result = json.validate[InterestOverview]

      result.isError shouldBe true
    }

    "fail to deserialize when field types are incorrect" in {
      val json = Json.parse(
        s"""{
           |  "periodStartDate":"2013-03-01",
           |  "periodEndDate":"2014-03-11",
           |  "interestAmount":"£81.84",
           |  "interestAccruingAmount":-25.76,
           |  "repaymentInterestAmount":41.23,
           |  "total":66.37
           |}""".stripMargin
      )

      val result = json.validate[InterestOverview]

      result.isError shouldBe true
    }
  }
}
