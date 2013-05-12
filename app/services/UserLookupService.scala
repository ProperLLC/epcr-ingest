package services


// Reactive Mongo Imports
import reactivemongo.api._
import play.Logger

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection

// Play Json imports
import play.api.libs.json._
import play.api.Play.current

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import java.security.MessageDigest

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
  lazy val collection = db.collection[JSONCollection]("users")

  val digest = MessageDigest.getInstance("MD5")

  def md5(message : String) = {
    digest.digest(message.getBytes).map("%02x".format(_)).mkString
  }

  def findByUsernamePassword(username : String, password : String) : Option[String] = {
    Logger.info(s"Looking up user ${username}...")
    val cursor = collection.find(Json.obj("username" -> username, "password" -> md5(password))).cursor[JsValue]
    // not sure if this is ideal, but it seems to work for now...
    Await.result(cursor.headOption, 5 seconds).map(user => Some((user \ "username").as[String])).getOrElse(None)
  }
}
