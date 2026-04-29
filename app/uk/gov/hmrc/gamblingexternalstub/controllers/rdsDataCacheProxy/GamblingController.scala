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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.Inject

class GamblingController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

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
      case "XGM00000001761" | "GAM0000000001" =>
        Ok(Json.toJson(ReturnSummary(mgdRegNumber, returnsDue = 0, returnsOverdue = 1)))

      // Scenario 2 → nothing due
      case "XGM00000001762" | "GAM0000000002" =>
        Ok(Json.toJson(ReturnSummary(mgdRegNumber, returnsDue = 0, returnsOverdue = 0)))

      // default fallback
      case reg =>
        Ok(Json.toJson(ReturnSummary(reg, returnsDue = 0, returnsOverdue = 0)))
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

      // Scenario 1 → Registered
      case "XGM00000001761" | "GAM0000000001" =>
        Ok(Json.toJson(BusinessDetails(mgdRegNumber, businessType = 1, currentlyRegistered = 1, groupReg = "foo", dateOfRegistration = Some(LocalDate.of(1991, 1,1 )), businessPartnerNumber = "bar", systemDate = Some(LocalDate.of(1991, 1,1 ))
        )))

      // Scenario 2 → Not Registered
      case "XGM00000001762" | "GAM0000000002" =>
        Ok(Json.toJson(BusinessDetails(mgdRegNumber, businessType = 1, currentlyRegistered = 0, groupReg = "foo", dateOfRegistration = Some(LocalDate.of(1991, 1,1 )), businessPartnerNumber = "bar", systemDate = Some(LocalDate.of(1991, 1,1 ))
        )))
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

      // ===== SCENARIO 1 =====
      case "XGM00000001761" =>
        Ok(
          Json.toJson(
            MgdCertificate(
              mgdRegNumber       = "XGM00000001761",
              registrationDate   = Some(LocalDate.parse("2023-01-15")),
              individualName     = Some("Mr John A Smith"),
              businessName       = Some("Acme Gaming Ltd"),
              tradingName        = Some("Acme Bets"),
              repMemName         = Some("Acme Rep Member Ltd"),
              busAddrLine1       = Some("1 High Street"),
              busAddrLine2       = Some("Newcastle"),
              busAddrLine3       = None,
              busAddrLine4       = None,
              busPostcode        = Some("NE1 1AA"),
              busCountry         = Some("United Kingdom"),
              busAdi             = Some("Some ADI Value"),
              repMemLine1        = Some("2 Low Street"),
              repMemLine2        = Some("Newcastle"),
              repMemLine3        = None,
              repMemLine4        = None,
              repMemPostcode     = Some("NE1 2BB"),
              repMemAdi          = Some("Rep ADI Value"),
              typeOfBusiness     = Some("Corporate Body"),
              businessTradeClass = Some(2),
              noOfPartners       = Some(2),
              groupReg           = "Y",
              noOfGroupMems      = Some(1),
              dateCertIssued     = Some(LocalDate.parse("2024-02-01")),
              partMembers = Seq(
                PartnerMember(
                  namesOfPartMems    = "Partner Member One Ltd",
                  solePropTitle      = None,
                  solePropFirstName  = None,
                  solePropMiddleName = None,
                  solePropLastName   = None,
                  typeOfBusiness     = 2
                ),
                PartnerMember(
                  namesOfPartMems    = "Sole Prop Example",
                  solePropTitle      = Some("Ms"),
                  solePropFirstName  = Some("Jane"),
                  solePropMiddleName = None,
                  solePropLastName   = Some("Doe"),
                  typeOfBusiness     = 1
                )
              ),
              groupMembers = Seq(
                GroupMember("Group Member One Ltd")
              ),
              returnPeriodEndDates = Seq(
                ReturnPeriodEndDate(LocalDate.parse("2026-03-31")),
                ReturnPeriodEndDate(LocalDate.parse("2026-06-30")),
                ReturnPeriodEndDate(LocalDate.parse("2026-09-30")),
                ReturnPeriodEndDate(LocalDate.parse("2026-12-31")),
                ReturnPeriodEndDate(LocalDate.parse("2027-03-31"))
              )
            )
          )
        )

      // ===== SCENARIO 2 =====
      case "XGM00000001762" =>
        Ok(
          Json.toJson(
            MgdCertificate(
              mgdRegNumber       = "XGM00000001762",
              registrationDate   = Some(LocalDate.parse("2022-10-05")),
              individualName     = None,
              businessName       = Some("Example Sole Trader"),
              tradingName        = None,
              repMemName         = None,
              busAddrLine1       = Some("10 Market Road"),
              busAddrLine2       = Some("Gateshead"),
              busAddrLine3       = None,
              busAddrLine4       = None,
              busPostcode        = Some("NE8 1ZZ"),
              busCountry         = Some("United Kingdom"),
              busAdi             = None,
              repMemLine1        = None,
              repMemLine2        = None,
              repMemLine3        = None,
              repMemLine4        = None,
              repMemPostcode     = None,
              repMemAdi          = None,
              typeOfBusiness     = Some("Sole proprietor"),
              businessTradeClass = Some(1),
              noOfPartners       = Some(0),
              groupReg           = "N",
              noOfGroupMems      = Some(0),
              dateCertIssued     = Some(LocalDate.parse("2024-01-10")),
              partMembers        = Seq.empty,
              groupMembers       = Seq.empty,
              returnPeriodEndDates = Seq(
                ReturnPeriodEndDate(LocalDate.parse("2026-03-31")),
                ReturnPeriodEndDate(LocalDate.parse("2026-06-30"))
              )
            )
          )
        )

      // ===== DEFAULT =====
      case reg =>
        Ok(
          Json.toJson(
            MgdCertificate(
              mgdRegNumber       = reg,
              registrationDate   = Some(LocalDate.parse("2021-01-01")),
              individualName     = None,
              businessName       = Some(s"Business for $reg"),
              tradingName        = None,
              repMemName         = None,
              busAddrLine1       = Some("Unknown Address Line 1"),
              busAddrLine2       = Some("Unknown Address Line 2"),
              busAddrLine3       = None,
              busAddrLine4       = None,
              busPostcode        = Some("AA1 1AA"),
              busCountry         = Some("United Kingdom"),
              busAdi             = None,
              repMemLine1        = None,
              repMemLine2        = None,
              repMemLine3        = None,
              repMemLine4        = None,
              repMemPostcode     = None,
              repMemAdi          = None,
              typeOfBusiness     = Some("Corporate Body"),
              businessTradeClass = Some(2),
              noOfPartners       = Some(0),
              groupReg           = "N",
              noOfGroupMems      = Some(0),
              dateCertIssued     = Some(LocalDate.parse("2024-01-01")),
              partMembers        = Seq.empty,
              groupMembers       = Seq.empty,
              returnPeriodEndDates = Seq(
                ReturnPeriodEndDate(LocalDate.parse("2026-03-31"))
              )
            )
          )
        )
    }
  }
}
