package controllers

import play.api.mvc._

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Json.JsValueWrapper

import services._

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 3/10/13
 * Time: 3:39 PM
 *
 * This is the core MVC controller class for the RESTful API.  It will intercept all the inbound calls and then send them
 * to the middleware for processing, and report the results.
 *
 * Please view the routes file (../conf/routes) to understand what HTTP endpoints we handle.
 *
 */
object Api extends Controller {

    def incidentIngest = Action(parse.xml) { request =>
      val data = IngestXmlService.parseIncident(request.body)
      // TODO - create async message to an actor to handle placing this into Mongo and returning a result code
      val json = Json.toJson(data)
      // for now this will simply output the XML as JSON to help us prove that our parsing works
      Ok(json).as(JSON)
    }


    //TODO - may want to move this to another class, perhaps where we parse the XML...but may depend on how/what we hand back to the actor that will put this into Mongo
    // note - taken (and modified) from: http://stackoverflow.com/questions/14467689/scala-to-json-in-play-framework-2-1
    implicit val objectMapFormat = new Format[Map[String, Any]] {

      def writes(map: Map[String, Any]): JsValue =
        Json.obj(map.map{case (s, o) =>
          val ret:(String, JsValueWrapper) = o match {
            case _:String => s -> JsString(o.asInstanceOf[String])
            case _ => s -> JsObject(o.asInstanceOf[Map[String, String]].map( node => node._1 -> JsString(node._2)).toList)
          }
          ret
        }.toSeq:_*)


      def reads(jv: JsValue): JsResult[Map[String, Any]] =
        JsSuccess(jv.as[Map[String, JsValue]].map{case (k, v) =>
          k -> (v match {
            case s:JsString => s.as[String]
            case l => l.as[Map[String, String]]
          })
        })
    }
}
