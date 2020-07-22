package mongo.actors;


import actors.codecs.RegisterJsValuesCodecs;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jsonvalues.*;
import jsonvalues.mongo.JsValuesRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.function.Supplier;

@ExtendWith(VertxExtension.class)
public class TestMongoOps {

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
        MongoDriver mongoClient = new MongoDriver(settings);

        Supplier<MongoCollection<JsObj>> dataCollection = mongoClient.collection("test",
                                                                                 "Data"
                                                                                );

        dataModule = new DataCollectionModule(dataCollection);


        CompositeFuture.all(vertx.deployVerticle(mongoClient),
                            vertx.deployVerticle(new RegisterMongoCodecs()),
                            vertx.deployVerticle(new RegisterJsValuesCodecs()),
                            vertx.deployVerticle(dataModule)
                           )
                       .onComplete(it -> {
                           if (it.succeeded())
                               testContext.completeNow();
                           else {
                               it.cause()
                                 .printStackTrace();
                               testContext.failNow(it.cause());
                           }
                       });
    }

    @Test
    public void testA(Vertx vertx,
                      VertxTestContext context) {

        JsObj obj = JsObj.of("string",
                             JsStr.of("a"),
                             "int",
                             JsInt.of(Integer.MAX_VALUE),
                             "long",
                             JsLong.of(Long.MAX_VALUE),
                             "boolean",
                             JsBool.TRUE,
                             "double",
                             JsDouble.of(1.5d),
                             "decimal",
                             JsBigDec.of(new BigDecimal("1.54456")),
                             "array",
                             JsArray.of(1,2,3),
                             "null",
                             JsNull.NULL,
                             "instant",
                             JsInstant.of(Instant.now()),
                             "biginteger",
                             JsBigInt.of(new BigInteger("11111111111111111111111"))
                            );
        System.out.println(obj.toString());
        dataModule.insertOne.apply(obj)
                            .compose(id -> dataModule.findOne
                                    .apply(new FindMessageBuilder().filter(Converters.toOid.apply(id))
                                                                   .create()
                                          )
                                    )
                            .setHandler(findRes -> {
                                if (findRes.succeeded()) {
                                    context.verify(()-> {
                                        Assertions.assertEquals(obj,
                                                                findRes.result()
                                                                       .get()
                                                                       .delete("_id")
                                                               );
                                        context.completeNow();
                                    });
                                }
                                else {
                                    context.failNow(findRes.cause());
                                }
                            });


    }



}
