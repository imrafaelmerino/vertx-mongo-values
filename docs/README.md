<img src="./logo/package_twitter_swe2n4mg/color1/full/coverphoto/color1_logo_light_background.png" alt="vertx-mongodb-effect"/>

[![Build Status](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect.svg?branch=master)](https://travis-ci.com/imrafaelmerino/vertx-mongodb-effect)
[![codecov](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/vertx-mongodb-effect)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_vertx-mongodb-effect&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_vertx-mongodb-effect)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/vertx-mongodb-effect.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/vertx-mongodb-effect)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/vertx-mongodb-effect/0.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/vertx-mongodb-effect/0.1/jar)
[![](https://jitpack.io/v/imrafaelmerino/vertx-mongodb-effect.svg)](https://jitpack.io/#imrafaelmerino/vertx-mongodb-effect)

- [Introduction](#introduction)
- [Supported types](#types)
- [Supported operations](#operations)
- [Defining modules](#defmodules)
- [Deploying modules](#depmodules)
- [Requirements](#requirements)
- [Installation](#installation)
- [Related projects](#rp)
- [Release process](#release)



## <a name="introduction"><a/> Introduction

**vertx-mongodb-effect** allows us to work with **MongoDB** following a purely functional and reactive style.
It requires to be familiar with [vertx-effect](https://vertx.effect.imrafaelmerino.dev/#modules). Both
**vertx-effect** and **vertx-mongo-effect** use the immutable and persistent Json from 
[json-values](https://github.com/imrafaelmerino/json-values). 

With **vertx-mongodb-effect** Jsons travel through all the system all the way down, without making any copy
nor conversion to BSON. 
  

## <a name="types"><a/> Supported types

json-values supports the standard Json types: string, number, null, object, array; There are five number specializations:
int, long, double, decimal and biginteger. json-values adds support for instants and binary data. Instants 
are serialized into its string representation according to ISO-8601; and the binary type is serialized into a 
string encoded in base 64. [mongo-values] abstracts the processes of enconding and decoding all these types into and from BSON.

When defining the mongo settings, you have to specify the codec registry _JsValuesRegistry_ from mongo-values:

```java
MongoClientSettings  settings =
             MongoClientSettings.builder()
                                .applyConnectionString(connString)
                                .codecRegistry(JsValuesRegistry.INSTANCE)
                                .build();
``` 

Find below the BSON types supported and their equivalent type from json-values.

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


## <a name="operations"><a/> Supported operations 

Every method of the mongodb driver has an associated lambda. Find below their types and constructors:

**Count :: λ<JsObj, Long>**

```java
public Count(Supplier<MongoCollection<JsObj>> collectionSupplier,
             CountOptions options
            )
```

**DeleteMany :: λ<JsObj, O>**
 
```java
public DeleteMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<DeleteResult, O> resultConverter,
                  DeleteOptions options)
                      
public DeleteMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<DeleteResult, O> resultConverter,
                  DeleteOptions options,
                  ClientSession session)                           
```   
    
**DeleteOne :: λ<JsObj, O>**
    
```java
public DeleteOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<DeleteResult, O> resultConverter,
                 DeleteOptions options)
                      
public DeleteOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<DeleteResult, O> resultConverter,
                 DeleteOptions options,
                 ClientSession session)      
```

**FindAll :: λ<FindMessage, JsArray>**

    
```java

public FindAll(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsArray> converter)

```    

**FindOne :: λ<FindMessage, JsObj>**
    
```java

public FindOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
               Function<FindIterable<JsObj>, JsObj> converter)    

```    

**FindOneAndDelete :: λ<JsObj, JsObj>**
    
```java

public FindOneAndDelete(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndDeleteOptions options) 

public FindOneAndDelete(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndDeleteOptions options,
                        ClientSession session)     

```   

**FindOneAndReplace :: λ<UpdateMessage, JsObj>**
    
```java
public FindOneAndReplace(Supplier<MongoCollection<JsObj>> collectionSupplier,
                         FindOneAndReplaceOptions options)   

public FindOneAndReplace(Supplier<MongoCollection<JsObj>> collectionSupplier,
                         FindOneAndReplaceOptions options,
                         ClientSession session)  
```    

**FindOneAndUpdate :: λ<UpdateMessage, JsObj>**

```java
public FindOneAndUpdate(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndUpdateOptions options)

public FindOneAndUpdate(Supplier<MongoCollection<JsObj>> collectionSupplier,
                        FindOneAndUpdateOptions options,
                        ClientSession session)     
```    

**InsertMany :: λ<JsArray, R>**

```java

public InsertMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<InsertManyResult, R> resultConverter,
                  InsertManyOptions options
                 )   

public InsertMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<InsertManyResult, R> resultConverter,
                  InsertManyOptions options,
                  ClientSession session) 

```    

**InsertOne :: λ<JsObj, R>**

```java

public InsertOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<InsertOneResult, R> resultConverter,
                 InsertOneOptions options
                )    

public InsertOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                 Function<InsertOneResult, R> resultConverter,
                 InsertOneOptions options,
                 ClientSession session) 

```    

**ReplaceOne :: λ<UpdateMessage, O>**

```java
public ReplaceOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ReplaceOptions options)   

public ReplaceOne(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ReplaceOptions options,
                  ClientSession session)  
```    
**UpdateMany :: λ<UpdateMessage, O>**

```java
public UpdateMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  UpdateOptions options
                 )

public UpdateMany(UpdateOptions options,
                  Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ClientSession session)     
```    

**UpdateOne :: λ<UpdateMessage, O>**

```java
public UpdateMany(Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  UpdateOptions options
                 )

public UpdateMany(UpdateOptions options,
                  Supplier<MongoCollection<JsObj>> collectionSupplier,
                  Function<UpdateResult, O> resultConverter,
                  ClientSession session)     
```    


## <a name="defmodules"><a/> Defining modules

As with vertx-effect, we use [modules](https://vertx.effect.imrafaelmerino.dev/#modules) to deploy 
verticles and exposes lambdas to communicate with them. The typical scenario is to create a module per 
collection. We can deploy or [spawn](https://vertx.effect.imrafaelmerino.dev/#spawning-verticles) verticles.


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
        this.deleteOne = this.ask(DELETE_ONE_ADDRESS);
        this.replaceOne = this.ask(REPLACE_ONE_ADDRESS);
        this.updateOne = this.ask(UPDATE_ONE_ADDRESS);
        
        λ<FindMessage, JsObj> findOneLambda = vertxRef.spawn("find_one",
                                                             new FindOne(collectionSupplier)
                                                            );
        this.findOne = m -> findOneLambda.apply(m)
                                         .map(Optional::ofNullable);
        this.findAll = vertxRef.spawn("find_all",
                                      new FindAll(collectionSupplier)
                                     );
        this.count = vertxRef.spawn("count",
                                    new Count(collectionSupplier)
                                   );

        this.updateMany = vertxRef.spawn("update_many",
                                         new UpdateMany<>(collectionSupplier,
                                                          Converters.updateResult2JsObj
                                                          )
                                        );
        this.findOneAndReplace = vertxRef.spawn("find_and_replace",
                                                new FindOneAndReplace(collectionSupplier)
                                               );
        this.findOneAndDelete = vertxRef.spawn("find_one_and_delete",
                                               new FindOneAndDelete(collectionSupplier)
                                              );
        this.findOneAndUpdate = vertxRef.spawn("find_one_and_update",
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
## <a name="depmodules"><a/> Deploying modules 

You need to register the Vertx message codecs from json-values and from this library to send all the required
types across the event bus. This is done by deploying the verticles _RegisterMongoEffectCodecs_ and
_RegisterJsValuesCodecs_. 


```java
int connectTimeoutMS = 2000;
int socketTimeoutMS = 5000;
int serverSelectionTimeoutMS = 3000;

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

## <a name="requirements"><a/> Requirements 


## <a name="installation"><a/> Installation 

## <a name="rp"><a/> Related projects 
## <a name="release"><a/> Release process 



