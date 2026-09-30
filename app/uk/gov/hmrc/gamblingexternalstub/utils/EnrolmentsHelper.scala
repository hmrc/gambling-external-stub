/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.gamblingexternalstub.utils

import uk.gov.hmrc.gamblingexternalstub.actions.AuthenticatedRequest
import uk.gov.hmrc.gamblingexternalstub.models.Regime
import uk.gov.hmrc.gamblingexternalstub.models.Regime.MGD

class EnrolmentsHelper {

  def agentEnrolmentsOpt(request: AuthenticatedRequest[?], regime: Regime): Option[String] = {

    val (enrolKey, idName) = regime match {
      case MGD => ("HMRC-MGD-AGNT", "HMRCMGDAGENTREF")
      case _   => ("HMRC-GTS-AGNT", "HMRCGTSAGENTREF")
    }

    for {
      enrol          <- request.enrolments.getEnrolment(enrolKey)
      agentReference <- enrol.getIdentifier(idName)
    } yield agentReference.value
  }

}
