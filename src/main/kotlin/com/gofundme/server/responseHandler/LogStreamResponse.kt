import java.io.Serializable
import java.time.LocalDateTime

class LogStreamResponse(
    val time:LocalDateTime= LocalDateTime.now(),
    val level:String,
    val serviceAffected:String,
    val message:String

):Serializable{

}