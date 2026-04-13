package uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.gamblingexternalstub.models.ReturnSummary
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MgdController @Inject()(
                               cc: ControllerComponents
                             )(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getReturnSummary(mgdRegNumber: String): Action[AnyContent] = Action { _ =>

    mgdRegNumber match {

      case "" =>
        logger.warn("[MGD Stub] Missing mgdRegNumber")
        BadRequest(Json.obj(
          "code" -> "INVALID_MGD_REG_NUMBER",
          "message" -> "mgdRegNumber must be provided"
        ))

      case "error" =>
        logger.error("[MGD Stub] Forced unexpected error")
        InternalServerError(Json.obj(
          "code" -> "UNEXPECTED_ERROR",
          "message" -> "Unexpected error occurred"
        ))

      case reg =>
        logger.info(s"[MGD Stub] Returning summary for $reg")

        Ok(Json.toJson(
          ReturnSummary(
            mgdRegNumber = reg,
            returnsDue = 2,
            returnsOverdue = 1
          )
        ))
    }
  }
}