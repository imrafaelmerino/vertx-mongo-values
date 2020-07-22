package mongo.actors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.*;
import mongovalues.JsValuesRegistry;
import jsonvalues.spec.JsErrorPair;
import jsonvalues.spec.JsSpecs;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
public class Converters {


    public static Function<JsObj, Bson> objVal2Bson = obj ->
            new BsonDocumentWrapper<>(obj,
                                      JsValuesRegistry.INSTANCE.get(JsObj.class)
            );

    public static Function<JsArray, List<JsObj>> arrayVal2ListOfObjVal =
            array -> {
                Set<JsErrorPair> errors = JsSpecs.arrayOfObj.test(array);
                if (!errors.isEmpty()) throw new IllegalArgumentException(errors.toString());

                List<JsObj> list = new ArrayList<>();
                array.iterator()
                     .forEachRemaining(it -> list.add(it.toJsObj()));
                return list;
            };


    public static Function<JsArray, List<Bson>> arrayVal2ListOfBsonVal =
            arrayVal2ListOfObjVal.andThen(list -> list.stream()
                                                      .map(it -> objVal2Bson.apply(it.toJsObj()))
                                                      .collect(Collectors.toList()));


    public static Function<BsonValue, String> bsonValue2HexId =
            bsonValue -> bsonValue.asObjectId()
                                  .getValue()
                                  .toHexString();

    public static Function<InsertOneResult, String> insertOneResult2HexId =
            result -> bsonValue2HexId.apply(result.getInsertedId());

    public static Function<InsertOneResult, JsObj> insertOneResult2ObjVal =
            result -> JsObj.of("insertedId",
                               JsStr.of(insertOneResult2HexId.apply(result)),
                               "wasAcknowledged",
                               JsBool.of(result.wasAcknowledged()),
                               "type",
                               JsStr.of(result.getClass()
                                              .getSimpleName()
                                       )
                              );

    public static Function<UpdateResult, Optional<String>> updateResult2Hex = it -> {
        BsonValue upsertedId = it.getUpsertedId();
        if (upsertedId == null) return Optional.empty();
        return Optional.of(bsonValue2HexId.apply(upsertedId));
    };

    public static Function<UpdateResult, JsObj> updateResult2ObjVal = result -> {
        Optional<String> optStr = updateResult2Hex.apply(result);
        return JsObj.of("upsertedId",
                        optStr.isPresent() ? JsStr.of(optStr.get()) : JsNull.NULL,
                        "matchedCount",
                        JsLong.of(result.getMatchedCount()),
                        "modifiedCount",
                        JsLong.of(result.getModifiedCount()),
                        "wasAcknowledged",
                        JsBool.of(result.wasAcknowledged()),
                        "type",
                        JsStr.of(result.getClass()
                                       .getSimpleName()
                                )
                       );
    };

    public static Function<FindIterable<JsObj>, JsObj> getFirst = MongoIterable::first;

    public static Function<FindIterable<JsObj>, JsArray> getArray = JsArray::ofIterable;

    public static Function<String,JsObj> toOid = id -> JsObj.of("_id",JsObj.of("$oid",
                                                                               JsStr.of(id)));
}
