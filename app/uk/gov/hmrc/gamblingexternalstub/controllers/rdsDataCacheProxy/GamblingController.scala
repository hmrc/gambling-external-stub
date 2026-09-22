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

import play.api.Logging
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject
import scala.util.Using

class GamblingController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  private lazy val businessNames =
    Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "business-name").map { registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/business-name/$registration.json")
        )(Json.parse)
        .as[JsObject]
    }.toMap

  private lazy val tradeClassDetails =
    Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "trade-class").map { registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/trade-class/$registration.json")
        )(Json.parse)
        .as[JsObject]
    }.toMap

  private lazy val mgdDetails = Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "mgd-details").map {
    registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/mgd-details/$registration.json")
        )(Json.parse)
        .as[JsObject]
  }.toMap

  private lazy val returnSummaries =
    Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "return-summary").map { registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/return-summary/$registration.json")
        )(Json.parse)
        .as[JsObject]
    }.toMap

  private lazy val mgdCertificates =
    Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "mgd-certificate").map { registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/mgd-certificate/$registration.json")
        )(Json.parse)
        .as[JsObject]
    }.toMap

  private lazy val businessDetails =
    Seq("XGM00000001761", "XGM00000001762", "XGM00000001763", "XGM00000001764", "XGM00000001765", "business-details").map { registration =>
      registration -> Using
        .resource(
          getClass.getResourceAsStream(s"/data/business-details/$registration.json")
        )(Json.parse)
        .as[JsObject]
    }.toMap

  def getReturnSummary(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      // Scenario 1 → overdue exists
      case "XGM00000001761" =>
        Ok(returnSummaries("XGM00000001761"))

      // Scenario 2 → returns due
      case "XGM00000001762" =>
        Ok(returnSummaries("XGM00000001762"))

      // Scenario 3 → both returns due and overdue exists
      case "XGM00000001763" =>
        Ok(returnSummaries("XGM00000001763"))

      // Scenario 1 → overdue exists
      case "XGM00000001764" =>
        Ok(returnSummaries("XGM00000001764"))

      // Scenario 2 → returns due
      case "XGM00000001765" =>
        Ok(returnSummaries("XGM00000001765"))

      // default fallback
      case reg =>
        Ok(returnSummaries("return-summary") ++ Json.obj("mgdRegNumber" -> reg))
    }
  }

  def getBusinessName(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      // Scenario 1
      case "XGM00000001761" =>
        Ok(businessNames("XGM00000001761"))

      // Scenario 2
      case "XGM00000001762" =>
        Ok(businessNames("XGM00000001762"))

      // Scenario 3 →
      case "XGM00000001763" =>
        Ok(businessNames("XGM00000001763"))

      case "XGM00000001764" =>
        Ok(businessNames("XGM00000001764"))

      case "XGM00000001765" =>
        Ok(businessNames("XGM00000001765"))

      // =============== DEFAULT ===============
      case reg =>
        Ok(businessNames("business-name") ++ Json.obj("mgdRegNumber" -> mgdRegNumber))
    }
  }

  def getBusinessDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case "XGM00000001761" =>
        Ok(businessDetails("XGM00000001761"))

      case "XGM00000001762" =>
        Ok(businessDetails("XGM00000001762"))

      case "XGM00000001763" =>
        Ok(businessDetails("XGM00000001763") ++ Json.obj("systemDate" -> LocalDate.now()))

      case "XGM00000001764" =>
        Ok(businessDetails("XGM00000001764") ++ Json.obj("systemDate" -> LocalDate.now()))

      case "XGM00000001765" =>
        Ok(businessDetails("XGM00000001765") ++ Json.obj("systemDate" -> LocalDate.now()))

      // =============== DEFAULT ===============
      case reg =>
        Ok(businessDetails("business-details") ++ Json.obj("mgdRegNumber" -> reg, "systemDate" -> LocalDate.now()))
    }
  }

  def getMgdCertificate(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        logger.warn("[Gambling Stub] Invalid MGD reg number (certificate)")

        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        logger.error("[Gambling Stub] Unexpected error (certificate)")
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case "XGM00000001761" =>
        Ok(mgdCertificates("XGM00000001761"))

      case "XGM00000001762" =>
        Ok(mgdCertificates("XGM00000001762"))

      case "XGM00000001763" =>
        Ok(mgdCertificates("XGM00000001763"))

      case "XGM00000001764" =>
        Ok(mgdCertificates("XGM00000001764"))

      case "XGM00000001765" =>
        Ok(mgdCertificates("XGM00000001765"))

      // ===== DEFAULT =====
      case reg =>
        val certificate = mgdCertificates("mgd-certificate")
        Ok(
          certificate ++ Json.obj(
            "mgdRegNumber" -> reg,
            "businessName" -> ((certificate \ "businessName").as[String] + reg)
          )
        )
    }
  }

  def getOperatorDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" => invalidResponse

      case "error" => errorResponse

      case "XGM00000001761" =>
        Ok(
          Json.toJson(
            baseOperator("XGM00000001761").copy(
              tradingName  = Some("Acme Bets"),
              businessName = Some("Acme Gaming Ltd"),
              adi          = Some("ADI123"),
              address1     = Some("1 High Street"),
              address2     = Some("Newcastle"),
              postcode     = Some("NE1 1AA"),
              agentOwnRef  = Some("AGENT001")
            )
          )
        )

      case "XGM00000001762" =>
        Ok(
          Json.toJson(
            baseOperator("XGM00000001762").copy(
              solePropName      = Some("Jane Doe"),
              solePropTitle     = Some("Ms"),
              solePropFirstName = Some("Jane"),
              solePropLastName  = Some("Doe"),
              tradingName       = None,
              businessName      = Some("Jane's Bets"),
              businessType      = Some(SoleProprietor),
              address1          = Some("10 Market Road"),
              address2          = Some("Gateshead"),
              postcode          = Some("NE8 1ZZ")
            )
          )
        )
      case "XGM00000001763" =>
        Ok(
          Json.toJson(
            baseOperator("XGM00000001763").copy(
              tradingName  = Some("Global Bets"),
              businessName = Some("Global Gaming Inc"),
              address1     = Some("123 International Way"),
              address2     = Some("Dublin"),
              postcode     = Some("D01 ABC"),
              country      = Some("Ireland"),
              abroadSig    = Some("Y"),
              adi          = Some("ADI999"),
              agentOwnRef  = Some("AGENT999")
            )
          )
        )

      case "XGM00000001764" =>
        Ok(
          Json.toJson(
            baseOperator("XGM00000001764").copy(
              businessName = Some("ABC Partnership"),
              tradingName  = Some("Partnership Bets"),
              businessType = Some(Partnership),
              address1     = Some("50 King Street"),
              address2     = Some("Leeds"),
              postcode     = Some("LS1 1AA")
            )
          )
        )

      case reg =>
        Ok(Json.toJson(baseOperator(reg)))
    }
  }

  def getTradeClassDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case "XGM00000001761" =>
        Ok(tradeClassDetails("XGM00000001761"))

      case "XGM00000001762" =>
        Ok(tradeClassDetails("XGM00000001762"))

      case "XGM00000001763" =>
        Ok(tradeClassDetails("XGM00000001763"))

      case "XGM00000001764" =>
        Ok(tradeClassDetails("XGM00000001764"))

      case "XGM00000001765" =>
        Ok(tradeClassDetails("XGM00000001765"))

      // Default
      case reg =>
        Ok(tradeClassDetails("trade-class"))
    }
  }

  def getMgdDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" =>
        BadRequest(
          Json.obj(
            "code"    -> "INVALID_MGD_REG_NUMBER",
            "message" -> "mgdRegNumber must be provided"
          )
        )

      case "error" =>
        InternalServerError(
          Json.obj(
            "code"    -> "UNEXPECTED_ERROR",
            "message" -> "Unexpected error occurred"
          )
        )

      case "XGM00000001761" =>
        Ok(mgdDetails("XGM00000001761"))

      case "XGM00000001762" =>
        Ok(mgdDetails("XGM00000001762"))

      case "XGM00000001763" =>
        Ok(mgdDetails("XGM00000001763"))

      case "XGM00000001764" =>
        Ok(mgdDetails("XGM00000001764"))

      case "XGM00000001765" =>
        Ok(mgdDetails("XGM00000001765"))

      // known good data only
      case _ =>
        Ok(mgdDetails("mgd-details") ++ Json.obj("mgdRegNumber" -> mgdRegNumber))

    }
  }

  def getCorrespondenceDetails(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "invalid" => invalidResponse

      case "error" => errorResponse

      case "XGM00000001761" =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = "XGM00000001761",
              nameLine1         = Some("UK XGM00000001761"),
              nameLine2         = Some("9C - 1st Floor"),
              address1          = Some("5 Quick Silver Way"),
              address2          = Some("Cobalt Business Park"),
              address3          = Some("Newcastle upon Tyne"),
              address4          = Some("Newcastle"),
              country           = None,
              postcode          = Some("NE27 0QQ"),
              phoneNumber       = Some("01000001761"),
              mobilePhoneNumber = Some("01000001761"),
              faxNumber         = Some("01000001761"),
              emailAddr         = Some("email1@example.com"),
              adi               = Some("Building 9C"),
              iomOrCiFlag       = Some("false"),
              Some(fixedDate)
            )
          )
        )

      case "XGM00000001762" =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = "XGM00000001762",
              nameLine1         = None,
              nameLine2         = None,
              address1          = None,
              address2          = None,
              address3          = None,
              address4          = None,
              country           = None,
              postcode          = None,
              phoneNumber       = None,
              mobilePhoneNumber = None,
              faxNumber         = None,
              emailAddr         = None,
              adi               = None,
              iomOrCiFlag       = Some("false"),
              Some(fixedDate)
            )
          )
        )

      case "XGM00000001763" =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = "XGM00000001763",
              nameLine1         = Some("UK Address with mandatory fields - XGM00000001763"),
              nameLine2         = None,
              address1          = Some("5 Quick Silver Way"),
              address2          = Some("Cobalt Business Park"),
              address3          = Some("Newcastle upon Tyne"),
              address4          = Some("Newcastle"),
              country           = None,
              postcode          = Some("NE27 0QQ"),
              phoneNumber       = Some("01000001761"),
              mobilePhoneNumber = None,
              faxNumber         = None,
              emailAddr         = None,
              adi               = None,
              iomOrCiFlag       = None,
              Some(fixedDate)
            )
          )
        )

      case "XGM00000001764" =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = "XGM00000001764",
              nameLine1         = Some("Non UK address - XGM00000001764"),
              nameLine2         = Some("9C - 1st Floor"),
              address1          = Some("5 Quick Silver Way"),
              address2          = Some("Cobalt Business Park"),
              address3          = Some("Amsderdam"),
              address4          = Some("1127 AA"),
              country           = Some("Netherlands"),
              postcode          = None,
              phoneNumber       = Some("01000001764"),
              mobilePhoneNumber = Some("01000001764"),
              faxNumber         = Some("01000001764"),
              emailAddr         = Some("email-nonuk-address@example.com"),
              adi               = Some("Non UK Address"),
              iomOrCiFlag       = Some("true"),
              Some(fixedDate)
            )
          )
        )

      case "XGM00000001765" =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = "XGM00000001765",
              nameLine1         = None,
              nameLine2         = None,
              address1          = None,
              address2          = None,
              address3          = None,
              address4          = None,
              country           = None,
              postcode          = None,
              phoneNumber       = None,
              mobilePhoneNumber = None,
              faxNumber         = None,
              emailAddr         = None,
              adi               = None,
              iomOrCiFlag       = None,
              Some(fixedDate)
            )
          )
        )

      case reg =>
        Ok(
          Json.toJson(
            CorrespondenceDetails(
              mgdRegNumber      = reg,
              nameLine1         = Some("Default correspondence name"),
              nameLine2         = Some("Default additional correspondence name"),
              address1          = Some("Default correspondence address line 1"),
              address2          = Some("Default correspondence address line 2"),
              address3          = Some("Default correspondence address town or city"),
              address4          = Some("Default correspondence address county"),
              country           = None,
              postcode          = Some("AA11 1AA"),
              phoneNumber       = Some("01000000001"),
              mobilePhoneNumber = Some("01000000002"),
              faxNumber         = Some("01000000003"),
              emailAddr         = Some("dafault-email@example.com"),
              adi               = Some("Default Correspondence address additional information"),
              iomOrCiFlag       = Some("false"),
              Some(fixedDate)
            )
          )
        )
    }
  }

  private val fixedDate = LocalDate.parse("2026-01-01")

  private val SoleProprietor = 1
  private val CorporateBody = 2
  private val Partnership = 4

  private def baseOperator(reg: String) =
    OperatorDetails(
      mgdRegNumber       = reg,
      solePropName       = None,
      solePropTitle      = None,
      solePropFirstName  = None,
      solePropMiddleName = None,
      solePropLastName   = None,
      tradingName        = None,
      businessName       = Some(s"Business for $reg"),
      businessType       = Some(CorporateBody),
      adi                = None,
      address1           = Some("Unknown Address Line 1"),
      address2           = Some("Unknown Address Line 2"),
      address3           = None,
      address4           = None,
      postcode           = Some("AA1 1AA"),
      country            = Some("United Kingdom"),
      abroadSig          = Some("N"),
      agentOwnRef        = None,
      systemDate         = Some(fixedDate)
    )

  private val invalidResponse =
    BadRequest(
      Json.obj(
        "code"    -> "INVALID_MGD_REG_NUMBER",
        "message" -> "mgdRegNumber must be provided"
      )
    )

  private val errorResponse =
    InternalServerError(
      Json.obj(
        "code"    -> "UNEXPECTED_ERROR",
        "message" -> "Unexpected error occurred"
      )
    )

}
