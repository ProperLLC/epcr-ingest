package services

import akka.actor._
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
class IngestService extends Actor {

  def receive = {
    case Save(incident) => {
      // handle save
      // add wrapper elements to JsValue
      // pull out key values to upper layer
      // save to mongo
    }
  }
}

// Messages we handle
case class Save( incident : JsValue)