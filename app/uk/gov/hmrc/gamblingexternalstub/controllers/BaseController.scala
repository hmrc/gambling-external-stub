package uk.gov.hmrc.gamblingexternalstub.controllers

import play.api.Logging
import play.api.http.HttpEntity
import play.api.mvc.Result

trait BaseController extends Logging {

  def logResult(msg: String, result: Result, limit: Int = 70): Unit =
    logger.info(s"$msg ${result._1.status} ${result._2.asInstanceOf[HttpEntity.Strict]._1.map(_.toChar).mkString.take(limit)}")

}
