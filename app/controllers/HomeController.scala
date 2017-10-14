package controllers

import javax.inject.{ Inject, Singleton }
import models.TestRecords.{ TestRecord, TestRecords }
import play.api.data.Form
import play.api.data.Forms. { nonEmptyText, mapping, optional, sqlDate, text }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.mvc.{ Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest }
import scala.concurrent.{ ExecutionContext, Future }

case class RecordFormData(name: String, gender: String, address: Option[String], birthday: Option[java.sql.Date])

@Singleton
class HomeController @Inject()(
  val mcc: MessagesControllerComponents,
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(mcc)
    with HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] {
  implicit val implicitDB = db
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "gender" -> nonEmptyText,
      "address" -> optional(text),
      "birthday" -> optional(sqlDate)
    )(RecordFormData.apply)(RecordFormData.unapply)
  )

  def index(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    TestRecords.getAll() map { records =>
      Ok(views.html.index(records, form))
    }
  }
  def post(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    form.bindFromRequest.fold (
      formWithErrors => Future.successful(Redirect(routes.HomeController.index()).flashing("message" -> "ng")),
      userData => TestRecords.create(TestRecord(0, userData.name, userData.gender.toInt, userData.address, userData.birthday)) map { res =>
        Redirect(routes.HomeController.index()).flashing("message" -> "ok")
      }
    )
  }
}
