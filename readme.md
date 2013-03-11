# heroku.scala

Async Scala Client for the V3 version of the heroku API.

## Usage (Spray Client impl shown)

```
import com.heroku.api._
import com.heroku.api.spray._
import com.heroku.api.spray.SprayApi._
import akka.actor._

val system = new ActorSystem("api")

val api = new SprayApi(system)

val apiKey = ...

api.execute(AppCreate(name = Some("my-app")), apiKey).map{
   case Left(ErrorResponse(_, msg)) => println(s"failed to create app: $msg")
   case Right(app) => println(s"created app: ${app.name}, id is ${app.id}")
}
```

