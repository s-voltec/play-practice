package tests

import org.scalatest.TestData
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import slick.basic.DatabaseConfig

trait SpecBase extends PlaySpec with GuiceOneAppPerTest with Injecting {
  override def newAppForTest(td: TestData): Application = new GuiceApplicationBuilder().configure(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.dataSourceClass" -> "slick.jdbc.DatabaseUrlDataSource",
    "slick.dbs.default.db.properties.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.properties.url" -> "jdbc:h2:mem:test"
  ).build()

  def withDB[T](body: slick.jdbc.JdbcBackend#Database => T)(implicit app: Application): T = {
    val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
    val dbConfig: DatabaseConfig[slick.jdbc.JdbcProfile] = dbConfigProvider.get
    implicit val db = dbConfig.db
    body(db)
  }
}
