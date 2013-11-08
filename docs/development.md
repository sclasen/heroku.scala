# dev setup

The integration tests use 2 heroku accouts so copy .env.sample to .env and fill out appropriately. Then use foreman or forego to run sbt.

```
forego run sbt
;clean;it:compile;gen-idea
it:test
```