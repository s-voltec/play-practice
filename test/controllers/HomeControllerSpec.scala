package tests

import controllers.routes
import play.api.test.FakeRequest
import play.api.test.Helpers._

//class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
class HomeControllerSpec extends SpecBase {
  "HomeController GET" should {
    "render the index page" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }

  }

  "HomeController POST" should {
    """redirect to index with flash message "ok" if request is valid""" in {
      val request = FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name" -> "Jhon Doe",
        "gender" -> "1",
        // address
        "birthday" -> "1970-01-02"
      )
      val post = route(app, request).get

      status(post) mustBe SEE_OTHER
      header(LOCATION, post) mustBe Some(routes.HomeController.index().path())
      flash(post).data.get("message") mustBe Some("ok")
    }

    """redirect to index with flash message "ng" if request is invalid""" in {
      val request = FakeRequest(POST, "/")
      val post = route(app, request).get

      status(post) mustBe SEE_OTHER
      header(LOCATION, post) mustBe Some(routes.HomeController.index().path())
      flash(post).data.get("message") mustBe Some("ng")
    }
  }
}
