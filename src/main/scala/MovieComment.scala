import play.api.libs.json.Json

/**
  * Created by tilon on 7/3/17.
  */
object MovieComment {
  case class MovieComment (author : String,
                      url : String,
                      id : String,
                      content : String)
  implicit val Reads = Json.reads[MovieComment]
  implicit val Writes = Json.writes[MovieComment]
}
