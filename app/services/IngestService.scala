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
    case Save(incident) => {
      // add wrapper elements to JsValue
      val incidentWrapper = Json.obj(
        "formId" ->  65432,
        "departmentCode" -> "BH",
        "hospitalCode" -> "XY",
        "deviceSequenceId" -> 1234,
        "complete" -> false,
        "formData" -> incident,
        "statusHistory" -> Json.arr(
          Json.obj(
            "dateTime" -> "2013-03-24T09:31:00-0700",
            "status" -> "SUBMITTED"
          )
        ),
        "audit" -> Json.obj(
          "user" -> "JSIXPACK",
          "dateCreated" -> "2013-03-24T09:31:00-0700"
        )
      )
      // save to mongo
      collection.insert[JsValue](incidentWrapper).map( lastError =>
        println(s"Error while saving: $lastError")
      )
    }
  }
}

// Messages we handle
case class Save( incident : JsValue)