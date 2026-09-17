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

package uk.gov.hmrc.gamblingexternalstub.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.gamblingexternalstub.models.OpenPeriod

import java.time.LocalDate

class OpenPeriodCacheServiceSpec extends AnyWordSpec with Matchers {

  private val period = OpenPeriod(consecNo = 1, period = "01/01/2026 - 31/03/2026", dueDate = LocalDate.now(), status = 1)

  "OpenPeriodCacheService" should {

    "never grow beyond the configured maximum size of 1000 entries" in {
      val service = new OpenPeriodCacheService()

      (1 to 1500).foreach(i => service.putAll(s"REG$i", Seq(period)))

      service.size should be <= 1000L
    }
  }
}
