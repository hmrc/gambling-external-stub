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

class BusinessContactDetailsSpec extends AnyWordSpec with Matchers {

  "BusinessContactDetails" should {

    "write to JSON" in {
      val model = BusinessContactDetails(
        mgdRegNumber      = "XRM00000000574",
        phoneNumber       = "123456789",
        mobilePhoneNumber = "123456789",
        faxNumber         = "123456789",
        emailAddr         = "viv@xyz.com",
        systemDate        = "2026-05-31"
      )

      Json.toJson(model) shouldBe Json.obj(
        "mgdRegNumber"      -> "XRM00000000574",
        "phoneNumber"       -> "123456789",
        "mobilePhoneNumber" -> "123456789",
        "faxNumber"         -> "123456789",
        "emailAddr"         -> "viv@xyz.com",
        "systemDate"        -> "2026-05-31"
      )
    }
  }
}
