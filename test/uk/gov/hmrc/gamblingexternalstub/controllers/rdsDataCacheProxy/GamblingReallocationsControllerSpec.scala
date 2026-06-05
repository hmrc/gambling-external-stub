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

class GamblingReallocationsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[StubController]

  "GamblingReallocationsController#getReallocations IN" should {

    "return 0 records for XWM00003100200" in {
      val result = controller.getReallocations("XWM00003100200", 1, 10, 0, 1)

      (result \ "totalRecords").as[Int]           shouldBe 0
      (result \ "items").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200" in {
      val result = controller.getReallocations("XWM00003103200", 1, 10, 3, 1)

      (result \ "totalRecords").as[Int]           shouldBe 3
      (result \ "items").as[JsArray].value.length shouldBe 3
    }

    "return first page for XWM00003109200 with pageNo=1 pageSize=5" in {
      val result = controller.getReallocations("XWM00003109200", 1, 5, 9, 1)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 5
    }

    "return second page for XWM00003109200 with pageNo=2 pageSize=5" in {
      val result = controller.getReallocations("XWM00003109200", 2, 5, 9, 1)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 4
    }

    "return 50 total records for XWM00003150200 with pageNo=1 pageSize=10" in {
      val result = controller.getReallocations("XWM00003150200", 1, 10, 50, 1)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }

    "return last page for XWM00003150200 with pageNo=5 pageSize=10" in {
      val result = controller.getReallocations("XWM00003150200", 5, 10, 50, 1)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }
  }

  "GamblingReallocationsController#getReallocations OUT" should {

    "return 0 records for XWM00003100200" in {
      val result = controller.getReallocations("XWM00003100200", 1, 10, 0, 2)

      (result \ "totalRecords").as[Int]           shouldBe 0
      (result \ "items").as[JsArray].value.length shouldBe 0
    }

    "return 3 records for XWM00003103200" in {
      val result = controller.getReallocations("XWM00003103200", 1, 10, 3, 2)

      (result \ "totalRecords").as[Int]           shouldBe 3
      (result \ "items").as[JsArray].value.length shouldBe 3
    }

    "return first page for XWM00003109200 with pageNo=1 pageSize=5" in {
      val result = controller.getReallocations("XWM00003109200", 1, 5, 9, 2)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 5
    }

    "return second page for XWM00003109200 with pageNo=2 pageSize=5" in {
      val result = controller.getReallocations("XWM00003109200", 2, 5, 9, 2)

      (result \ "totalRecords").as[Int]           shouldBe 9
      (result \ "items").as[JsArray].value.length shouldBe 4
    }

    "return 50 total records for XWM00003150200 with pageNo=1 pageSize=10" in {
      val result = controller.getReallocations("XWM00003150200", 1, 10, 50, 2)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }

    "return last page for XWM00003150200 with pageNo=5 pageSize=10" in {
      val result = controller.getReallocations("XWM00003150200", 5, 10, 50, 2)

      (result \ "totalRecords").as[Int]           shouldBe 50
      (result \ "items").as[JsArray].value.length shouldBe 10
    }
  }
}
