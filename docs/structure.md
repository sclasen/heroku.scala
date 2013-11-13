# project structure

How does all this stuff work?

### production code

* There are base abstractions defined in `api/src/main/scala`
* at compile time, the first code generator kicks in: `ModelBoilerplateGen`
* based on `schema.json` and the base abstractions, code is generated for the Api and compiled
* once the api module is compiled, the second set of generators kicks in: `SprayJsonBoilerplateGen`
* this reflects all the types present in `ApiRequestJson` and `ApiResponseJson` (previously generated) and generates Json serializers and deserializers for all required types

### test code

* there are abstract tests in `api/src/test/scala`
* client implementations should provide integration tests based on these by:
* implementing a base test trait, self typed to `ApiSpec`. See `SprayApiSpec`
* creating concrete tests by applying this trait to each abstract test. See `IntegrationTests` in the spray-client module
* `class SprayAccountSpec extends AccountSpec(SprayJsonBoilerplate) with SprayApiSpec`



