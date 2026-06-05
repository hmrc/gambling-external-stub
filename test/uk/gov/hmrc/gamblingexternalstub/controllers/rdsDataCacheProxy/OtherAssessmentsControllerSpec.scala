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
import play.api.libs.json.JsArray
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase

class OtherAssessmentsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[StubController]

  // Reg number convention: last 3 digits = HTTP status, 4th+5th from right = 2-digit record count
  // e.g. XWM00003100404 (404), XWM00003100500 (500), XWM00003103200 (200, 3 records), XWM00003150200 (200, 50 records)
  "GamblingOtherAssessmentsController#getOtherAssessments" should {

    "return 0 records for XWM00003100200 (last 3 = 200, 4th+5th from right = 00)" in {
      val result = controller.getOtherAssessments("XWM00003100200", 1, 10, 0)

      (result \ "totalRecords").as[Int]           shouldBe 0
      (result \ "items").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200 (last 3 = 200, 4th+5th from right = 03)" in {
      val result = controller.getOtherAssessments("XWM00003103200", 1, 10, 3)

      (result \ "totalRecords").as[Int]           shouldBe 3
      (result \ "items").as[JsArray].value.length shouldBe 3
    }

    "return first page for XWM00003109200 (9 records) with pageNo=1 pageSize=5" in {
      val result = controller.getOtherAssessments("XWM00003109200", 1, 5, 9)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 5
    }

    "return second page for XWM00003109200 (9 records) with pageNo=2 pageSize=5" in {
      val result = controller.getOtherAssessments("XWM00003109200", 2, 5, 9)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 4
    }

    "return 50 total records for XWM00003150200 with pageNo=1 pageSize=10" in {
      val result = controller.getOtherAssessments("XWM00003150200", 1, 10, 50)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }

    "return last page for XWM00003150200 with pageNo=5 pageSize=10" in {
      val result = controller.getOtherAssessments("XWM00003150200", 5, 10, 50)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }
  }
}
