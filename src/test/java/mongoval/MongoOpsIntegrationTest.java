package mongoval;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jsonvalues.*;
import mongoval.codecs.RegisterMongoValuesCodecs;
import mongovalues.JsValuesRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertxval.codecs.RegisterJsValuesCodecs;
import vertxval.exp.Val;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Random;

import static jsonvalues.JsBool.FALSE;
import static jsonvalues.JsBool.TRUE;
import static jsonvalues.JsNull.NULL;
import static mongoval.Converters.str2Oid;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class MongoOpsIntegrationTest {

    public static Random random = new Random();

    public static DataCollectionModule dataModule;

    static MongoClientSettings settings;

    static {

        ConnectionString connString = new ConnectionString(
                "mongodb://localhost:27017"
        );
        settings = MongoClientSettings.builder()
                                      .applyConnectionString(connString)
                                      .retryWrites(true)
                                      .codecRegistry(JsValuesRegistry.INSTANCE)
                                      .build();

    }


    @BeforeAll
    public static void prepare(Vertx vertx,
                               VertxTestContext testContext
                              ) {
        MongoVertxClient mongoClient = new MongoVertxClient(settings);

        dataModule = new DataCollectionModule(mongoClient.collection("test",
                                                                     "Data"
                                                                    )
        );

        CompositeFuture.all(vertx.deployVerticle(mongoClient),
                            vertx.deployVerticle(new RegisterMongoValuesCodecs()),
                            vertx.deployVerticle(new RegisterJsValuesCodecs()),
                            vertx.deployVerticle(dataModule)
                           )
                       .onComplete(TestFns.pipeTo(testContext));
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

        Val<JsArray> val = dataModule.insertAll
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
                .flatMap(ids -> dataModule
                                 .findAll
                                 .apply(FindMessage.ofFilter(JsObj.of("test",
                                                                      JsInt.of(key)
                                                                     )
                                                            )
                                       )
                        );


        Verifiers.<JsArray>verifySuccess(it -> {
            System.out.println(it);
            return it.size() == 3;
        }).accept(val,
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
                            .onSuccess(optResult -> {
                                context.verify(() -> Assertions.assertTrue(optResult.isEmpty()));
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
                .flatMap(id -> {
                             System.out.println("id " + id);
                             return dataModule.findOneAndReplace.apply(new UpdateMessage(filter,
                                                                                         newObj
                                                                       )
                                                                      );
                         }
                        )
                .flatMap(r -> {
                    System.out.println("findAndOneReplace result " + r);
                    return dataModule.findOne.apply(FindMessage.ofFilter(filter));
                })
                .onComplete(
                        TestFns.pipeTo(result -> {
                                           System.out.println("findOne result "+result);
                                           assertEquals(Optional.of(newObj),
                                                        result.map(it -> it.delete("_id"))
                                                       );
                                       },
                                       context
                                      )
                           )
                .get();
    }


}
