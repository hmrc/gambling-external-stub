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
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.gamblingexternalstub.base.SpecBase
import uk.gov.hmrc.gamblingexternalstub.models.PartnerDetails
import uk.gov.hmrc.gamblingexternalstub.models.PartnerFormats.*

import scala.concurrent.Future

class PartnerDetailsControllerSpec extends AnyWordSpec with Matchers with SpecBase {

  private val app = applicationBuilder().build()
  private val controller = app.injector.instanceOf[PartnerDetailsController]

  "PartnerDetailsController#getPartnerDetails" should {

    "return full partner details for XPM00000000600" in {
      val result = controller.getPartnerDetails("MGD", "XPM00000000600")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String] shouldBe "XPM00000000600"
      (json \ "partners" \ 0 \ "businessPartnerNumber").as[String] shouldBe "BPN000000001"
      (json \ "partners" \ 0 \ "businessType").as[Int] shouldBe 1
      (json \ "partners" \ 0 \ "tradingName").asOpt[String] shouldBe None
    }

    "return full partner details for XMM00000001177" in new Context {

      val result: Future[Result] = controller.getPartnerDetails("MGD", "XMM00000001177")(FakeRequest())

      status(result) shouldBe OK

      val json: JsValue = contentAsJson(result)
      val jsonRes: PartnerDetails = Json.parse(json.toString).as[PartnerDetails]
      val expected: PartnerDetails = Json.parse(jsonForXMM00000001177).as[PartnerDetails]

      jsonRes shouldBe expected

    }

    "return full partner details for XPM00000000985" in new Context {

      val result: Future[Result] = controller.getPartnerDetails("MGD", "XPM00000000985")(FakeRequest())

      status(result) shouldBe OK

      val json: JsValue = contentAsJson(result)
      val jsonRes: PartnerDetails = Json.parse(json.toString).as[PartnerDetails]
      val expected: PartnerDetails = Json.parse(jsonForXPM00000000985).as[PartnerDetails]

      jsonRes shouldBe expected

    }

    "return partial partner details for XJM00000000570" in {
      val result = controller.getPartnerDetails("mGd", " XJM00000000570 ")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String]              shouldBe "XJM00000000570"
      (json \ "partners" \ 0 \ "businessPartnerNumber").asOpt[String]  shouldBe Some("0100049899")
      (json \ "partners" \ 0 \ "businessName").as[String]              shouldBe "Partner1"
      (json \ "partners" \ 0 \ "countryOfIncorporation").asOpt[String] shouldBe Some("countryOfIncorporation")
    }

    "return no partner details for XGM00000001763" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000001763")(FakeRequest())

      status(result) shouldBe OK

      val json = contentAsJson(result)

      (json \ "partners" \ 0 \ "mgdRegNumber").as[String]    shouldBe "XGM00000001763"
      (json \ "partners" \ 0 \ "businessName").asOpt[String] shouldBe None
    }

    "return BAD_REQUEST for an unrecognised regime" in {
      val regime = "nope"
      val result = controller.getPartnerDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for PartnerDetails"
      )
    }

    "return BAD_REQUEST for an unsupported regime" in {
      val regime = "PBD"
      val result = controller.getPartnerDetails(regime, "XWM00003100200")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REGIME",
        "message" -> s"Regime $regime is not supported for PartnerDetails"
      )
    }

    "return BadRequest for XGM00000000560" in {
      val result = controller.getPartnerDetails("MGD", "XGM00000000560")(FakeRequest())

      status(result) shouldBe BAD_REQUEST
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "INVALID_REQUEST",
        "message" -> "Bad request"
      )

    }

    "return Unauthorized for XMM00000000580" in {
      val result = controller.getPartnerDetails("MGD", "XMM00000000580")(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNAUTHORIZED",
        "message" -> "Unauthorized to access this resource"
      )

    }

    "return InternalServerError for XAM00000001090" in {
      val result = controller.getPartnerDetails("MGD", "XAM00000001090")(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )

    }

  }

  sealed trait Context {
    val jsonForXMM00000001177: String =
      """{
          |  "partners": [
          |    {
          |      "mgdRegNumber": "XMM00000001177",
          |      "dateOfJoining": "2013-02-01",
          |      "dateOfLeaving": "1999-12-31",
          |      "businessName": "Corporate Body Name",
          |      "tradingName": "Trading Name",
          |      "utr": "4444444444",
          |      "crn": "01234556",
          |      "dateOfIncorporation": "2012-10-29",
          |      "address1": "1 address",
          |      "address2": "2 address",
          |      "postcode": "AD34 4FD",
          |      "adi": "add",
          |      "iomOrCiFlag": "false",
          |      "isFutureLeaveDate": 0,
          |      "isFutureJoinDate": 0,
          |      "businessType": 2
          |    },
          |    {
          |      "mgdRegNumber": "XMM00000001177",
          |      "dateOfJoining": "2013-02-01",
          |      "dateOfLeaving": "1999-12-31",
          |      "businessName": "Unicorporated Body Name",
          |      "tradingName": "Trading Name",
          |      "utr": "4444444444",
          |      "address1": "1 Address Line 1",
          |      "address2": "2 Address Line 2",
          |      "postcode": "AD23 9JJ",
          |      "iomOrCiFlag": "false",
          |      "isFutureLeaveDate": 0,
          |      "isFutureJoinDate": 0,
          |      "businessType": 3
          |    }
          |  ],
          |  "systemDate": "2026-07-31"
          |}""".stripMargin

    val jsonForXPM00000000985: String =
      """{
        |  "partners": [
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2013-02-01",
        |      "dateOfLeaving": "1999-12-31",
        |      "solePropTitle": "Mr",
        |      "solePropFirstName": "STEPHEN",
        |      "solePropMiddleName": "ADAM",
        |      "solePropLastName": "CRYPTON",
        |      "dateOfBirth": "1960-01-06",
        |      "nino": "BT100028B",
        |      "address1": "10 LONG ROAD",
        |      "address2": "BRIGHTON",
        |      "postcode": "BN12 3KJ",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "0044444 444 444",
        |      "mobilePhoneNumber": "0044444 444 445",
        |      "faxNumber": "0044444 444 446",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 1
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "BDP PARTNERS",
        |      "tradingName": "BDP PARTNERS",
        |      "utr": "5177008741",
        |      "address1": "10 RING ROAD",
        |      "address2": "LUTON",
        |      "postcode": "LN12 4RT",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "0044 777 777 777 004",
        |      "mobilePhoneNumber": "0044 777 777 777 005",
        |      "faxNumber": "0044 777 777 777 006",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 4
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "BROADVIEW BUILDERS LIMITED",
        |      "tradingName": "BROADVIEW BUILDERS LIMITED",
        |      "dateOfIncorporation": "1991-12-19",
        |      "address1": "CENTURY PLACE LAMBERTS ROAD",
        |      "address2": "TUNBRIDGE WELLS",
        |      "address3": "KENT",
        |      "postcode": "TN2 3EH",
        |      "adi": "AUKER HUTTON MLS BUSINESS CENTRE",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "123",
        |      "mobilePhoneNumber": "345",
        |      "faxNumber": "11",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 5
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "JB PARTNERS",
        |      "tradingName": "JB PARTNERS",
        |      "utr": "2177013303",
        |      "address1": "44 HIGH STREET",
        |      "address2": "WORTHING",
        |      "postcode": "BN12 4XJ",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "01452 252525",
        |      "mobilePhoneNumber": "01452 252526",
        |      "faxNumber": "01452 252527",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 4
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "BELLEVUE PROPERTY INVESTMENTS LIMITED",
        |      "tradingName": "BELLEVUE PROPERTY INVESTMENTS LIMITED",
        |      "dateOfIncorporation": "1992-01-20",
        |      "address1": "109 SILVERDALE AVE",
        |      "address2": "WALTON-ON-THAMES",
        |      "address3": "SURREY",
        |      "postcode": "KT12 1EH",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "0044 111 111 1111",
        |      "mobilePhoneNumber": "0044 111 111 1112",
        |      "faxNumber": "0044 111 111 1113",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 5
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "solePropTitle": "Mr",
        |      "solePropFirstName": "T",
        |      "solePropLastName": "PERRY",
        |      "dateOfBirth": "1960-06-12",
        |      "nino": "GY002534B",
        |      "address1": "12 LOW ROAD",
        |      "address2": "SUTTON",
        |      "postcode": "BN12 5RT",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "12",
        |      "mobilePhoneNumber": "13",
        |      "faxNumber": "14",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 1
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "solePropTitle": "Mrs",
        |      "solePropFirstName": "ELLEN",
        |      "solePropLastName": "EGGPLANT",
        |      "dateOfBirth": "1960-06-13",
        |      "nino": "ST006004A",
        |      "address1": "22 GROVE ROAD",
        |      "address2": "BRIGHTON",
        |      "postcode": "BN44 7TL",
        |      "iomOrCiFlag": "false",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 1
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "PARTNER 2",
        |      "tradingName": "PARTNER 2",
        |      "utr": "4177013315",
        |      "address1": "12 RED ROAD",
        |      "address2": "WORTHING",
        |      "postcode": "WR23 4RY",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "1",
        |      "mobilePhoneNumber": "2",
        |      "faxNumber": "3",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 4
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "PARTNER 03",
        |      "tradingName": "PARTNER 03",
        |      "utr": "2177013303",
        |      "address1": "7 TOWERS STREET",
        |      "address2": "LIVERPOOL",
        |      "postcode": "LP32 2YY",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "33",
        |      "mobilePhoneNumber": "34",
        |      "faxNumber": "12",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 4
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "solePropTitle": "Mr",
        |      "solePropFirstName": "H",
        |      "solePropLastName": "HOLLAND",
        |      "dateOfBirth": "1960-06-12",
        |      "nino": "MT000080D",
        |      "address1": "44 RED ROAD",
        |      "address2": "BRIGHTON",
        |      "postcode": "BN34 5RT",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "0044 666 666 666",
        |      "mobilePhoneNumber": "0044 666 666 667",
        |      "faxNumber": "0044 666 666 668",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 1
        |    },
        |    {
        |      "mgdRegNumber": "XPM00000000985",
        |      "dateOfJoining": "2012-10-15",
        |      "dateOfLeaving": "1999-12-31",
        |      "businessName": "B.G. MIDDLETON (DEVELOPMENTS) LIMITED",
        |      "tradingName": "B.G. MIDDLETON (DEVELOPMENTS) LIMITED",
        |      "dateOfIncorporation": "1991-05-31",
        |      "address1": "RAFTERS",
        |      "address2": "BROADHEMBURY",
        |      "address3": "HONITON",
        |      "address4": "DEVON EX14 3NQ",
        |      "postcode": "EX14 3NQ",
        |      "iomOrCiFlag": "false",
        |      "phoneNumber": "0044 111 111 1111",
        |      "mobilePhoneNumber": "0044 111 111 1122",
        |      "faxNumber": "0044 111 111 1133",
        |      "isFutureLeaveDate": 0,
        |      "isFutureJoinDate": 0,
        |      "businessType": 5
        |    }
        |  ],
        |  "systemDate": "2026-08-06"
        |}""".stripMargin

  }

}
