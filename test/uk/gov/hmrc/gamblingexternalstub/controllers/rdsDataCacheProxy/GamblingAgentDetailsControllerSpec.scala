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

package uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class GamblingAgentDetailsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()

  private def get(ref: String) =
    route(app, FakeRequest(GET, s"/rds-datacache-proxy/gambling/agent-details/$ref")).get

  "GamblingAgentDetailsController#getAgentDetails" should {
    "return agent details" in {
      val result = get("AGENT001")
      status(result)                                      shouldBe OK
      (contentAsJson(result) \ "businessName").as[String] shouldBe "Gambling company 1"
      (contentAsJson(result) \ "email").as[String]        shouldBe "user@example.com"
    }

    "return agent details for any non-empty reference" in {
      Seq("NOTFOUND", "error", "%20AGENT001%20").foreach(ref => status(get(ref)) shouldBe OK)
    }

    "return the matching error for references ending in 400/401/404/500" in {
      Seq(
        "XAM00000000400"       -> BAD_REQUEST,
        "%20XAM00000000401%20" -> UNAUTHORIZED,
        "XAM00000000404"       -> NOT_FOUND,
        "XAM00000000500"       -> INTERNAL_SERVER_ERROR
      ).foreach { case (ref, expected) => status(get(ref)) shouldBe expected }
    }

    "return 500 for a blank reference" in {
      val result = get("%20%20")
      status(result)                              shouldBe INTERNAL_SERVER_ERROR
      (contentAsJson(result) \ "code").as[String] shouldBe "UNEXPECTED_ERROR"
    }
  }
}
