package services


// Reactive Mongo Imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import play.Logger

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
    Logger.info(s"Looking up user ${username}...")
    val collection = db("users")
    val qb = QueryBuilder().query(Json.obj("userName" -> username, "password" -> password))
    val cursor = collection.find[JsValue](qb)
    // not sure if this is ideal, but it seems to work for now...
    Await.result(cursor.headOption, 1 second).map(user => Some((user \ "userName").as[String])).getOrElse(None)
  }
}
