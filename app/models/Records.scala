package models


protected object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with generated.Tables

object TestRecords {
  type TestRecord = Tables.TestRecordsRow
  val TestRecord = Tables.TestRecordsRow

  object TestRecords {
    import scala.concurrent.Future
    import Tables.profile.api._

    def create(record: TestRecord)(implicit db: Database): Future[Long] = {
      db.run((Tables.TestRecords returning Tables.TestRecords.map(_.id)) += record)
    }

    def getAll()(implicit db: Database): Future[Seq[TestRecord]] = {
      db.run(Tables.TestRecords.result)
    }
  }
}
