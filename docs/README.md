<img src="./logo/package_twitter_swe2n4mg/color1/full/coverphoto/white_logo_color1_background.png" alt="logo"/>

[![Build Status](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect.svg?branch=master)](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect)
[![codecov](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/vertx-mongodb-effect.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/vertx-mongodb-effect)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/vertx-mongodb-effect/0.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/vertx-mongodb-effect/0.1/jar)
[![](https://jitpack.io/v/imrafaelmerino/vertx-mongodb-effect.svg)](https://jitpack.io/#imrafaelmerino/vertx-mongodb-effect)


- [Introduction](#introduction)
- [What to use _vertx-mongodb-effect_ for and when to use it](#whatfor)
- [When not to use it](#notwhatfor)
- [Requirements](#requirements)
- [Installation](#installation)
- [Want to help](#wth)
- [Develop](#develop)
- [Related projects](#rp)

## <a name="introduction"><a/> Introduction 

vertx-mongodb-effect allows us to work with MongoDB following a purely functional and reactive style.
It requires to be familiar with [vertx-effect](https://imrafaelmerino.github.io/vertx-effect)

As with vertx-effect, we use [modules]() to deploy verticles and exposes lambdas to communicate with them.
The typical scenario is to create a module per collection. We can deploy or spawn verticles. 
It uses the immutable and persistent Json from [json-values](https://imrafaelmerino.github.io/json-values/).



```java
public class MyCollectionModule extends MongoModule {

    public MyCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    public static λ<JsObj, String> insertOne;
    public static λ<JsObj, JsObj> deleteOne;
    public static λ<JsArray, JsArray> insertMany;
    public static λ<JsObj, JsObj> deleteMany;
    public static λ<FindMessage, Optional<JsObj>> findOne;
    public static λ<FindMessage, JsArray> findAll;
    public static λ<UpdateMessage, JsObj> findOneAndReplace;
    public static λ<UpdateMessage, JsObj> replaceOne;
    public static λ<UpdateMessage, JsObj> updateOne;
    public static λ<JsObj, Long> count;
    public static λ<UpdateMessage, JsObj> updateMany;
    public static λ<JsArray, JsArray> aggregate;
    public static λ<JsObj, JsObj> findOneAndDelete;
    public static λ<UpdateMessage, JsObj> findOneAndUpdate;

    private static final String DELETE_ONE_ADDRESS = "delete_one";   
    private static final String UPDATE_ONE_ADDRESS = "update_one";
    private static final String REPLACE_ONE_ADDRESS = "replace_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    private static final String DELETE_MANY_ADDRESS = "delete_all";

    @Override
    protected void deploy() {
        this.deploy(INSERT_ONE_ADDRESS,
                    new InsertOne<>(collectionSupplier,
                                    Converters.insertOneResult2HexId
                    )
                   );
        this.deploy(INSERT_MANY_ADDRESS,
                    new InsertMany<>(collectionSupplier,
                                     Converters.insertManyResult2JsArrayOfHexIds
                    )
                   );
        this.deploy(DELETE_MANY_ADDRESS,
                    new DeleteMany<>(collectionSupplier,
                                     Converters.deleteResult2JsObj
                    )
                   );
        this.deploy(DELETE_ONE_ADDRESS,
                    new DeleteOne<>(collectionSupplier,
                                    Converters.deleteResult2JsObj
                    )
                   );

        this.deploy(REPLACE_ONE_ADDRESS,
                    new ReplaceOne<>(collectionSupplier,
                                     Converters.updateResult2JsObj
                    )
                   );
        this.deploy(UPDATE_ONE_ADDRESS,
                    new UpdateOne<>(collectionSupplier,
                                    Converters.updateResult2JsObj
                    )
                   );
    }  

    @Override
    protected void initialize() {
        this.insertOne = this.ask(INSERT_ONE_ADDRESS);
        this.insertMany = this.ask(INSERT_MANY_ADDRESS);
        this.deleteMany = this.ask(DELETE_MANY_ADDRESS);
        
        λ<FindMessage, JsObj> findOneLambda = vertxRef.spawn("findOne",
                                                             new FindOne(collectionSupplier)
                                                            );
        this.findOne = m -> findOneLambda.apply(m)
                                         .map(Optional::ofNullable);
        this.findAll = vertxRef.spawn("findAll",
                                      new FindAll(collectionSupplier)
                                     );
        this.count = vertxRef.spawn("count",
                                    new Count(collectionSupplier)
                                   );
        this.deleteOne = this.ask(DELETE_ONE_ADDRESS);
        this.replaceOne = this.ask(REPLACE_ONE_ADDRESS);
        this.updateOne = this.ask(UPDATE_ONE_ADDRESS);
        this.updateMany = vertxRef.spawn("updateMany",
                                         new UpdateMany<>(collectionSupplier,
                                                          Converters.updateResult2JsObj
                                                          )
                                        );
        this.findOneAndReplace = vertxRef.spawn("findAndReplace",
                                                new FindOneAndReplace(collectionSupplier)
                                               );
        this.findOneAndDelete = vertxRef.spawn("findOneAndDelete",
                                               new FindOneAndDelete(collectionSupplier)
                                              );
        this.findOneAndUpdate = vertxRef.spawn("findOneAndUpdate",
                                               new FindOneAndUpdate(collectionSupplier)
                                              );
        this.aggregate = vertxRef.spawn("aggregate",
                                        new Aggregate<>(collectionSupplier,
                                                        Converters.aggregateResult2JsArray
                                                       )
                                       );
    }
}

```

You need to register the Vertx codecs from json-values and this library to send all the required
messages across the event bus. This is done deploying the verticles _RegisterMongoEffectCodecs_ and
_RegisterJsValuesCodecs_. 

On the other hand, when defining the mongo settings, you have to specify the codec registry _JsValuesRegistry_.
from [mongo-values](). It abstracts the processes of decoding and econding BSONs, beeing able to work with
the Json from json-values all the way down, from a web handler for example to a mongo collection.

```java
int connectTimeoutMS = 2000;
int socketTimeoutMS = 5000;
int serverSelectionTimeoutMS = 3000;

String connectionUrl = String.format("mongodb://localhost:27017/?connectTimeoutMS=%s&socketTimeoutMS=%s&serverSelectionTimeoutMS=%s",
                                     connectTimeoutMS,
                                     socketTimeoutMS,
                                     serverSelectionTimeoutMS 
                                    );
 
ConnectionString connString = new ConnectionString(connectionUrl);

MongoClientSettings  settings =
             MongoClientSettings.builder()
                                .applyConnectionString(connString)
                                .codecRegistry(JsValuesRegistry.INSTANCE)
                                .build();

// one vertx client per database connection 
MongoVertxClient mongoClient = new MongoVertxClient(settings);

String database = "DB";
String collection = "Collection"
MyCollectionModule collectionModule = 
          new MyCollectionModule(mongoClient.getCollection(database,
                                                           collection
                                                          )
                                );

VertxRef vertxRef = new VertxRef(vertx);

Quadruple.sequential(vertxRef.deployVerticle(new RegisterJsValuesCodecs()),
                     vertxRef.deployVerticle(new RegisterMongoEffectCodecs()),
                     vertxRef.deployVerticle(mongoClient),
                     vertxRef.deployVerticle(collectionModule)
                     ) 
         .get();
```


Once everything is up and running, enjoy your lambdas!

```java

Function<JsObj,JsObj> byCode = doc -> JsObj.of("code",doc.get("code"));

λ<Optional<JsObj>, Optional<String>> setTimeStamp = opt -> 
{
    if (opt.isPresent()) 
    {
        JsObj doc = opt.get();
        if (!doc.containsKey("code"))
           return Cons.failure(new IllegalArgumentException("code is required"));
        return dataModule.updateOne
                         .apply(new UpdateMessage(byCode.apply(doc),
                                                  doc.set("timestamp",
                                                           JsInstant.of(Instant.now())
                                                         )
                                                  )
                                )
                         .map(result -> Optional.ofNullable(result.getStr("upsertedId")));
    }
    else return Cons.success(Optional.empty());
};

JsObj doc = ???;
dataModule.findOne
          .andThen(setTimeStamp)
          .apply(FindMessage.ofFilter(byCode.apply(doc)));
          

```
