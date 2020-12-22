package vertx.mongodb.effect;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jsonvalues.*;
import jsonvalues.gen.JsGens;
import jsonvalues.gen.JsObjGen;
import mongovalues.JsValuesRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.effect.RegisterJsValuesCodecs;
import vertx.effect.Val;
import vertx.effect.VertxRef;
import vertx.effect.exp.Quadruple;
import vertx.mongodb.effect.codecs.RegisterMongoEffectCodecs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static jsonvalues.JsBool.FALSE;
import static jsonvalues.JsBool.TRUE;
import static jsonvalues.JsNull.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static vertx.mongodb.effect.Converters.str2Oid;

@ExtendWith(VertxExtension.class)
public class MongoOpsTest {

    public static Random random = new Random();

    public static DataCollectionModule dataModule;

    static MongoClientSettings settings;


    @BeforeAll
    public static void prepare(Vertx vertx,
                               VertxTestContext testContext
                              ) {
        ConnectionString connString = new ConnectionString(
                "mongodb://localhost:27017/?connectTimeoutMS=10000&socketTimeoutMS=10000&serverSelectionTimeoutMS=10000"
        );

        settings = MongoClientSettings.builder()
                                      .applyConnectionString(connString)
                                      .codecRegistry(JsValuesRegistry.INSTANCE)
                                      .build();

        VertxRef vertxRef = new VertxRef(vertx);

        MongoVertxClient mongoClient = new MongoVertxClient(settings);

        dataModule = new DataCollectionModule(mongoClient.getCollection("test",
                                                                        "Data"
                                                                       ));

        Quadruple.sequential(vertxRef.deployVerticle(new RegisterJsValuesCodecs()),
                             vertxRef.deployVerticle(mongoClient),
                             vertxRef.deployVerticle(new RegisterMongoEffectCodecs()),
                             vertxRef.deployVerticle(dataModule)
                            )
                 .onComplete(TestFns.pipeTo(testContext))
                 .get();


    }


    @Test
    public void testInsert(VertxTestContext testContext) throws InterruptedException {

        int        number = 100;
        Checkpoint checkpoint             = testContext.checkpoint(number);
        var gen = JsObjGen.of("a",
                              JsGens.alphabetic,
                              "b",
                              JsGens.integer
                             );

        Supplier<JsObj> supplier = gen.apply(new Random());


        IntStream.range(0,
                        number
                       )
                 .parallel()
                 .forEach(i -> {
                     JsObj obj = supplier.get();
                     dataModule.insertOne.apply(obj)
                                         .flatMap(id -> dataModule.findOne.apply(FindMessage
                                                                                         .ofFilter(str2Oid.apply(id))
                                                                                )
                                                                          .map(it -> it.get()
                                                                                       .delete("_id"))
                                                                          .onSuccess(a -> {
                                                                              Assertions.assertEquals(obj,
                                                                                                      a
                                                                                                     );
                                                                              checkpoint.flag();
                                                                          })
                                                 ).get();

                 });



    }

    @Test
    public void test_insert_one(VertxTestContext context) {

        JsObj obj = JsObj.of("string",
                             JsStr.of("a"),
                             "int",
                             JsInt.of(Integer.MAX_VALUE),
                             "long",
                             JsLong.of(Long.MAX_VALUE),
                             "boolean",
                             TRUE,
                             "double",
                             JsDouble.of(1.5d),
                             "decimal",
                             JsBigDec.of(new BigDecimal("1.54456")),
                             "array",
                             JsArray.of(1,
                                        2,
                                        3
                                       ),
                             "null",
                             NULL,
                             "instant",
                             JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                             "biginteger",
                             JsBigInt.of(new BigInteger("11111111111111111111111"))
                            );

        dataModule.insertOne
                .apply(obj)
                .flatMap(id -> dataModule.findOne.apply(FindMessage.ofFilter(str2Oid.apply(id))))
                .onComplete(
                        TestFns.pipeTo(result -> assertEquals(Optional.of(obj),
                                                              result.map(it -> it.delete("_id"))
                                                             ),
                                       context
                                      )
                           )
                .get();


    }

    @Test
    public void test_find_all(VertxTestContext context) {

        int key = random.nextInt();

        Val<JsArray> val = dataModule.insertMany
                .apply(JsArray.of(JsObj.of("name",
                                           JsStr.of("Rafa"),
                                           "age",
                                           JsInt.of(38),
                                           "test",
                                           JsInt.of(key)
                                          ),
                                  JsObj.of("name",
                                           JsStr.of("Alberto"),
                                           "age",
                                           JsInt.of(10),
                                           "test",
                                           JsInt.of(key)

                                          ),
                                  JsObj.of("name",
                                           JsStr.of("Josefa"),
                                           "age",
                                           JsInt.of(49),
                                           "test",
                                           JsInt.of(key)
                                          )
                                 )
                      )
                .flatMap(ids -> {
                             FindMessage message = new FindMessageBuilder().maxTime(1,
                                                                                    TimeUnit.MILLISECONDS
                                                                                   )
                                                                           .filter(JsObj.of("test",
                                                                                            JsInt.of(key)
                                                                                           ))
                                                                           .create();
                             return dataModule
                                     .findAll
                                     .apply(message
                                           );
                         }
                        );


        Verifiers.<JsArray>verifySuccess(it -> it.size() == 3)
                .accept(val,
                        context
                       );


    }

    @Test
    public void test_delete_one(VertxTestContext context) {

        JsInstant now = JsInstant.of(Instant.now());

        JsObj filter = JsObj.of("time",
                                JsObj.of("$gte",
                                         now
                                        )
                               );

        JsObj doc = JsObj.of("time",
                             now
                            );

        dataModule.insertOne.apply(doc)
                            .flatMap(id -> dataModule.deleteOne.apply(filter))
                            .flatMap(deleteResult -> dataModule.findOne.apply(FindMessage.ofFilter(filter)))
                            .onComplete(optResult -> {
                                if (optResult.succeeded())
                                    context.verify(() -> Assertions.assertTrue(optResult.result()
                                                                                        .isEmpty())
                                                  );
                                context.completeNow();
                            })
                            .get();
    }

    @Test
    public void test_find_and_replace(VertxTestContext context) {

        int keyValue = random.nextInt();

        JsObj filter = JsObj.of("key",
                                JsInt.of(keyValue)
                               );

        JsObj obj = filter.union(JsObj.of("string",
                                          JsStr.of("a"),
                                          "int",
                                          JsInt.of(Integer.MAX_VALUE),
                                          "long",
                                          JsLong.of(Long.MAX_VALUE),
                                          "boolean",
                                          TRUE,
                                          "double",
                                          JsDouble.of(1.5d),
                                          "decimal",
                                          JsBigDec.of(new BigDecimal("1.54456")),
                                          "array",
                                          JsArray.of(1,
                                                     2,
                                                     3
                                                    ),
                                          "null",
                                          NULL,
                                          "instant",
                                          JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                                          "biginteger",
                                          JsBigInt.of(new BigInteger("11111111111111111111111"))
                                         )
                                );

        JsObj newObj = filter.union(JsObj.of("string",
                                             JsStr.of("new"),
                                             "int",
                                             JsInt.of(Integer.MIN_VALUE),
                                             "long",
                                             JsLong.of(Long.MIN_VALUE),
                                             "boolean",
                                             FALSE,
                                             "double",
                                             JsDouble.of(10.5d),
                                             "decimal",
                                             JsBigDec.of(new BigDecimal("1.544456")),
                                             "array",
                                             JsArray.of(1,
                                                        2,
                                                        3,
                                                        4,
                                                        5
                                                       ),
                                             "null",
                                             NULL,
                                             "instant",
                                             JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                                             "biginteger",
                                             JsBigInt.of(new BigInteger("21111111111111111111111"))

                                            ));

        dataModule.insertOne
                .apply(obj)
                .flatMap(id -> dataModule.findOneAndReplace.apply(new UpdateMessage(filter,
                                                                                    newObj
                                                                  )
                                                                 )
                        )
                .flatMap(r -> dataModule.findOne.apply(FindMessage.ofFilter(filter)))
                .onComplete(
                        TestFns.pipeTo(result -> {
                                           assertEquals(Optional.of(newObj),
                                                        result.map(it -> it.delete("_id"))
                                                       );
                                       },
                                       context
                                      )
                           )
                .get();
    }


    @Test
    public void test_find_sort_asc_and_projection(VertxTestContext context) {

        JsInt keyValue = JsInt.of(random.nextInt());
        JsObj filter = JsObj.of("key",
                                keyValue
                               );
        JsArray expectedSortAsc = JsArray.of(JsObj.of("name",
                                                      JsStr.of("Albert")
                                                     ),
                                             JsObj.of("name",
                                                      JsStr.of("Philip")
                                                     ),
                                             JsObj.of("name",
                                                      JsStr.of("Rafa")
                                                     )
                                            );
        JsObj projection = JsObj.of("name",
                                    JsInt.of(1),
                                    "_id",
                                    JsInt.of(0)

                                   );
        JsObj sortAsc = JsObj.of("age",
                                 JsInt.of(1)
                                );
        JsArray array = JsArray.of(JsObj.of("age",
                                            JsInt.of(16),
                                            "name",
                                            JsStr.of("Albert"),
                                            "key",
                                            keyValue
                                           ),
                                   JsObj.of("age",
                                            JsInt.of(25),
                                            "name",
                                            JsStr.of("Philip"),
                                            "key",
                                            keyValue
                                           ),
                                   JsObj.of("age",
                                            JsInt.of(38),
                                            "name",
                                            JsStr.of("Rafa"),
                                            "key",
                                            keyValue
                                           )
                                  );

        Verifiers.<JsArray>verifySuccess(expectedSortAsc::equals)
                .accept(dataModule.insertMany.apply(array)
                                             .flatMap(result -> dataModule.findAll.apply(FindMessage.ofFilter(filter,
                                                                                                              projection,
                                                                                                              sortAsc
                                                                                                             ))),
                        context
                       );

    }

    @Test
    public void test_find_sort_desc_and_projection(VertxTestContext context) {

        JsInt keyValue = JsInt.of(random.nextInt());
        JsObj filter = JsObj.of("key",
                                keyValue
                               );
        JsArray expectedSortDesc = JsArray.of(JsObj.of("name",
                                                       JsStr.of("Rafa")
                                                      ),
                                              JsObj.of("name",
                                                       JsStr.of("Philip")
                                                      ),
                                              JsObj.of("name",
                                                       JsStr.of("Albert")
                                                      )
                                             );
        JsObj projection = JsObj.of("name",
                                    JsInt.of(1),
                                    "_id",
                                    JsInt.of(0)

                                   );
        JsObj sortDesc = JsObj.of("age",
                                  JsInt.of(-1)
                                 );
        JsArray array = JsArray.of(JsObj.of("age",
                                            JsInt.of(16),
                                            "name",
                                            JsStr.of("Albert"),
                                            "key",
                                            keyValue
                                           ),
                                   JsObj.of("age",
                                            JsInt.of(25),
                                            "name",
                                            JsStr.of("Philip"),
                                            "key",
                                            keyValue
                                           ),
                                   JsObj.of("age",
                                            JsInt.of(38),
                                            "name",
                                            JsStr.of("Rafa"),
                                            "key",
                                            keyValue
                                           )
                                  );

        Verifiers.<JsArray>verifySuccess(expectedSortDesc::equals)
                .accept(dataModule.insertMany.apply(array)
                                             .flatMap(result -> dataModule.findAll.apply(FindMessage.ofFilter(filter,
                                                                                                              projection,
                                                                                                              sortDesc
                                                                                                             ))),
                        context
                       );

    }

    @Test
    public void test_replace(VertxTestContext context) {

        int keyValue = random.nextInt();

        JsObj filter = JsObj.of("key",
                                JsInt.of(keyValue)
                               );

        JsObj obj = filter.union(JsObj.of("string",
                                          JsStr.of("a"),
                                          "int",
                                          JsInt.of(Integer.MAX_VALUE),
                                          "long",
                                          JsLong.of(Long.MAX_VALUE),
                                          "boolean",
                                          TRUE,
                                          "double",
                                          JsDouble.of(1.5d),
                                          "decimal",
                                          JsBigDec.of(new BigDecimal("1.54456")),
                                          "array",
                                          JsArray.of(1,
                                                     2,
                                                     3
                                                    ),
                                          "null",
                                          NULL,
                                          "instant",
                                          JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                                          "biginteger",
                                          JsBigInt.of(new BigInteger("11111111111111111111111"))
                                         )
                                );

        JsObj newObj = filter.union(JsObj.of("string",
                                             JsStr.of("new"),
                                             "int",
                                             JsInt.of(Integer.MIN_VALUE),
                                             "long",
                                             JsLong.of(Long.MIN_VALUE),
                                             "boolean",
                                             FALSE,
                                             "double",
                                             JsDouble.of(10.5d),
                                             "decimal",
                                             JsBigDec.of(new BigDecimal("1.544456")),
                                             "array",
                                             JsArray.of(1,
                                                        2,
                                                        3,
                                                        4,
                                                        5
                                                       ),
                                             "null",
                                             NULL,
                                             "instant",
                                             JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                                             "biginteger",
                                             JsBigInt.of(new BigInteger("21111111111111111111111"))

                                            ));

        dataModule.insertOne
                .apply(obj)
                .flatMap(id -> dataModule.replaceOne.apply(new UpdateMessage(filter,
                                                                             newObj
                                                           )
                                                          )
                        )
                .flatMap(r -> dataModule.findOne.apply(FindMessage.ofFilter(filter)))
                .onComplete(
                        TestFns.pipeTo(result -> {
                                           assertEquals(Optional.of(newObj),
                                                        result.map(it -> it.delete("_id"))
                                                       );
                                       },
                                       context
                                      )
                           )
                .get();
    }

    @Test
    public void test_count(VertxTestContext context) {
        JsInt key = JsInt.of(random.nextInt());
        JsObj filter = JsObj.of("key",
                                key
                               );
        Verifiers.<Long>verifySuccess(count -> count == 2)
                .accept(dataModule.insertMany.apply(JsArray.of(JsObj.of("a",
                                                                        JsStr.of("a")
                                                                       )
                                                                    .union(filter),
                                                               JsObj.of("b",
                                                                        JsStr.of("b")
                                                                       )
                                                                    .union(filter)
                                                              )
                                                   )
                                             .flatMap(r -> dataModule.count.apply(filter)),
                        context
                       );
    }

    @Test
    public void test_update_many(VertxTestContext context) {
        JsInt key = JsInt.of(random.nextInt());

        JsObj filter = JsObj.of("key",
                                key
                               );

        JsObj projection = JsObj.of("_id",
                                    JsInt.of(0),
                                    "key",
                                    JsInt.of(0)
                                   );

        JsObj update = JsObj.of("$unset",
                                JsObj.of("a",
                                         JsStr.of("")
                                        )
                               );

        JsObj obj = JsObj.of("a",
                             JsInt.of(1),
                             "b",
                             JsInt.of(2)
                            );


        Verifiers.<JsArray>verifySuccess(array -> JsArray.of(JsObj.of("b",
                                                                      JsInt.of(2)
                                                                     ),
                                                             JsObj.of("b",
                                                                      JsInt.of(2)
                                                                     )
                                                            )
                                                         .equals(array)
                                        )
                .accept(dataModule.insertMany.apply(JsArray.of(obj.union(filter),
                                                               obj.union(filter)
                                                              ))
                                             .flatMap($ -> dataModule.updateMany.apply(new UpdateMessage(filter,
                                                                                                         update
                                                                                       )
                                                                                      )
                                                     )
                                             .flatMap(updateResult -> dataModule.findAll.apply(FindMessage.ofFilter(filter,
                                                                                                                    projection
                                                                                                                   ))),
                        context
                       );
    }

    @Test
    public void test_update_one(VertxTestContext context) {
        JsInt key = JsInt.of(random.nextInt());

        JsObj filter = JsObj.of("key",
                                key
                               );

        JsObj update = JsObj.of("$unset",
                                JsObj.of("a",
                                         JsStr.of("")
                                        )
                               );

        JsObj obj = JsObj.of("a",
                             JsInt.of(1),
                             "b",
                             JsInt.of(2)
                            );


        Verifiers.<Optional<JsObj>>verifySuccess(optObj -> optObj.isPresent() && !optObj.get()
                                                                                        .containsKey("a")
                                                )
                .accept(dataModule.insertOne.apply(obj.union(filter))
                                            .flatMap($ -> dataModule.updateOne
                                                             .apply(new UpdateMessage(filter,
                                                                                      update
                                                                    )
                                                                   )
                                                    )
                                            .flatMap(updateResult -> dataModule.findOne.apply(FindMessage.ofFilter(filter))),
                        context
                       );

    }

    @Test
    @Disabled //test only valid for replicaSet
    public void test_watcher(final VertxTestContext context,
                             final Vertx vertx) {
        VertxRef vertxRef = new VertxRef(vertx);

        AtomicInteger counter = new AtomicInteger();

        vertxRef.deployVerticle(new Watcher(dataModule.collectionSupplier,
                                            stream -> stream.forEach($ -> counter.addAndGet(1))
        ))
                .onSuccess(id ->
                                   Verifiers.<String>verifySuccess(it -> counter.get() == 1)
                                           .accept(dataModule.insertOne.apply(JsObj.of("a",
                                                                                       TRUE
                                                                                      )
                                                                             ),
                                                   context
                                                  ))
                .get();
    }


    @Test
    public void test_aggregate(VertxTestContext context) {
        JsInt key = JsInt.of(random.nextInt());

        JsObj filter = JsObj.of("key",
                                key
                               );
        JsArray array = JsArray.of(JsObj.of("cust_id",
                                            JsStr.of("A123"),
                                            "amount",
                                            JsInt.of(500),
                                            "status",
                                            JsStr.of("A")
                                           )
                                        .union(filter),
                                   JsObj.of("cust_id",
                                            JsStr.of("A123"),
                                            "amount",
                                            JsInt.of(250),
                                            "status",
                                            JsStr.of("A")
                                           )
                                        .union(filter),
                                   JsObj.of("cust_id",
                                            JsStr.of("B212"),
                                            "amount",
                                            JsInt.of(200),
                                            "status",
                                            JsStr.of("A")
                                           )
                                        .union(filter),
                                   JsObj.of("cust_id",
                                            JsStr.of("A123"),
                                            "amount",
                                            JsInt.of(300),
                                            "status",
                                            JsStr.of("D")
                                           )
                                        .union(filter)
                                  );

        JsArray pipeline = JsArray.of(JsObj.of("$match",
                                               JsObj.of("status",
                                                        JsStr.of("A")
                                                       )
                                                    .union(filter)
                                              ),
                                      JsObj.of("$group",
                                               JsObj.of("_id",
                                                        JsStr.of("$cust_id"),
                                                        "total",
                                                        JsObj.of("$sum",
                                                                 JsStr.of("$amount")
                                                                )
                                                       )
                                              )
                                     );
        JsArray aggregationExpectedResult = JsArray.of(JsObj.of("_id",
                                                                JsStr.of("B212")
                ,
                                                                "total",
                                                                JsInt.of(200)
                                                               ),
                                                       JsObj.of("_id",
                                                                JsStr.of("A123")
                                                               ,
                                                                "total",
                                                                JsInt.of(750)
                                                               )
                                                      );
        Verifiers.<JsArray>verifySuccess(aggregationExpectedResult::equals
                                        ).accept(dataModule.insertMany.apply(array)
                                                                      .flatMap(ids -> dataModule.aggregate.apply(pipeline)),
                                                 context
                                                );
    }

    @Test
    public void test_delete_all(VertxTestContext context) {

        int key = random.nextInt();
        JsObj filter = JsObj.of("key",
                                JsInt.of(key)
                               );
        Val<JsArray> val = dataModule.insertMany
                .apply(JsArray.of(JsObj.of("name",
                                           JsStr.of("Rafa"),
                                           "age",
                                           JsInt.of(38)
                                          )
                                       .union(filter),
                                  JsObj.of("name",
                                           JsStr.of("Alberto"),
                                           "age",
                                           JsInt.of(10)
                                          )
                                       .union(filter),
                                  JsObj.of("name",
                                           JsStr.of("Josefa"),
                                           "age",
                                           JsInt.of(49)
                                          )
                                       .union(filter)
                                 )
                      )
                .flatMap(ids -> dataModule.deleteMany.apply(filter))
                .flatMap(r -> dataModule.findAll.apply(FindMessage.ofFilter(filter)));


        Verifiers.verifySuccess(JsArray::isEmpty)
                 .accept(val,
                         context
                        );


    }


    @Test
    public void test_find_one_update(VertxTestContext context) {


        JsInt key = JsInt.of(random.nextInt());

        JsObj filter = JsObj.of("key",
                                key
                               );


        JsObj update = JsObj.of("$unset",
                                JsObj.of("a",
                                         JsStr.of("")
                                        )
                               );

        JsObj obj = JsObj.of("a",
                             JsInt.of(1),
                             "b",
                             JsInt.of(2)
                            );

        Verifiers.<Optional<JsObj>>verifySuccess(o -> Objects.equals(JsObj.of("b",
                                                                              JsInt.of(2)
                                                                             )
                                                                          .union(filter),
                                                                     o.get()
                                                                      .delete("_id")
                                                                    ))
                .accept(dataModule.insertOne.apply(obj.union(filter))
                                            .flatMap($ -> dataModule.findOneAndUpdate.apply(new UpdateMessage(filter,
                                                                                                              update
                                                                                            )
                                                                                           )
                                                    )
                                            .flatMap(r -> dataModule.findOne.apply(FindMessage.ofFilter(filter))),
                        context
                       );

    }

    @Test
    public void test_find_one_delete(VertxTestContext context) {

        JsInt key = JsInt.of(random.nextInt());

        JsObj filter = JsObj.of("key",
                                key
                               );


        JsObj obj = JsObj.of("a",
                             JsInt.of(1),
                             "b",
                             JsInt.of(2)
                            );

        Verifiers.<Optional<JsObj>>verifySuccess(Optional::isEmpty)
                .accept(dataModule.insertOne.apply(obj.union(filter))
                                            .flatMap($ -> dataModule.findOneAndDelete
                                                             .apply(filter)
                                                    )
                                            .flatMap(r -> dataModule.findOne.apply(FindMessage.ofFilter(filter))),
                        context
                       );

    }

}
