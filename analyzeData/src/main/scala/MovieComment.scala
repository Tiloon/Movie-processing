import play.api.libs.json.Json

object MovieComment {
  case class MovieComment (author : String,
                      url : String,
                      id : String,
                      content : String)
  implicit val Reads = Json.reads[MovieComment]
  implicit val Writes = Json.writes[MovieComment]
}
