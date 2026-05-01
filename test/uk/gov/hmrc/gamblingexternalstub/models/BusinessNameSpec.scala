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

class BusinessNameSpec extends AnyWordSpec with Matchers {

  "Business Name JSON format" should {


    "serialize to JSON when defined" in {
      val dateBusinessName = LocalDate.of(1991, 4, 7)
      val model = BusinessName(
        mgdRegNumber = "GAM0000000001",
        solePropTitle = Some("Mr"),
        solePropFirstName = Some("Foo"),
        solePropMidName = Some("B"),
        solePropLastName = Some("Bar"),
        businessName = Some("Foo Bar Co"),
        businessType = Some(1),
        tradingName = Some("FooBar"),
        systemDate = Some(dateBusinessName))

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber" -> "GAM0000000001",
        "solePropTitle" -> "Mr",
        "solePropFirstName" -> "Foo",
        "solePropMidName" -> "B",
        "solePropLastName" -> "Bar",
        "businessName" -> "Foo Bar Co",
        "businessType" -> 1,
        "tradingName" -> "FooBar",
        "systemDate" -> dateBusinessName
      )
    }


      "deserialize JSON" in {
        val dateBusinessName = LocalDate.of(1991, 4, 7)
        val json = Json.obj(
          "mgdRegNumber" -> "GAM0000000002",
          "solePropTitle" -> "abc",
          "solePropFirstName" -> "abc",
          "solePropMidName" -> "abc",
          "solePropLastName" -> "abc",
          "businessName" -> "abc",
          "businessType" -> 1,
          "tradingName" -> "abc",
          "systemDate" -> dateBusinessName
        )

        val result = json.as[BusinessName]

        result shouldBe BusinessName(
          mgdRegNumber = "GAM0000000002",
          solePropTitle = Some("abc"),
          solePropFirstName = Some("abc"),
          solePropMidName = Some("abc"),
          solePropLastName = Some("abc"),
          businessName = Some("abc"),
          businessType = Some(1),
          tradingName = Some("abc"),
          systemDate = Some(dateBusinessName)
        )

    }
  }
}