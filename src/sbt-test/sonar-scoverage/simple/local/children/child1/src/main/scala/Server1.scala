class Server1 {
  def serve: String = "hello"
  def start(parameter: Option[String]): Option[Int] = {
    println("Start server 1")
    parameter.filter(_.length < 4).map(_.toInt)
  }
  def stop: Unit = {
    println("Stop server 1")
  }
}