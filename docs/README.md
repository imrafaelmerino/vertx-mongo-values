<img src="./logo/package_twitter_swe2n4mg/color1/full/coverphoto/color1_logo_light_background.png" alt="vertx-mongodb-effect"/>

[![Build Status](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect.svg?branch=master)](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect)
[![codecov](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/vertx-mongodb-effect.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/vertx-mongodb-effect)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/vertx-mongodb-effect/0.3)](https://search.maven.org/artifact/com.github.imrafaelmerino/vertx-mongodb-effect/0.3/jar)
[![](https://jitpack.io/v/imrafaelmerino/vertx-mongodb-effect.svg)](https://jitpack.io/#imrafaelmerino/vertx-mongodb-effect)

- [Introduction](#introduction)
- [Supported types](#types)
- [Supported operations](#operations)
- [Defining modules](#defmodules)
- [Deploying modules](#depmodules)
- [Publishing events](#events)
- [Requirements](#requirements)
- [Installation](#installation)
- [Release process](#release)


## <a name="introduction"><a/> Introduction

**vertx-mongodb-effect** allows us to work with **MongoDB** following a purely functional and reactive style.
It requires to be familiar with [vertx-effect](https://vertx.effect.imrafaelmerino.dev/#modules). Both
**vertx-effect** and **vertx-mongo-effect** use the immutable and persistent Json from 
[json-values](https://github.com/imrafaelmerino/json-values). **Jsons travel across the event bus, 
from verticle to verticle, back and forth, without being neither copied nor converted to BSON**.
 
## <a name="types"><a/> Supported types
**json-values** supports the standard Json types: string, number, null, object, array; 
There are five number specializations: int, long, double, decimal, and BigInteger. 
**json-values adds support for instants and binary data**. It serializes Instants into 
its string representation according to ISO-8601, and the binary type into a string encoded in base 64. 

**vertx-mongodb-effect** uses [mongo-values](https://mongo.values.imrafaelmerino.dev). 
It abstracts the processes of encoding to BSON and decoding from BSON. 	
Please find below the BSON types supported and their equivalent types in json-values.

```java    

Map<BsonType, Class<?>> map = new HashMap<>();
map.put(BsonType.NULL, JsNull.class);
map.put(BsonType.ARRAY, JsArray.class);
map.put(BsonType.BINARY, JsBinary.class);
map.put(BsonType.BOOLEAN, JsBool.class);
map.put(BsonType.DATE_TIME, JsInstant.class);
map.put(BsonType.DOCUMENT, JsObj.class);
map.put(BsonType.DOUBLE, JsDouble.class);
map.put(BsonType.INT32, JsInt.class);
map.put(BsonType.INT64, JsLong.class);
map.put(BsonType.DECIMAL128, JsBigDec.class);
map.put(BsonType.STRING, JsStr.class);

```

When defining the mongodb settings, **you have to specify the codec registry _JsValuesRegistry_ from mongo-values**:

```java
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import mongovalues.JsValuesRegistry;

MongoClientSettings  settings =
             MongoClientSettings.builder()
                                .applyConnectionString(connString)
                                .codecRegistry(JsValuesRegistry.INSTANCE)
                                .build();
``` 

## <a name="operations"><a/> Supported operations 
**Every method of the MongoDB driver has an associated lambda**. Verticles can't send [sessions](https://docs.mongodb.com/manual/reference/method/Session/) 
and [transactions](https://docs.mongodb.com/manual/core/transactions/) across the event bus; nevertheless, [spawning](https://vertx.effect.imrafaelmerino.dev/#spawning-verticles) verticles opens the door to using them. 

Since **vertx-mongodb-effect** uses the driver API directly, it can benefit from all its features and methods. 
**It's an advantage over the official vertx-mongodb-client**.

Please find below the types and constructors of the most essentials operations:

**Count :: λc<JsObj, Long>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;

public Count(Supplier<MongoCollection<JsObj>> collectionSupplier,
             CountOptions options
            )
```

**DeleteMany :: λc<JsObj, O>**
 
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.ClientSession;

public DeleteMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<DeleteResult, O> resultConverter,
                  DeleteOptions options 
                 )
                      
public DeleteMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<DeleteResult, O> resultConverter,
                  DeleteOptions options,
                  ClientSession session 
                 )                           
```   
    
**DeleteOne :: λc<JsObj, O>**
    
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.ClientSession;

public DeleteOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<DeleteResult, O> resultConverter,
                 DeleteOptions options 
                )
                      
public DeleteOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<DeleteResult, O> resultConverter,
                 DeleteOptions options,
                 ClientSession session
                )      
```

**FindAll :: λc<FindMessage, JsArray>**

    
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public FindAll(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsArray> converter 
              )
```    

**FindOne :: λc<FindMessage, JsObj>**
    
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public FindOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsObj> converter 
              )                 
```    

**FindOneAndDelete :: λc<JsObj, JsObj>**
    
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.ClientSession;

public FindOneAndDelete(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndDeleteOptions options
                       ) 

public FindOneAndDelete(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndDeleteOptions options,
                        ClientSession session 
                       )     
```   

**FindOneAndReplace :: λc<UpdateMessage, JsObj>**
    
```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.ClientSession;

public FindOneAndReplace(Supplier<MongoCollection<JsObj>> collectionSupplier,
                         FindOneAndReplaceOptions options
                        )   

public FindOneAndReplace(Supplier<MongoCollection<JsObj>> collectionSupplier,
                         FindOneAndReplaceOptions options,
                         ClientSession session
                        )  
```    

**FindOneAndUpdate :: λc<UpdateMessage, JsObj>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.ClientSession;

public FindOneAndUpdate(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndUpdateOptions options
                       )

public FindOneAndUpdate(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndUpdateOptions options,
                        ClientSession session
                       )     
```    

**InsertMany :: λc<JsArray, R>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.ClientSession;

public InsertMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<InsertManyResult, R> resultConverter,
                  InsertManyOptions options
                 )   

public InsertMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<InsertManyResult, R> resultConverter,
                  InsertManyOptions options,
                  ClientSession session 
                 ) 
```    

**InsertOne :: λc<JsObj, R>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.ClientSession;

public InsertOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<InsertOneResult, R> resultConverter,
                 InsertOneOptions options
                )    

public InsertOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<InsertOneResult, R> resultConverter,
                 InsertOneOptions options,
                 ClientSession session
                ) 
```    

**ReplaceOne :: λc<UpdateMessage, O>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.ClientSession;

public ReplaceOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ReplaceOptions options
                 )   

public ReplaceOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ReplaceOptions options,
                  ClientSession session
                 )  
```    
**UpdateMany :: λc<UpdateMessage, O>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.ClientSession;

public UpdateMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  UpdateOptions options
                 )

public UpdateMany(UpdateOptions options,
                  Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ClientSession session
                 )     
```    

**UpdateOne :: λc<UpdateMessage, O>**

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.ClientSession;

public UpdateOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<UpdateResult, O> resultConverter,
                 UpdateOptions options
                )

public UpdateOne(UpdateOptions options,
                 Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<UpdateResult, O> resultConverter,
                 ClientSession session
                )     
```    


## <a name="defmodules"><a/> Defining modules
Like with vertx-effect, [modules](https://vertx.effect.imrafaelmerino.dev/#modules) to deploy 
verticles and expose lambdas to communicate with them.
The typical scenario is to create a module per collection. We can deploy or spawn verticles. 

The following modules are just a couple of examples.


We create a module where all the lambdas make read operations and spawn verticles to reach a significant level of parallelization:

```java
import vertx.mongodb.effect.MongoModule;
import vertx.effect.λc;

public class ReadModule extends MongoModule {

    public MyCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    public static λc<FindMessage, Optional<JsObj>> findOne;
    public static λc<FindMessage, JsArray> findAll;
    public static λc<JsObj, Long> count;
    public static λc<JsArray, JsArray> aggregate;

    @Override
    protected void deploy() {}  

    @Override
    protected void initialize() {
        λc<FindMessage, JsObj> findOneLambda = vertxRef.spawn("find_one",
                                                             new FindOne(collection)
                                                            );
        this.findOne = (context,message) -> findOneLambda.apply(context,message)
                                                         .map(Optional::ofNullable);
        this.findAll = vertxRef.spawn("find_all",
                                      new FindAll(collection)
                                     );
        this.count = vertxRef.spawn("count",
                                    new Count(collection)
                                   );

        this.aggregate = vertxRef.spawn("aggregate",
                                        new Aggregate<>(collection,
                                                        Converters.aggregateResult2JsArray
                                                       )
                                       );
    }
}
```

We create a module where all the lambdas make delete, insert and update operations, and deploy only one
instance per verticle. 


```java
import vertx.mongodb.effect.MongoModule;
import vertx.effect.λc;

public class MyCollectionModule extends MongoModule {

    public MyCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    public static λc<JsObj, String> insertOne;
    public static λc<JsObj, JsObj> deleteOne;
    public static λc<UpdateMessage, JsObj> replaceOne;
    public static λc<UpdateMessage, JsObj> updateOne;

    @Override
    protected void deploy() {
        this.deploy(INSERT_ONE_ADDRESS,
                    new InsertOne<>(collection,
                                    Converters.insertOneResult2HexId
                                   ),
                   );
        this.deploy(DELETE_ONE_ADDRESS,
                    new DeleteOne<>(collection,
                                    Converters.deleteResult2JsObj
                                   )
                   );

        this.deploy(REPLACE_ONE_ADDRESS,
                    new ReplaceOne<>(collection,
                                     Converters.updateResult2JsObj
                                    )
                   );
        this.deploy(UPDATE_ONE_ADDRESS,
                    new UpdateOne<>(collection,
                                    Converters.updateResult2JsObj
                                   )
                   );
    }  

    @Override
    protected void initialize() {
        this.insertOne = this.trace(INSERT_ONE_ADDRESS);
        this.deleteOne = this.trace(DELETE_ONE_ADDRESS);
        this.replaceOne = this.trace(REPLACE_ONE_ADDRESS);
        this.updateOne = this.trace(UPDATE_ONE_ADDRESS);
    }

    private static final String DELETE_ONE_ADDRESS = "delete_one";   
    private static final String UPDATE_ONE_ADDRESS = "update_one";
    private static final String REPLACE_ONE_ADDRESS = "replace_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    private static final String DELETE_MANY_ADDRESS = "delete_all";

}

``` 
## <a name="depmodules"><a/> Deploying modules 

The verticles _RegisterMongoEffectCodecs_ and _RegisterJsValuesCodecs_ need to be deployed to register the vertx message codecs.
Remember that you can't send any message to the event bus. If a message is not supported by Vertx you have to
create a _MessageCodec_.


```java
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import vertx.effect.VertxRef;

// define every timeout if you wanna be reactive
int connectTimeoutMS =  ???;
int socketTimeoutMS = ???;
int serverSelectionTimeoutMS = ???;

String connectionUrl = 
     String.format("mongodb://localhost:27017/?connectTimeoutMS=%s&socketTimeoutMS=%s&serverSelectionTimeoutMS=%s",
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

String database = ???;
String collection = ???; 
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

BiFunction<Integer,String,Val<Optional<JsObj>>> findByCode = (attempts,code) ->
          MyCollectionModule.findOne
                            .apply(FindMessage.ofFilter(JsObj.of("code",
                                                                 JsStr.of(code)
                                                                )
                                                        ) 
                                   )                    
                            .retryIf(e -> Failures.MONGO_TIMEOUT_PRISM.getOptional
                                                                      .apply(e)
                                                                      .isPresent(),
                                     attempts
                                     )
                            .recoverWith(e -> Cons.success(Optional.empty()));
```
## <a name="events"><a/> Publishing events

Since **vertx-effect** publishes the most critical events into the address **vertx-effect-events**, 
it' possible to register consumers to explode that information. You can disable this feature 
with the Java system property **-Dpublish.events=false**. Thanks to λc, it's possible to correlate
different events that belongs to the same transaction.
Go to the vertx-effect [documentation]([vertx-effect doc](https://vertx.effect.imrafaelmerino.dev/#events)) 
for further details.


## <a name="requirements"><a/> Requirements 

   -  Java 11 or greater
   -  [vertx-effect](https://vertx.effect.imrafaelmerino.dev)
   -  [mongo driver sync](https://mongodb.github.io/mongo-java-driver/4.1/whats-new/)

## <a name="installation"><a/> Installation 
```xml
<dependency>
   <groupId>com.github.imrafaelmerino</groupId>
   <artifactId>vertx-mongodb-effect</artifactId>
   <version>0.3</version>
</dependency>
```

## <a name="release"><a/> Release process 
Every time a tagged commit is pushed into master, a Travis CI build will be triggered automatically and 
start the release process, deploying to Maven repositories and GitHub Releases. See the Travis conf file 
**.travis.yml** for further details. On the other hand, the master branch is read-only, and all the commits 
should be pushed to master through pull requests. 


