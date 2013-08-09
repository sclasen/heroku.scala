# heroku.scala

Async Scala Client for the V3 version of the heroku API.

## Usage (Spray Client impl shown)

```
import com.heroku.platform.api._
import com.heroku.platform.api.client.spray.SprayApiJson._
import com.heroku.platform.api.client.spray.SprayApi
import akka.actor._

val system = ActorSystem("api")

val api = new SprayApi(system)

val apiKey = ...

api.execute(HerokuApp.Create(name = Some("my-app")), apiKey).map{
   case Left(ErrorResponse(_, msg)) => println(s"failed to create app: $msg")
   case Right(app) => println(s"created app: ${app.name}, id is ${app.id}")
}
```

