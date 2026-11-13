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

class GroupMembersDetailsSpec extends AnyWordSpec with Matchers {


  "MemberDetails JSON format" should {

    "serialize to JSON when defined" in {

      val model = MemberDetails(
        "XGM00000001761",
        businessPartnerNumber = Some("0100053091"), businessName = Some("BRUCE HOPKINS LIMITED"), tradingName = Some(" Trading name 1"), crn = Some(""),
        dateOfIncorporation = Some(LocalDate.of(2020,2,15)), countryOfIncorporation = Some("Spain"), foreignCorporateRef = Some(""), utr = Some(1L), vrn = Some(1L), tradeClass = Some(1L),
        businessActivityDesc = Some(""), phoneNumber = Some(""), mobilePhoneNumber = Some(""), faxNumber = Some(""), emailAddr = Some(""), adi = Some(""),
        address1 = Some("Address 1"), address2 = Some("Address 2"), address3 = Some("Address 3"), address4 = Some(""), postcode = Some(""), isIomOrCiFlag = Some("0"), haveGamblingLicenceNo = Some("1"),
        gamblingLicenceNo = Some("123-456789-A-123456-789"), heldByLandlord = Some("1"), localAuthority = Some("1"), familyEntertainment = Some("0"), clubGaming = Some("0"), clubLicence = Some("1"),
        prizeGaming = Some("0"), onPremises = Some("1"), clubPremises = Some("0"), regCert = Some("0"), bookmaking = Some("0"), bingo = Some("0"), amusement = Some("0"),
        serveAlcohol = Some("0"), premisesNotCovered = Some("0"), previousMgdrn1 = Some("XAM00000000327"), previousMgdrn2 = Some("XNM00000000457"), previousMgdrn3 = Some("XHM00000000177"), associatedMgdrn1 = Some("XYM00000000287"),
        associatedMgdrn2 = Some(""), associatedMgdrn3 = Some(""), dateOfJoining = Some(LocalDate.of(2013,2,1)), dateOfLeaving = Some(LocalDate.of(2023,3,1)), isRepMemSameAsCb = Some("0"),
        isUkIncorporated = Some("0"), isRepMemberOfGroup = Some("1"), systemDate = Some(LocalDate.of(2026,11,4)),
        premises = Seq(PremisesDetails("XCM00000001236", Some(" 1.Dalford court "),
          Some(" TeLford "), Some(" Shropshire "),
          Some(" "),
          Some("TF3 2BP"), Some(LocalDate.of(2026,6,30))),PremisesDetails("XCM00000001236", Some(" 2.Dalford court "),
          Some(" TeLford "), Some(" Shropshire "),
          Some(" "),
          Some("TF3 2BP"), Some(LocalDate.of(2026,6,30)))
        )
      )

      val json = Json.toJson(model)

      json shouldBe Json.obj(
        "mgdRegNumber"-> "XGM00000001761",
        "businessPartnerNumber"-> "0100053091",
        "businessName"-> "BRUCE HOPKINS LIMITED",
        "tradingName"-> " Trading name 1",
        "crn"-> "",
        "dateOfIncorporation"-> "2020-02-15",
        "countryOfIncorporation"-> "Spain",
        "foreignCorporateRef"-> "",
        "utr"-> 1,
        "vrn"-> 1,
        "tradeClass"-> 1,
        "businessActivityDesc"-> "",
        "phoneNumber"-> "",
        "mobilePhoneNumber"-> "",
        "faxNumber"-> "",
        "emailAddr"-> "",
        "adi"-> "",
        "address1"-> "Address 1",
        "address2"-> "Address 2",
        "address3"-> "Address 3",
        "address4"-> "",
        "postcode"-> "",
        "isIomOrCiFlag"-> "0",
        "haveGamblingLicenceNo"-> "1",
        "gamblingLicenceNo"-> "123-456789-A-123456-789",
        "heldByLandlord"-> "1",
        "localAuthority"-> "1",
        "familyEntertainment"-> "0",
        "clubGaming"-> "0",
        "clubLicence"-> "1",
        "prizeGaming"-> "0",
        "onPremises"-> "1",
        "clubPremises"-> "0",
        "regCert"-> "0",
        "bookmaking"-> "0",
        "bingo"-> "0",
        "amusement"-> "0",
        "serveAlcohol"-> "0",
        "premisesNotCovered"-> "0",
        "previousMgdrn1"-> "XAM00000000327",
        "previousMgdrn2"-> "XNM00000000457",
        "previousMgdrn3"-> "XHM00000000177",
        "associatedMgdrn1"-> "XYM00000000287",
        "associatedMgdrn2"-> "",
        "associatedMgdrn3"-> "",
        "dateOfJoining"-> "2013-02-01",
        "dateOfLeaving"-> "2023-03-01",
        "isRepMemSameAsCb"-> "0",
        "isUkIncorporated"-> "0",
        "isRepMemberOfGroup"-> "1",
        "systemDate"-> "2026-11-04",
        "premises" -> Json.arr(
          Json.obj(
            "mgdRegNumber" -> "XCM00000001236",
            "address1" -> " 1.Dalford court ",
            "address2" -> " TeLford ",
            "address3" -> " Shropshire ",
            "address4" -> " ",
            "postcode" -> "TF3 2BP",
            "systemDate" -> "2026-06-30"
          ),
          Json.obj(
            "mgdRegNumber" -> "XCM00000001236",
            "address1" -> " 2.Dalford court ",
            "address2" -> " TeLford ",
            "address3" -> " Shropshire ",
            "address4" -> " ",
            "postcode" -> "TF3 2BP",
            "systemDate" -> "2026-06-30"
          )
        )
      )
    }

    "deserialize JSON" in {

      val json = Json.obj(
        "mgdRegNumber"-> "XWM00000001020",
        "businessPartnerNumber"-> "0100053091",
        "businessName"-> "BRUCE HOPKINS LIMITED",
        "tradingName"-> " Trading name 1",
        "crn"-> "",
        "dateOfIncorporation"-> "2020-02-15",
        "countryOfIncorporation"-> "Spain",
        "foreignCorporateRef"-> "",
        "utr"-> 1,
        "vrn"-> 1,
        "tradeClass"-> 1,
        "businessActivityDesc"-> "",
        "phoneNumber"-> "",
        "mobilePhoneNumber"-> "",
        "faxNumber"-> "",
        "emailAddr"-> "",
        "adi"-> "",
        "address1"-> "Address 1",
        "address2"-> "Address 2",
        "address3"-> "Address 3",
        "address4"-> "",
        "postcode"-> "",
        "isIomOrCiFlag"-> "0",
        "haveGamblingLicenceNo"-> "1",
        "gamblingLicenceNo"-> "123-456789-A-123456-789",
        "heldByLandlord"-> "1",
        "localAuthority"-> "1",
        "familyEntertainment"-> "0",
        "clubGaming"-> "0",
        "clubLicence"-> "1",
        "prizeGaming"-> "0",
        "onPremises"-> "1",
        "clubPremises"-> "0",
        "regCert"-> "0",
        "bookmaking"-> "0",
        "bingo"-> "0",
        "amusement"-> "0",
        "serveAlcohol"-> "0",
        "premisesNotCovered"-> "0",
        "previousMgdrn1"-> "XAM00000000327",
        "previousMgdrn2"-> "XNM00000000457",
        "previousMgdrn3"-> "XHM00000000177",
        "associatedMgdrn1"-> "XYM00000000287",
        "associatedMgdrn2"-> "",
        "associatedMgdrn3"-> "",
        "dateOfJoining"-> "2013-02-01",
        "dateOfLeaving"-> "2023-03-01",
        "isRepMemSameAsCb"-> "0",
        "isUkIncorporated"-> "0",
        "isRepMemberOfGroup"-> "1",
        "systemDate"-> "2026-05-31",
        "premises" -> Json.arr(
          Json.obj(
            "mgdRegNumber" -> "XCM00000001236",
            "address1" -> " 1.Dalford court ",
            "address2" -> " TeLford ",
            "address3" -> " Shropshire ",
            "address4" -> " ",
            "postcode" -> "TF3 2BP",
            "systemDate" -> "2026-06-30"
          ),
          Json.obj(
            "mgdRegNumber" -> "XCM00000001236",
            "address1" -> " 2.Dalford court ",
            "address2" -> " TeLford ",
            "address3" -> " Shropshire ",
            "address4" -> " ",
            "postcode" -> "TF3 2BP",
            "systemDate" -> "2026-06-30"
          )
        )
      )

      val result = json.as[MemberDetails]

      result shouldBe MemberDetails(
        "XWM00000001020",
        businessPartnerNumber = Some("0100053091"), businessName = Some("BRUCE HOPKINS LIMITED"), tradingName = Some(" Trading name 1"), crn = Some(""),
        dateOfIncorporation = Some(LocalDate.of(2020,2,15)), countryOfIncorporation = Some("Spain"), foreignCorporateRef = Some(""), utr = Some(1L), vrn = Some(1L), tradeClass = Some(1L),
        businessActivityDesc = Some(""), phoneNumber = Some(""), mobilePhoneNumber = Some(""), faxNumber = Some(""), emailAddr = Some(""), adi = Some(""),
        address1 = Some("Address 1"), address2 = Some("Address 2"), address3 = Some("Address 3"), address4 = Some(""), postcode = Some(""), isIomOrCiFlag = Some("0"), haveGamblingLicenceNo = Some("1"),
        gamblingLicenceNo = Some("123-456789-A-123456-789"), heldByLandlord = Some("1"), localAuthority = Some("1"), familyEntertainment = Some("0"), clubGaming = Some("0"), clubLicence = Some("1"),
        prizeGaming = Some("0"), onPremises = Some("1"), clubPremises = Some("0"), regCert = Some("0"), bookmaking = Some("0"), bingo = Some("0"), amusement = Some("0"),
        serveAlcohol = Some("0"), premisesNotCovered = Some("0"), previousMgdrn1 = Some("XAM00000000327"), previousMgdrn2 = Some("XNM00000000457"), previousMgdrn3 = Some("XHM00000000177"), associatedMgdrn1 = Some("XYM00000000287"),
        associatedMgdrn2 = Some(""), associatedMgdrn3 = Some(""), dateOfJoining = Some(LocalDate.of(2013,2,1)), dateOfLeaving = Some(LocalDate.of(2023,3,1)), isRepMemSameAsCb = Some("0"),
        isUkIncorporated = Some("0"), isRepMemberOfGroup = Some("1"), systemDate = Some(LocalDate.of(2026,5,31)),
        premises = Seq(PremisesDetails("XCM00000001236", Some(" 1.Dalford court "),
          Some(" TeLford "), Some(" Shropshire "),
          Some(" "),
          Some("TF3 2BP"), Some(LocalDate.of(2026,6,30))),PremisesDetails("XCM00000001236", Some(" 2.Dalford court "),
          Some(" TeLford "), Some(" Shropshire "),
          Some(" "),
          Some("TF3 2BP"), Some(LocalDate.of(2026,6,30)))
        ))
    }
    
  }
}
