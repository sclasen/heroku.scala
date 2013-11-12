# heroku.scala

Async Scala Client for the V3 version of the heroku API.

## Usage (Spray Client impl shown)

```
import com.heroku.platform.api._
import com.heroku.platform.api.client.spray.SprayJsonBoilerplate._
import com.heroku.platform.api.client.spray.SprayApi
import akka.actor._

val system = ActorSystem("api")

implicit val ctx = system.dispatcher

val api = SprayApi(system)

val apiKey = ...your api key...

api.execute(HerokuApp.Create(name = Some("my-app")), apiKey).map {
   case Left(Response(status, headers, ErrorResponse(id, msg))) => println(s"failed to create app: $msg")
   case Right(Response(status, headers, app)) => println(s"created app: ${app.name}, id is ${app.id}")
}

val simpleApi = SimpleApi(api, apiKey)

simpleApi.execute(HerokuApp.Info("my-app")).map {
    app:HerokuApp => println(s"got app info for ${app.name}")
}


val syncApi = SyncApi(api, apiKey)
val app = syncApi.execute(HerokuApp.Info("my-app"))
println(s" synchronously got app info for ${app.name}")}

```

