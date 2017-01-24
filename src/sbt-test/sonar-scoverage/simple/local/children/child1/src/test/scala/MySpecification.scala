import org.specs2._

class MySpecification extends org.specs2.mutable.Specification {
  "this is my specification" >> {
    val s = new Server1
    s.start(Some("123")) must_== Some(123)
//    s.start(Some("12345")) must_== None
    s.start(None) must_== None
    s.serve must_== "hello"
    s.stop
    success
  }
}
