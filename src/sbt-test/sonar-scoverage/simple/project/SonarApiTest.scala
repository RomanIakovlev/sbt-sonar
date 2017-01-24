import scalaj.http._

object SonarApiTest {
  def clean(projectKey: String): Unit = {
    val resp = Http("http://localhost:9000/api/projects/delete")
      .method("POST")
      .param("key", projectKey)
      .auth("admin", "admin")
      .asString
    println(resp)
  }

  def sanityCheck(projectKey: String): Unit = {
    val resp: HttpResponse[String] =
      Http("http://localhost:9000/api/components/show").param("key", projectKey).auth("admin", "admin").asString
    println(resp)
    require(resp.isSuccess)

  }
}
