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

trait OtherAssessmentsT extends itemDates {

  def getOtherAssessments(
    regNumber: String,
    pageNo: Int,
    pageSize: Int,
    recordCount: Int
  ): JsValue = {

    val allRecords = (1 to recordCount).map { i =>
      val monthOffset = (i - 1) % windowMonths
      val dateRaised = periodStart.plusMonths(monthOffset)
      AssessmentItem(
        dateRaised      = Some(dateRaised),
        periodStartDate = Some(periodStartItem),
        periodEndDate   = Some(periodEndItem),
        amount          = Some(BigDecimal(i * 100) * -1)
      )
    }

    val from = (pageNo - 1) * pageSize
    val page = allRecords.slice(from, from + pageSize)

    Json.toJson(
      Assessments(
        periodStartDate = Some(periodStart),
        periodEndDate   = Some(periodEnd),
        total           = Some(allRecords.flatMap(_.amount).sum),
        totalRecords    = Some(recordCount),
        items           = page
      )
    )
  }
}
