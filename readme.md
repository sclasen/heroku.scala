# heroku.scala

Asynchronous Scala Client for the [Heroku Platform API](https://devcenter.heroku.com/articles/platform-api-reference).

This project aims to provide a complete, typesafe and well tested scala library for interacting with the heroku platform api. 

The `api` module of this project provides the abstractions necessary for one to easily build a client using the http stack and json stack of one's choosing. 

The `spray-client` module of this project provides a full client implementation based on [spray-client](https://github.com/spray/spray) and [spray-json](https://github.com/spray/spray-json).

## roll your own client

Plugging in one's own http client involves implementing the `com.heroku.platform.api.Api` trait, which contains 4 abstract methods. That implementation can then be used with the provided spray-json based `SprayJsonBoilerplate`, or with json serializers/deserialzers implemented with another json stack.

The json serializations/deserializations are driven by a granular set of implicits, so if you want to implement a client for a single operation on a single endpoint, you will only need to implement a single deserializer for the response, and a serializer for the request if it contains a body.

There are some examples of this in the `examples` directory.

the `spray-jackson` example shows using the spray http client with an implementation of the json serializers for `Key` operations based on `jackson-scala`

the `finagle-spray` example shows using a finagle https client implementation with the provided `spray-json` de/serializers

## code generation

The heroku platform api is specified using json schema, and we take advantage of this to generate much of the boilerplate involved in the client code. Hand-coded abstractions are used as a basis for generating the code for each endpoint. The generated code is not checked in to the `master` branch, but is checked into the `generated` branch [here for the models](https://github.com/heroku/heroku.scala/tree/generated/api/src_managed/main/scala/com/heroku/platform/api) and [here for the json boilerplate](https://github.com/heroku/heroku.scala/blob/generated/spray-client/src_managed/main/scala/com/heroku/platform/api/client/spray/SprayJsonBoilerplate.scala).

The code generators live in the `boilerplate-generator` project, and are driven by sbt.

## usage

This example shows the usage of the included client implementation based on spray-client and spray-json. 

Create a simple sbt project by making a directory and placing the following in `build.sbt` in that directory

```scala
scalaVersion := "2.10.2"

libraryDependencies += "io.spray" %% "spray-json" % "1.2.5"

libraryDependencies += "com.heroku.platform.api" %% "spray-client" % "0.0.2-BETA"
```

then run `sbt console`

```scala
val apiKey = ...your api key...

//once your apiKey is set, you can cut and paste the rest

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

// Low level api

val api = SprayApi(system)

// simple async api, throws exceptions when an error response is recieved

val simpleApi = SimpleApi(api, apiKey)

// synchronous api, uses SimpleApi under the hood, and wraps it in Await.
// Good for console usage.

val syncApi = SyncApi(api, apiKey)

val app = syncApi.execute(HerokuApp.Create())
println(s"created app ${app.name} with syncApi")


simpleApi.execute(HerokuApp.Info(app.id)).map {
    appInfo:HerokuApp => println(s"got app info for ${appInfo.name} with simpleApi")
}


api.execute(HerokuApp.Info(app.name), apiKey).map {
   case Left(Response(status, headers, ErrorResponse(id, msg))) => 
        println(s"failed to get app info: $id $msg")
   case Right(Response(status, headers, appInfo)) =>
        println(s"got app info with api: ${appInfo.name}, id is ${appInfo.id}")
}

```

