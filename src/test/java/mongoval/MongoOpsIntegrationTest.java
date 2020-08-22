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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertxval.codecs.RegisterJsValuesCodecs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static mongoval.Converters.str2Oid;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class MongoOpsIntegrationTest {

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
        MongoClient mongoClient = new MongoClient(settings);

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
    public void testA(VertxTestContext context) {

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
                             JsArray.of(1,
                                        2,
                                        3
                                       ),
                             "null",
                             JsNull.NULL,
                             "instant",
                             JsInstant.of(Instant.now(Clock.tickMillis(ZoneId.of("UTC")))),
                             "biginteger",
                             JsBigInt.of(new BigInteger("11111111111111111111111"))
                            );
        System.out.println(obj.toString());

        dataModule.insertOne.apply(obj)
                            .flatMap(id -> dataModule.findOne.apply(FindMessage.filter(str2Oid.apply(id)))
                                    )
                            .onComplete(
                                    TestFns.pipeTo(result -> assertEquals(Optional.of(obj),
                                                                          result.map(it -> it.delete("_id"))
                                                                         ),
                                                   context
                                                  )
                                       )
                            .get();


    }


}
