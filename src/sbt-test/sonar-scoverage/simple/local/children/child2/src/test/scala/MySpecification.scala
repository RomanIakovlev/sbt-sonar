import org.specs2._

class MySpecification extends org.specs2.mutable.Specification {
  "this is my specification" >> {
    val s = new Server2
    s.start
    s.serve must_== "world"
    s.stop
    success
  }
}