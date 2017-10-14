package tests

import models.TestRecords.{ TestRecord, TestRecords }
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

class ModelsSpec extends SpecBase {
  "TestRecords model" should {
    "getAll() returns empry sequence when DB is empry" in withDB { implicit db =>
      val f = TestRecords.getAll()
      Await.ready(f, Duration.Inf)
      f.value mustBe Some(Success(Seq()))
    }

    "create() returns ID of inserted record" in withDB {  implicit db =>
      val f = TestRecords.create(TestRecord(0, "Jhon Doe", 1, None, None))
      Await.ready(f, Duration.Inf)
      f.value.isDefined mustBe true
      f.value.value.isSuccess mustBe true
    }
  }
}
