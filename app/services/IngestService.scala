package services

import akka.actor._
import concurrent.ExecutionContext.Implicits.global

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.ReactiveBSONImplicits._

// Reactive Mongo Imports
import reactivemongo.bson._

// Play Json imports
import play.api.libs.json._

import play.api.Play.current
import play.api.libs.json.JsValue

import org.joda.time._

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 3/24/13
 * Time: 6:33 PM
 *
 * This actor contains the business logic for processing an inbound incident report.
 *
 */

// TODO - doing this inline just to get mechanics working; likely need to create a companion class for this
class IngestService extends Actor {

  val db = ReactiveMongoPlugin.db
  lazy val collection = db("incidents")

  def receive = {
    case Save(incident, fileName, user) => {
      // add wrapper elements to JsValue
      val incidentWrapper = IngestService.createIncidentWrapper(incident, fileName, user)
      // save to mongo
      // TODO - move to DAO or something...
      collection.insert[JsValue](incidentWrapper).map( lastError =>
        println(s"Error while saving: $lastError")
      )
      // send the results back to the caller
      sender ! incidentWrapper
    }
  }
}

object IngestService {
  def createIncidentWrapper(incident : JsValue, fileName : String, user : String) : JsObject = {
    var Array(deptCode, formId, deviceId) = fileName.split('.')
    var inDate = new DateTime()
    Json.obj(
      "formId" ->  formId,
      "departmentCode" -> deptCode, // first two letters of the file name
      "hospitalCode" -> (incident \ "transporthospcode"),  // transporthospcode
      "deviceId" -> deviceId,
      "sequenceId" -> fileName, // must be unique; currently the name of the xml file; if receive duplicate, overwrite
      "complete" -> false,
      "formData" -> incident,
      "statusHistory" -> Json.arr(
        Json.obj(
          "dateTime" -> inDate.getMillis,
          "status" -> "SUBMITTED"
        )
      ),
      "audit" -> Json.obj(
        "user" -> user,
        "dateCreated" -> inDate.getMillis
      )
    )
  }
}

// Messages we handle
case class Save( incident : JsValue, fileName : String, user : String)