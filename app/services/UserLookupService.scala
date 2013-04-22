package services


// Reactive Mongo Imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

// Play Json imports
import play.api.libs.json._
import play.api.Play.current

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Simple service that will lookup a user given user credentials.
 *
 * User: terry
 * Date: 4/21/13
 * Time: 10:15 PM
 *
 */
object UserLookupService {

  val db : DefaultDB = ReactiveMongoPlugin.db

  def findByUsernamePassword(username : String, password : String) : Option[String] = {
    val collection = db("users")
    val qb = QueryBuilder().query(Json.obj("userName" -> username, "password" -> password))
    // if we found any with that sequenceId - we nuke them
    val cursor = collection.find[JsValue](qb)
//
//    val results = for {
//      maybeUser <-  cursor.headOption
//      result <- maybeUser.map(user => Some((user \ "userName").as[String]))
//    } yield result

    Await.result(cursor.headOption, 1 second).map(user => Some((user \ "userName").as[String])).getOrElse(None)

  }
}
