package controllers

import play.api._
import play.api.mvc._
import services.UserLookupService

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

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) { request =>
      val result = for {
        credentials <- userCredentials(request)
        username <- userLookup(credentials)
      } yield f(AuthenticatedRequest(username, request))
      result getOrElse onUnauthorized(request)
    }
  }

  /**
   * Decode the basic auth header, if it exists.
   * @param auth
   * @return
   */
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

  /**
   * Function that handled looking up user credentials in the db. For now, we just return the username if we find a match.
   *
   * @param creds
   * @return
   */
  def userLookup(creds : UserCredentials) : Option[String] = {
    UserLookupService.findByUsernamePassword(creds.username, creds.password)
  }

  /**
   *
   * @param request
   * @return
   */
  private def userCredentials(request: RequestHeader) : Option[UserCredentials] = {
    request.headers.get("Authorization").map{ basicAuth =>
      decodeBasicAuth(basicAuth)
    }.getOrElse(None)
  }
}

case class AuthenticatedRequest[A](user: String, private val request: Request[A]) extends WrappedRequest(request)

case class UserCredentials(username : String, password : String)
