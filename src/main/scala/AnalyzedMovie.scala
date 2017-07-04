import Movie.Movie
import MovieComment.MovieComment
import play.api.libs.json.Json

/**
  * Created by tilon on 7/3/17.
  */
object AnalyzedMovie {
  case class AnalyzedMovie(backdrop_path: Option[String],
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
                           vote_average: Float,
                           commentWords: Int) {
    def this(movie: Movie) = this(
      movie.backdrop_path,
      movie.adult,
      movie.vote_count,
      movie.poster_path,
      movie.genre_ids,
      movie.id,
      movie.release_date,
      movie.original_language,
      movie.popularity,
      movie.comments,
      movie.original_title,
      movie.title,
      movie.overview,
      movie.video,
      movie.vote_average,
      movie.comments.map(x => x.content.split(" ").length).sum)
  }
  implicit val Writes = Json.writes[AnalyzedMovie]
}
