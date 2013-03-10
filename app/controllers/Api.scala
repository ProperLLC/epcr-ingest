package controllers

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 3/10/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
object Api extends Controller {

    def incidentIngest = Action { request =>
      // validate XML is legit (somehow? phase 2?)
      // parse (should this be in a helper class?)
      // hand to back end for storage
      // return results
      Ok(views.html.index("Your new application is ready."))
    }
}
