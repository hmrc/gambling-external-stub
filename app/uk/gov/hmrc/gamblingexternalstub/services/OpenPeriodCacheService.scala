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

import com.google.common.cache.{Cache, CacheBuilder}
import uk.gov.hmrc.gamblingexternalstub.models.OpenPeriod

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

@Singleton
class OpenPeriodCacheService @Inject() () {

  private val cache: Cache[String, Map[Int, OpenPeriod]] =
    CacheBuilder
      .newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build()

  private[services] def size: Long = {
    cache.cleanUp()
    cache.size()
  }

  def getForRegNumber(regNumber: String): Seq[OpenPeriod] =
    Option(cache.getIfPresent(regNumber)).map(_.values.toSeq).getOrElse(Seq.empty)

  def putAll(regNumber: String, periods: Seq[OpenPeriod]): Unit =
    cache.put(regNumber, periods.map(p => p.consecNo -> p).toMap)

  def updateStatus(regNumber: String, consecNo: Int, status: Int): Boolean = {
    var updated = false
    cache
      .asMap()
      .computeIfPresent(
        regNumber,
        (_, periods) =>
          periods.get(consecNo) match {
            case Some(period) =>
              updated = true
              periods.updated(consecNo, period.copy(status = status))
            case None => periods
          }
      )
    updated
  }
}
