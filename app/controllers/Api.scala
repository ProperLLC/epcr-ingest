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
 * To change this template use File | Settings | File Templates.
 */
object Api extends Controller {

    def incidentIngest = Action(parse.xml) { request =>
      // validate XML is legit (somehow? phase 2?)
      // parse (should this be in a helper class?)
      // hand to back end for storage
      // return results
      println(request.body)
      val data = IngestXmlService.parseIncident(request.body)
      val json = Json.toJson(data)
      println("json => " + json)
      Ok(json).as(JSON)
    }

    implicit val objectMapFormat = new Format[Map[String, Any]] {

      def writes(map: Map[String, Any]): JsValue =
        Json.obj(map.map{case (s, o) =>
          val ret:(String, JsValueWrapper) = o match {
            case _:String => s -> JsString(o.asInstanceOf[String])
            case _ => s -> JsObject(o.asInstanceOf[Map[String, String]].map( node => node._1 -> JsString(node._2)).toList)
          }
          ret
        }.toSeq:_*)


      def reads(jv: JsValue): JsResult[Map[String, Object]] =
        JsSuccess(jv.as[Map[String, JsValue]].map{case (k, v) =>
          k -> (v match {
            case s:JsString => s.as[String]
            case l => l.as[List[String]]
          })
        })
    }
}
