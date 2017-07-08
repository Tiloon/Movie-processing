import org.scalatest.FunSuite

class PersisterTest extends FunSuite {

  // this test is already green but see how we download the data in the loadData method
  test("checktopic") {
    println("data loaded")
    Persister2.persist()
  }
}
