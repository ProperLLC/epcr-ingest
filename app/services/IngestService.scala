package services

import akka.actor._
import scala.concurrent.Await
import akka.util.Timeout

// Reactive Mongo Imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

// Play Json imports
import play.api.libs.json._

import org.joda.time._

import play.api.Play.current
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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

  val db : DefaultDB = ReactiveMongoPlugin.db
  //lazy val collection : Collection = db.("incidents")
  implicit val timeout = Timeout(1 second)

  def receive = {
    case Save(incident, fileName, user) => {
      val collection = db("incidents")
      // add wrapper elements to JsValue
      val incidentWrapper = IngestService.createIncidentWrapper(incident, fileName, user)
      // look to see if there's any here with the sequenceId we have...
      val qb = QueryBuilder().query(Json.obj("fileName" -> fileName))
      // if we found any with that sequenceId - we nuke them
      val results = collection.find[JsValue](qb).toList.map {  incident =>
         collection.remove[JsValue](Json.obj("fileName" -> fileName)).map( lastError =>
            println(s"Results of removing documents: $lastError")
         )
      } map { _ =>
        // save to mongo
        // TODO - move to DAO or something...
        collection.insert[JsValue](incidentWrapper).map( lastError =>
          println(s"Results of save: $lastError")
        )
      }
      // send the results back to the caller  (should we wait for any of the mongo stuff to complete, or trust it its async'edness??
      sender ! incidentWrapper
    }
  }
}

object IngestService {
  def createIncidentWrapper(incident : JsValue, fileName : String, user : String) : JsObject = {
    var Array(deptCode, formId, sequenceId) = fileName.split('.')
    var inDate = new DateTime()
    Json.obj(
      "formId" ->  formId,
      "departmentCode" -> deptCode, // first two letters of the file name
      "hospitalCode" -> (incident \ "transporthospcode"),  // transporthospcode
      "sequenceId" -> sequenceId, // must be unique; currently the name of the xml file; if receive duplicate, overwrite
      "fileName" -> fileName,
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