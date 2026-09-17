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

package uk.gov.hmrc.gamblingexternalstub.services

import uk.gov.hmrc.gamblingexternalstub.models.OpenPeriod

import javax.inject.{Inject, Singleton}
import scala.collection.concurrent.TrieMap

@Singleton
class OpenPeriodCacheService @Inject() () {

  private val cache = TrieMap.empty[(String, Int), OpenPeriod]

  def getForRegNumber(regNumber: String): Seq[OpenPeriod] =
    cache.collect { case ((rn, _), period) if rn == regNumber => period }.toSeq

  def putAll(regNumber: String, periods: Seq[OpenPeriod]): Unit =
    periods.foreach(p => cache.update((regNumber, p.consecNo), p))

  def updateStatus(regNumber: String, consecNo: Int, status: Int): Boolean =
    cache.get((regNumber, consecNo)) match {
      case Some(period) =>
        cache.update((regNumber, consecNo), period.copy(status = status))
        true
      case None => false
    }
}
