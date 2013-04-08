package controllers

import play.api._
import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 4/6/13
 * Time: 8:52 PM
 * Trait to encapsulate the authentication (and potentially authorization) for the API.
 *
 * Lifted shamelessly from the Play Framework ZenTasks example
 */
trait Secured {
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) : Option[String] = {
    request.headers.get("Authorization").map{ basicAuth =>
      lookupCredentials(basicAuth)
    }.getOrElse(None)
  }

  private def lookupCredentials(basicAuth : String)  : Option[String] = {
    decodeBasicAuth(basicAuth).flatMap { creds =>
    // TODO - look up user/pass in the db
      if (creds.username != "blaz")
        None
      else
        Some(creds.username)
    }
  }

  private def decodeBasicAuth(auth: String) : Option[UserCredentials] = {
    auth.split(" ").drop(1).headOption.flatMap { encoded =>
      new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
        case u :: p :: Nil => Some(UserCredentials(u,p))
        case _ => None
      }
    }
  }

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Unauthorized.withHeaders("WWW-Authenticate" -> "Basic realm=\"ProperLLC\"")

  // --

  /**
   * Action for authenticated users.
   */
  // TODO - work the body parser in here somehow...
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  def Secured[A](username: String, password: String)(action: Action[A]) = Action(action.parser) { request =>
    request.headers.get("Authorization").flatMap { authorization =>
      authorization.split(" ").drop(1).headOption.filter { encoded =>
        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
          case u :: p :: Nil if u == username && password == p => true
          case _ => false
        }
      }.map(_ => action(request))
    }.getOrElse {
      Results.Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured"""")
    }
  }

}


case class UserCredentials(username : String, password : String)
