# heroku.scala

Async Scala Client for the V3 version of the [Heroku API](https://devcenter.heroku.com/articles/platform-api-reference).

This client allows one to plug in the http client and json stack of one's choosing. A client based on spray-client and spray-json is provided. 

Plugging in one's own http client involves implementing the `com.heroku.platform.api.Api` trait, which contains 4 abstract methods.

The json serializations/deserializations are driven by a granular set of implicits, so if you want to implement a client for a single operation on a single endpoint, you will only need to implement a single deserializer for the response, and a serialiser for the request if it contains a body.

## code generation

The heroku platform api is specified using json schema, and we take advantage of this to generate much of the boilerplate involved in the client code. Hand-coded abstractions are used as a basis for generating the code for each endpoint. The generated code is not checked in to the master branch, but is checked into the generated branch [here for the models](https://github.com/heroku/heroku.scala/tree/generated/api/src_managed/main/scala/com/heroku/platform/api) and [here for the json boilerplate](https://github.com/heroku/heroku.scala/blob/generated/spray-client/src_managed/main/scala/com/heroku/platform/api/client/spray/SprayJsonBoilerplate.scala).

The code generators live in the `boilerplate-generator` project, and are driven by sbt.

## usage

This example shows the usage of the included client implementation based on spray-client and spray-json

```scala
// bring in the base api
import com.heroku.platform.api._

// bring in the implicits for json serialization/deserialization
import com.heroku.platform.api.client.spray.SprayJsonBoilerplate._

// bring in the spray client
import com.heroku.platform.api.client.spray.SprayApi

// bring in akka (used by spray)
import akka.actor._

val system = ActorSystem("api")

implicit val ctx = system.dispatcher

val api = SprayApi(system)

val apiKey = ...your api key...

// Low level api

api.execute(HerokuApp.Create(name = Some("my-app")), apiKey).map {
   case Left(Response(status, headers, ErrorResponse(id, msg))) => 
        println(s"failed to create app: $msg")
   case Right(Response(status, headers, app)) => 
        println(s"created app: ${app.name}, id is ${app.id}")
}

// simple async api, throws exceptions when an error response is recieved

val simpleApi = SimpleApi(api, apiKey)

simpleApi.execute(HerokuApp.Info("my-app")).map {
    app:HerokuApp => println(s"got app info for ${app.name}")
}

// synchronous api, uses SimpleApi under the hood, and wraps it in Await. 
// Good for console usage.

val syncApi = SyncApi(api, apiKey)
val app = syncApi.execute(HerokuApp.Info("my-app"))
println(s" synchronously got app info for ${app.name}")}

```

