import MovieComment.MovieComment
import play.api.libs.json.Json

object Movie {
  case class Movie(backdrop_path: Option[String],
                   adult: Boolean,
                   vote_count: Int,
                   poster_path: Option[String],
                   genre_ids: List[Int],
                   id: Int,
                   release_date: String,
                   original_language: String,
                   popularity: Float,
                   comments: List[MovieComment],
                   original_title: String,
                   title: String,
                   overview: String,
                   video: Boolean,
                   vote_average: Float)
  implicit val Reads = Json.reads[Movie]
}
