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
import uk.gov.hmrc.gamblingexternalstub.models.ReturnSummary

class ReturnSummarySpec extends AnyWordSpec with Matchers {

  "ReturnSummary JSON format" should {

    "serialize to JSON without totalDueAmount when None" in {
      val model = ReturnSummary(
        mgdRegNumber   = "GAM0000000001",
        returnsDue     = 1,
        returnsOverdue = 0
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber"   -> "GAM0000000001",
        "returnsDue"     -> 1,
        "returnsOverdue" -> 0
      )
    }

    "serialize to JSON with totalDueAmount when defined" in {
      val model = ReturnSummary(
        mgdRegNumber   = "GAM0000000002",
        returnsDue     = 2,
        returnsOverdue = 1,
        totalDueAmount = Some(BigDecimal(100.50))
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber"   -> "GAM0000000002",
        "returnsDue"     -> 2,
        "returnsOverdue" -> 1,
        "totalDueAmount" -> BigDecimal(100.50)
      )
    }

    "deserialize JSON without totalDueAmount to None" in {
      val json = Json.obj(
        "mgdRegNumber"   -> "GAM0000000003",
        "returnsDue"     -> 0,
        "returnsOverdue" -> 0
      )

      val result = json.as[ReturnSummary]

      result shouldBe ReturnSummary(
        mgdRegNumber   = "GAM0000000003",
        returnsDue     = 0,
        returnsOverdue = 0,
        totalDueAmount = None
      )
    }

    "deserialize JSON with totalDueAmount" in {
      val json = Json.obj(
        "mgdRegNumber"   -> "GAM0000000004",
        "returnsDue"     -> 3,
        "returnsOverdue" -> 1,
        "totalDueAmount" -> 250.75
      )

      val result = json.as[ReturnSummary]

      result shouldBe ReturnSummary(
        mgdRegNumber   = "GAM0000000004",
        returnsDue     = 3,
        returnsOverdue = 1,
        totalDueAmount = Some(BigDecimal(250.75))
      )
    }

    "deserialize JSON with explicit null" in {
      val json = Json.obj(
        "mgdRegNumber"   -> "GAM0000000005",
        "returnsDue"     -> 1,
        "returnsOverdue" -> 1,
        "totalDueAmount" -> null
      )

      json.as[ReturnSummary].totalDueAmount shouldBe None
    }
  }
}
