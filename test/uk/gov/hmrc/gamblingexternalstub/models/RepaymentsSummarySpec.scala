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

class RepaymentsSummarySpec extends AnyWordSpec with Matchers {

  "RepaymentsSummary JSON format" should {

    "serialize to JSON when defined" in {
      val model = RepaymentsSummary(
        periodStartDate                = Some(LocalDate.of(2013, 3, 1)),
        periodEndDate                  = Some(LocalDate.of(2014, 3, 11)),
        actualRepaymentsAmount         = BigDecimal(71.84),
        repaymentsInterestRepaidAmount = BigDecimal(-35.76),
        total                          = BigDecimal(36.08)
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "periodStartDate"                -> "2013-03-01",
        "periodEndDate"                  -> "2014-03-11",
        "actualRepaymentsAmount"         -> 71.84,
        "repaymentsInterestRepaidAmount" -> -35.76,
        "total"                          -> 36.08
      )
    }

    "deserialize JSON" in {
      val json = Json.obj(
        "periodStartDate"                -> "2013-03-01",
        "periodEndDate"                  -> "2014-03-11",
        "actualRepaymentsAmount"         -> 71.84,
        "repaymentsInterestRepaidAmount" -> -35.76,
        "total"                          -> 36.08
      )

      val result = json.as[RepaymentsSummary]

      result shouldBe RepaymentsSummary(
        periodStartDate                = Some(LocalDate.of(2013, 3, 1)),
        periodEndDate                  = Some(LocalDate.of(2014, 3, 11)),
        actualRepaymentsAmount         = BigDecimal(71.84),
        repaymentsInterestRepaidAmount = BigDecimal(-35.76),
        total                          = BigDecimal(36.08)
      )
    }
  }
}
