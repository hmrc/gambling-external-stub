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
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class PenaltiesSpec extends AnyWordSpec with Matchers {

  "Penalties JSON format" should {

    "serialize to JSON correctly" in {
      val model = Penalties(
        periodStartDate = Some(LocalDate.of(2013, 3, 1)),
        periodEndDate = Some(LocalDate.of(2014, 3, 11)),
        total = BigDecimal(-24500.00),
        totalRecords = 3,
        items = Seq(
          PenaltyItem(
            dateRaised = LocalDate.of(2014, 4, 1),
            descriptionCode = 2680,
            amount = BigDecimal(-9500.00),
            periodStartDate = LocalDate.of(2014, 4, 1),
            periodEndDate = LocalDate.of(2014, 6, 30)
          ),
          PenaltyItem(
            dateRaised = LocalDate.of(2014, 4, 2),
            descriptionCode = 2690,
            amount = BigDecimal(-8000.00),
            periodStartDate = LocalDate.of(2014, 1, 1),
            periodEndDate = LocalDate.of(2014, 3, 31)
          ),
          PenaltyItem(
            dateRaised = LocalDate.of(2014, 4, 3),
            descriptionCode = 2680,
            amount = BigDecimal(-7000.00),
            periodStartDate = LocalDate.of(2013, 10, 1),
            periodEndDate = LocalDate.of(2013, 12, 31)
          )
        )
      )

      val json = Json.toJson(model)

      (json \ "periodStartDate").as[String] shouldBe "2013-03-01"
      (json \ "periodEndDate").as[String] shouldBe "2014-03-11"
      (json \ "total").as[Double] shouldBe -24500.0
      (json \ "totalRecords").as[Int] shouldBe 3

      (json \ "items").as[Seq[PenaltyItem]].size shouldBe 3

      val item1 = (json \ "items")(0)
      (item1 \ "dateRaised").as[String] shouldBe "2014-04-01"
      (item1 \ "descriptionCode").as[Int] shouldBe 2680
      (item1 \ "periodStartDate").as[String] shouldBe "2014-04-01"
      (item1 \ "periodEndDate").as[String] shouldBe "2014-06-30"
      (item1 \ "amount").as[Double] shouldBe -9500.0

      val item2 = (json \ "items")(1)
      (item2 \ "dateRaised").as[String] shouldBe "2014-04-02"
      (item2 \ "descriptionCode").as[Int] shouldBe 2690
      (item2 \ "periodStartDate").as[String] shouldBe "2014-01-01"
      (item2 \ "periodEndDate").as[String] shouldBe "2014-03-31"
      (item2 \ "amount").as[Double] shouldBe -8000.0

      val item3 = (json \ "items")(2)
      (item3 \ "dateRaised").as[String] shouldBe "2014-04-03"
      (item3 \ "descriptionCode").as[Int] shouldBe 2680
      (item3 \ "periodStartDate").as[String] shouldBe "2013-10-01"
      (item3 \ "periodEndDate").as[String] shouldBe "2013-12-31"
      (item3 \ "amount").as[Double] shouldBe -7000.0
    }

    "deserialize from JSON correctly" in {
      val json = Json.parse(
        s"""{
           |  "periodStartDate":"${LocalDate.of(2013, 3, 1)}",
           |  "periodEndDate":"${LocalDate.of(2014, 3, 11)}",
           |  "total":-24500.0,
           |  "totalRecords":3,
           |  "items":[
           |    {
           |      "dateRaised":"${LocalDate.of(2014, 4, 1)}",
           |      "descriptionCode":2680,
           |      "amount":-9500.0,
           |      "periodStartDate":"${LocalDate.of(2014, 4, 1)}",
           |      "periodEndDate":"${LocalDate.of(2014, 6, 30)}"
           |    },
           |    {
           |      "dateRaised":"${LocalDate.of(2014, 4, 2)}",
           |      "descriptionCode":2690,
           |      "amount":-8000.0,
           |      "periodStartDate":"${LocalDate.of(2014, 1, 1)}",
           |      "periodEndDate":"${LocalDate.of(2014, 3, 31)}"
           |    },
           |    {
           |      "dateRaised":"${LocalDate.of(2014, 4, 3)}",
           |      "descriptionCode":2680,
           |      "amount":-7000.0,
           |      "periodStartDate":"${LocalDate.of(2013, 10, 1)}",
           |      "periodEndDate":"${LocalDate.of(2013, 12, 31)}"
           |    }
           |  ]
           |}""".stripMargin
      )

      val result = json.validate[Penalties]

      result shouldBe JsSuccess(
        Penalties(
          periodStartDate = Some(LocalDate.of(2013, 3, 1)),
          periodEndDate = Some(LocalDate.of(2014, 3, 11)),
          total = BigDecimal(-24500.00),
          totalRecords = 3,
          items = Seq(
            PenaltyItem(
              dateRaised = LocalDate.of(2014, 4, 1),
              descriptionCode = 2680,
              amount = BigDecimal(-9500.00),
              periodStartDate = LocalDate.of(2014, 4, 1),
              periodEndDate = LocalDate.of(2014, 6, 30)
            ),
            PenaltyItem(
              dateRaised = LocalDate.of(2014, 4, 2),
              descriptionCode = 2690,
              amount = BigDecimal(-8000.00),
              periodStartDate = LocalDate.of(2014, 1, 1),
              periodEndDate = LocalDate.of(2014, 3, 31)
            ),
            PenaltyItem(
              dateRaised = LocalDate.of(2014, 4, 3),
              descriptionCode = 2680,
              amount = BigDecimal(-7000.00),
              periodStartDate = LocalDate.of(2013, 10, 1),
              periodEndDate = LocalDate.of(2013, 12, 31)
            )
          )
        )
      )
    }
  }
}
