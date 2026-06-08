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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.gamblingexternalstub.models.*

trait GamblingReallocationsController extends itemDates {

  def getReallocations(
    regNumber: String,
    pageNo: Int,
    pageSize: Int,
    recordCount: Int,
    customisation: Int
  ): JsValue = {
    customisation match {
      case 0 | 1 | 2 | 3 => getReallocationsDetails(recordCount, customisation)
      case 4             => getReallocationsIn(pageNo, pageSize, recordCount)
      case 5             => getReallocationsOut(pageNo, pageSize, recordCount)
    }
  }

  private def getReallocationsDetails(recordCount: Int, customisation: Int): JsValue = {

    val reallocationsInRecordCount = customisation match {
      case 2 | 3 => 0
      case _     => recordCount
    }

    val reallocationsOutRecordCount = customisation match {
      case 1 | 3 => 0
      case _     => recordCount
    }
    val reallocationsIn = createReallocations(1, 10, 1, 0, reallocationsInRecordCount)
    val reallocationsOut = createReallocations(1, 10, -1, 33.33, reallocationsOutRecordCount)

    Json.toJson(
      ReallocationsDetails(
        periodStartDate        = reallocationsIn.periodStartDate,
        periodEndDate          = reallocationsIn.periodEndDate,
        reallocationsInAmount  = reallocationsIn.total.getOrElse(0),
        reallocationsOutAmount = reallocationsOut.total.getOrElse(0),
        total                  = (reallocationsIn.total.getOrElse(BigDecimal(0)) + reallocationsOut.total.getOrElse(BigDecimal(0))).abs * -1
      )
    )
  }

  private def getReallocationsIn(pageNo: Int, pageSize: Int, recordCount: Int): JsValue = {
    Json.toJson(createReallocations(pageNo, pageSize, 1, 0, recordCount))
  }

  private def getReallocationsOut(pageNo: Int, pageSize: Int, recordCount: Int): JsValue = {
    Json.toJson(createReallocations(pageNo, pageSize, -1, 33.33, recordCount))
  }

  private def createReallocations(pageNo: Int, pageSize: Int, amountSign: Int, offset: BigDecimal, recordCount: Int): Reallocations = {
    val allRecords = (1 to recordCount).map { i =>
      val monthOffset = (i - 1) % windowMonths
      val dateProcessed = periodStart.plusMonths(monthOffset)
      val amount = (BigDecimal(i * 100) + offset) * amountSign

      ReallocationItem(
        dateProcessed = Some(dateProcessed),
        amount        = Some(amount)
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allRecords.slice(from, from + pageSize)

    Reallocations(
      periodStartDate = Some(periodStart),
      periodEndDate   = Some(periodEnd),
      total           = Some(allRecords.flatMap(_.amount).sum),
      totalRecords    = Some(recordCount),
      items           = page
    )
  }
}
