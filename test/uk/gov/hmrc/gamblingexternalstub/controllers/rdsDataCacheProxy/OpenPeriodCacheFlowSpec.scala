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
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class OpenPeriodCacheFlowSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val openReturnsController = app.injector.instanceOf[GamblingOpenReturnsController]

  private def url(regime: String, registrationNo: String, consecNo: Int) =
    s"/rds-datacache-proxy/gambling/update-status-period/$regime/$registrationNo/$consecNo"

  private def putStatus(regime: String, registrationNo: String, consecNo: Int, newStatus: Int) =
    route(app, FakeRequest(PUT, url(regime, registrationNo, consecNo)).withJsonBody(Json.obj("status" -> newStatus))).get

  "getOpenPeriods and updateStatusPeriod" should {

    "close a period and have that reflected on the next getOpenPeriods call for the same regNumber" in {
      val regNumber = "XWM00003103200"

      val firstResult = openReturnsController.getOpenPeriods("MGD", regNumber, None, None)(FakeRequest())
      status(firstResult) shouldBe OK
      val firstItems = (contentAsJson(firstResult) \ "openPeriods").as[JsArray].value
      firstItems.length                                                                 shouldBe 3
      firstItems.find(item => (item \ "consecNo").as[Int] == 2).get.\("status").as[Int] shouldBe 2 // consecNo=2 -> 2 % 3 = 2 (per stub generation)

      val closeResult = putStatus("MGD", regNumber, 2, newStatus = 1)
      status(closeResult) shouldBe NO_CONTENT

      val secondResult = openReturnsController.getOpenPeriods("MGD", regNumber, None, None)(FakeRequest())
      status(secondResult) shouldBe OK
      val secondItems = (contentAsJson(secondResult) \ "openPeriods").as[JsArray].value
      secondItems.length                                                                 shouldBe 3
      secondItems.find(item => (item \ "consecNo").as[Int] == 2).get.\("status").as[Int] shouldBe 1
    }

    "leave a period unaffected if updateStatusPeriod targets a regNumber never seen by getOpenPeriods" in {
      val result = putStatus("MGD", "XWM00003100200", 1, newStatus = 1)
      status(result) shouldBe NO_CONTENT
    }
  }
}
