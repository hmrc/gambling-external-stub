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

import play.api.libs.json.*

import java.time.LocalDate

case class Partner(
  mgdRegNumber: String,
  businessPartnerNumber: Option[String] = None,
  dateOfJoining: Option[LocalDate] = None,
  dateOfLeaving: Option[LocalDate] = None,
  solePropTitle: Option[String] = None,
  solePropFirstName: Option[String] = None,
  solePropMiddleName: Option[String] = None,
  solePropLastName: Option[String] = None,
  businessName: Option[String] = None,
  tradingName: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  nino: Option[String] = None,
  utr: Option[String] = None,
  vrn: Option[String] = None,
  crn: Option[String] = None,
  dateOfIncorporation: Option[LocalDate] = None,
  countryOfIncorporation: Option[String] = None,
  foreignCorporateRef: Option[String] = None,
  address1: Option[String] = None,
  address2: Option[String] = None,
  address3: Option[String] = None,
  address4: Option[String] = None,
  postcode: Option[String] = None,
  country: Option[String] = None,
  adi: Option[String] = None,
  iomOrCiFlag: Option[String] = None,
  phoneNumber: Option[String] = None,
  mobilePhoneNumber: Option[String] = None,
  faxNumber: Option[String] = None,
  emailAddr: Option[String] = None,
  isFutureLeaveDate: Option[Int] = None,
  isFutureJoinDate: Option[Int] = None,
  businessType: Option[Int] = None
)

case class PartnerDetails(partners: List[Partner], systemDate: Option[LocalDate])

object PartnerFormats {
  implicit val partnerDetailsFormat: OFormat[Partner] = Json.format[Partner]
  implicit val partnerDetailsResponseFormat: OFormat[PartnerDetails] = Json.format[PartnerDetails]

  def fullModel(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber           = mgdRegNumber,
        businessPartnerNumber  = Some("0100049899"),
        dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
        dateOfLeaving          = Some(LocalDate.of(2025, 1, 1)),
        solePropTitle          = Some("Mx"),
        solePropFirstName      = Some("solePropFirstName"),
        solePropMiddleName     = Some("solePropMiddleName"),
        solePropLastName       = Some("solePropLastName"),
        businessName           = Some("Partner1"),
        tradingName            = Some("tradingName"),
        dateOfBirth            = Some(LocalDate.of(1999, 9, 9)),
        nino                   = Some("ni123456789no"),
        utr                    = Some("123456789"),
        vrn                    = Some("123456789"),
        crn                    = Some("123456789"),
        dateOfIncorporation    = Some(LocalDate.of(2024, 1, 1)),
        countryOfIncorporation = Some("countryOfIncorporation"),
        foreignCorporateRef    = Some("foreignCorporateRef"),
        address1               = Some("address1"),
        address2               = Some("address2"),
        address3               = Some("address3"),
        address4               = Some("address4"),
        postcode               = Some("postcode"),
        country                = Some("country"),
        adi                    = Some("adi"),
        iomOrCiFlag            = Some("false"),
        phoneNumber            = Some("phoneNumber"),
        mobilePhoneNumber      = Some("mobilePhoneNumber"),
        faxNumber              = Some("faxNumber"),
        emailAddr              = Some("emailAddr"),
        isFutureLeaveDate      = Some(0),
        isFutureJoinDate       = Some(0),
        businessType           = Some(2)
      )
    ),
    systemDate = baseDate
  )

  def partialModel(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber           = mgdRegNumber,
        businessPartnerNumber  = Some("0100049899"),
        dateOfJoining          = Some(LocalDate.of(2024, 1, 1)),
        dateOfLeaving          = Some(LocalDate.of(2025, 1, 1)),
        solePropTitle          = Some("Mx"),
        solePropFirstName      = Some("solePropFirstName"),
        solePropLastName       = Some("solePropLastName"),
        businessName           = Some("Partner1"),
        tradingName            = Some("tradingName"),
        dateOfBirth            = Some(LocalDate.of(1999, 9, 9)),
        nino                   = Some("ni123456789no"),
        crn                    = Some("123456789"),
        dateOfIncorporation    = Some(LocalDate.of(2024, 1, 1)),
        countryOfIncorporation = Some("countryOfIncorporation"),
        foreignCorporateRef    = Some("foreignCorporateRef"),
        adi                    = Some("adi"),
        iomOrCiFlag            = Some("false"),
        phoneNumber            = Some("phoneNumber"),
        mobilePhoneNumber      = Some("mobilePhoneNumber"),
        faxNumber              = Some("faxNumber"),
        emailAddr              = Some("emailAddr"),
        isFutureLeaveDate      = Some(0),
        isFutureJoinDate       = Some(0),
        businessType           = Some(2)
      )
    ),
    systemDate = baseDate
  )

  def noDataModel(mgdRegNumber: String): PartnerDetails =
    PartnerDetails(partners = List(Partner(mgdRegNumber)), systemDate = baseDate)

  def `XMM00000001177`(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber        = "XMM00000001177",
        dateOfJoining       = Some(LocalDate.parse("2013-02-01")),
        dateOfLeaving       = Some(LocalDate.parse("1999-12-31")),
        businessName        = Some("Corporate Body Name"),
        tradingName         = Some("Trading Name"),
        utr                 = Some("4444444444"),
        crn                 = Some("01234556"),
        dateOfIncorporation = Some(LocalDate.parse("2012-10-29")),
        address1            = Some("1 address"),
        address2            = Some("2 address"),
        postcode            = Some("AD34 4FD"),
        adi                 = Some("add"),
        iomOrCiFlag         = Some("false"),
        isFutureLeaveDate   = Some(0),
        isFutureJoinDate    = Some(0),
        businessType        = Some(2)
      ),
      Partner(
        mgdRegNumber      = "XMM00000001177",
        dateOfJoining     = Some(LocalDate.parse("2013-02-01")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        businessName      = Some("Unicorporated Body Name"),
        tradingName       = Some("Trading Name"),
        utr               = Some("4444444444"),
        address1          = Some("1 Address Line 1"),
        address2          = Some("2 Address Line 2"),
        postcode          = Some("AD23 9JJ"),
        iomOrCiFlag       = Some("false"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(3)
      )
    ),
    systemDate = Some(LocalDate.parse("2026-07-31"))
  )

  def `XPM00000000985`(mgdRegNumber: String): PartnerDetails = PartnerDetails(
    partners = List(
      Partner(
        mgdRegNumber       = "XPM00000000985",
        dateOfJoining      = Some(LocalDate.parse("2013-02-01")),
        dateOfLeaving      = Some(LocalDate.parse("1999-12-31")),
        solePropTitle      = Some("Mr"),
        solePropFirstName  = Some("STEPHEN"),
        solePropMiddleName = Some("ADAM"),
        solePropLastName   = Some("CRYPTON"),
        dateOfBirth        = Some(LocalDate.parse("1960-01-06")),
        nino               = Some("BT100028B"),
        address1           = Some("10 LONG ROAD"),
        address2           = Some("BRIGHTON"),
        postcode           = Some("BN12 3KJ"),
        iomOrCiFlag        = Some("false"),
        phoneNumber        = Some("0044444 444 444"),
        mobilePhoneNumber  = Some("0044444 444 445"),
        faxNumber          = Some("0044444 444 446"),
        isFutureLeaveDate  = Some(0),
        isFutureJoinDate   = Some(0),
        businessType       = Some(1)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        businessName      = Some("BDP PARTNERS"),
        tradingName       = Some("BDP PARTNERS"),
        utr               = Some("5177008741"),
        address1          = Some("10 RING ROAD"),
        address2          = Some("LUTON"),
        postcode          = Some("LN12 4RT"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("0044 777 777 777 004"),
        mobilePhoneNumber = Some("0044 777 777 777 005"),
        faxNumber         = Some("0044 777 777 777 006"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(4)
      ),
      Partner(
        mgdRegNumber        = "XPM00000000985",
        dateOfJoining       = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving       = Some(LocalDate.parse("1999-12-31")),
        businessName        = Some("BROADVIEW BUILDERS LIMITED"),
        tradingName         = Some("BROADVIEW BUILDERS LIMITED"),
        dateOfIncorporation = Some(LocalDate.parse("1991-12-19")),
        address1            = Some("CENTURY PLACE LAMBERTS ROAD"),
        address2            = Some("TUNBRIDGE WELLS"),
        address3            = Some("KENT"),
        postcode            = Some("TN2 3EH"),
        adi                 = Some("AUKER HUTTON MLS BUSINESS CENTRE"),
        iomOrCiFlag         = Some("false"),
        phoneNumber         = Some("123"),
        mobilePhoneNumber   = Some("345"),
        faxNumber           = Some("11"),
        isFutureLeaveDate   = Some(0),
        isFutureJoinDate    = Some(0),
        businessType        = Some(5)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        businessName      = Some("JB PARTNERS"),
        tradingName       = Some("JB PARTNERS"),
        utr               = Some("2177013303"),
        address1          = Some("44 HIGH STREET"),
        address2          = Some("WORTHING"),
        postcode          = Some("BN12 4XJ"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("01452 252525"),
        mobilePhoneNumber = Some("01452 252526"),
        faxNumber         = Some("01452 252527"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(4)
      ),
      Partner(
        mgdRegNumber        = "XPM00000000985",
        dateOfJoining       = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving       = Some(LocalDate.parse("1999-12-31")),
        businessName        = Some("BELLEVUE PROPERTY INVESTMENTS LIMITED"),
        tradingName         = Some("BELLEVUE PROPERTY INVESTMENTS LIMITED"),
        dateOfIncorporation = Some(LocalDate.parse("1992-01-20")),
        address1            = Some("109 SILVERDALE AVE"),
        address2            = Some("WALTON-ON-THAMES"),
        address3            = Some("SURREY"),
        postcode            = Some("KT12 1EH"),
        iomOrCiFlag         = Some("false"),
        phoneNumber         = Some("0044 111 111 1111"),
        mobilePhoneNumber   = Some("0044 111 111 1112"),
        faxNumber           = Some("0044 111 111 1113"),
        isFutureLeaveDate   = Some(0),
        isFutureJoinDate    = Some(0),
        businessType        = Some(5)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        solePropTitle     = Some("Mr"),
        solePropFirstName = Some("T"),
        solePropLastName  = Some("PERRY"),
        dateOfBirth       = Some(LocalDate.parse("1960-06-12")),
        nino              = Some("GY002534B"),
        address1          = Some("12 LOW ROAD"),
        address2          = Some("SUTTON"),
        postcode          = Some("BN12 5RT"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("12"),
        mobilePhoneNumber = Some("13"),
        faxNumber         = Some("14"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(1)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        solePropTitle     = Some("Mrs"),
        solePropFirstName = Some("ELLEN"),
        solePropLastName  = Some("EGGPLANT"),
        dateOfBirth       = Some(LocalDate.parse("1960-06-13")),
        nino              = Some("ST006004A"),
        address1          = Some("22 GROVE ROAD"),
        address2          = Some("BRIGHTON"),
        postcode          = Some("BN44 7TL"),
        iomOrCiFlag       = Some("false"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(1)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        businessName      = Some("PARTNER 2"),
        tradingName       = Some("PARTNER 2"),
        utr               = Some("4177013315"),
        address1          = Some("12 RED ROAD"),
        address2          = Some("WORTHING"),
        postcode          = Some("WR23 4RY"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("1"),
        mobilePhoneNumber = Some("2"),
        faxNumber         = Some("3"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(4)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        businessName      = Some("PARTNER 03"),
        tradingName       = Some("PARTNER 03"),
        utr               = Some("2177013303"),
        address1          = Some("7 TOWERS STREET"),
        address2          = Some("LIVERPOOL"),
        postcode          = Some("LP32 2YY"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("33"),
        mobilePhoneNumber = Some("34"),
        faxNumber         = Some("12"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(4)
      ),
      Partner(
        mgdRegNumber      = "XPM00000000985",
        dateOfJoining     = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving     = Some(LocalDate.parse("1999-12-31")),
        solePropTitle     = Some("Mr"),
        solePropFirstName = Some("H"),
        solePropLastName  = Some("HOLLAND"),
        dateOfBirth       = Some(LocalDate.parse("1960-06-12")),
        nino              = Some("MT000080D"),
        address1          = Some("44 RED ROAD"),
        address2          = Some("BRIGHTON"),
        postcode          = Some("BN34 5RT"),
        iomOrCiFlag       = Some("false"),
        phoneNumber       = Some("0044 666 666 666"),
        mobilePhoneNumber = Some("0044 666 666 667"),
        faxNumber         = Some("0044 666 666 668"),
        isFutureLeaveDate = Some(0),
        isFutureJoinDate  = Some(0),
        businessType      = Some(1)
      ),
      Partner(
        mgdRegNumber        = "XPM00000000985",
        dateOfJoining       = Some(LocalDate.parse("2012-10-15")),
        dateOfLeaving       = Some(LocalDate.parse("1999-12-31")),
        businessName        = Some("B.G. MIDDLETON (DEVELOPMENTS) LIMITED"),
        tradingName         = Some("B.G. MIDDLETON (DEVELOPMENTS) LIMITED"),
        dateOfIncorporation = Some(LocalDate.parse("1991-05-31")),
        address1            = Some("RAFTERS"),
        address2            = Some("BROADHEMBURY"),
        address3            = Some("HONITON"),
        address4            = Some("DEVON EX14 3NQ"),
        postcode            = Some("EX14 3NQ"),
        iomOrCiFlag         = Some("false"),
        phoneNumber         = Some("0044 111 111 1111"),
        mobilePhoneNumber   = Some("0044 111 111 1122"),
        faxNumber           = Some("0044 111 111 1133"),
        isFutureLeaveDate   = Some(0),
        isFutureJoinDate    = Some(0),
        businessType        = Some(5)
      )
    ),
    systemDate = Some(LocalDate.parse("2026-08-06"))
  )

  def `XPM00000000600`: PartnerDetails = mockPartnerDetails

  private val baseDate = Some(LocalDate.now())

  private val hundredPartners: List[Partner] = (1 to 100).map { i =>
    val indexPad = f"$i%04d"
    val isCorporate = i % 2 == 0

    Partner(
      mgdRegNumber           = s"XPM00000000600",
      businessPartnerNumber  = Some(s"BPN00000$indexPad"),
      dateOfJoining          = baseDate.map(_.plusDays(i.toLong)),
      dateOfLeaving          = if (i % 5 == 0) baseDate.map(_.plusYears(1).plusDays(i.toLong)) else None,
      solePropTitle          = if (!isCorporate) Some(if (i % 2 == 0) "Mr" else "Ms") else None,
      solePropFirstName      = if (!isCorporate) Some(s"PartnerFirst$i") else None,
      solePropMiddleName     = if (!isCorporate && i % 3 == 0) Some("Middle") else None,
      solePropLastName       = if (!isCorporate) Some(s"PartnerLast$i") else None,
      businessName           = if (isCorporate) Some(s"Partner Company $i Ltd") else None,
      tradingName            = if (i % 3 == 0) Some(s"Trading Name $i") else None,
      dateOfBirth            = if (!isCorporate) Some(LocalDate.of(1985, 1, 1).plusDays(i.toLong)) else None,
      nino                   = if (!isCorporate) Some(f"AA$i%06d A") else None,
      utr                    = Some("1121766916"),
      vrn                    = if (i % 2 == 0) Some(f"12345$i%04d") else None,
      crn                    = if (isCorporate) Some(f"0123$i%04d") else None,
      dateOfIncorporation    = if (isCorporate) Some(LocalDate.of(2015, 5, 12)) else None,
      countryOfIncorporation = if (isCorporate) Some("GB") else None,
      foreignCorporateRef    = None,
      address1               = Some(s"$i High Street"),
      address2               = Some("Suite 100"),
      address3               = Some("Town Centre"),
      address4               = Some("County"),
      postcode               = Some("AA1 1AA"),
      country                = Some("GB"),
      adi                    = if (i % 4 == 0) Some(s"Additional Info $i") else None,
      iomOrCiFlag            = Some("false"),
      phoneNumber            = Some("01234567890"),
      mobilePhoneNumber      = Some("07123456789"),
      faxNumber              = if (i % 10 == 0) Some("01234567899") else None,
      emailAddr              = Some(s"partner$i@example.com"),
      isFutureLeaveDate      = Some(0),
      isFutureJoinDate       = Some(0),
      businessType           = Some(if (isCorporate) 2 else 1) // 1 = Soleproprietor, 2 = Corporatebody
    )
  }.toList

  private val mockPartnerDetails: PartnerDetails = PartnerDetails(
    partners   = hundredPartners,
    systemDate = Some(LocalDate.of(2026, 9, 7))
  )
}
