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
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.gamblingexternalstub.models.ReturnSummary

import scala.concurrent.ExecutionContext

class MgdControllerSpec extends AnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val cc: ControllerComponents      = Helpers.stubControllerComponents()

  lazy val controller = new MgdController(cc)

  "MgdController#getReturnSummary" should {

    "return BAD_REQUEST when mgdRegNumber is empty" in {
      val result = controller.getReturnSummary("")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    }

    "return INTERNAL_SERVER_ERROR when mgdRegNumber is 'error'" in {
      val result = controller.getReturnSummary("error")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    }

    "return Scenario 1 response for GAM0000000001" in {
      val result = controller.getReturnSummary("GAM0000000001")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary(
          mgdRegNumber   = "GAM0000000001",
          returnsDue     = 0,
          returnsOverdue = 1
        )
      )
    }

    "return Scenario 2 response for GAM0000000002" in {
      val result = controller.getReturnSummary("GAM0000000002")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary(
          mgdRegNumber   = "GAM0000000002",
          returnsDue     = 0,
          returnsOverdue = 0
        )
      )
    }

    "return default response for unknown mgdRegNumber" in {
      val result = controller.getReturnSummary("GAM9999999999")(FakeRequest())

      status(result) shouldBe OK
      contentAsJson(result) shouldBe Json.toJson(
        ReturnSummary(
          mgdRegNumber   = "GAM9999999999",
          returnsDue     = 2,
          returnsOverdue = 1
        )
      )
    }
  }
}