# dev setup

The integration tests use 2 heroku accouts so copy .env.sample to .env and fill out appropriately. Then use [foreman](http://ddollar.github.io/foreman/) or [forego](https://github.com/ddollar/forego) to run sbt.

```shell
forego run sbt
clean
it:compile
gen-idea
it:test
```
