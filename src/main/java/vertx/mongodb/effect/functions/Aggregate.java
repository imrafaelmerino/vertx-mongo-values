package vertx.mongodb.effect.functions;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class Aggregate<O> implements λc<JsArray, O> {

    public final Function<AggregateIterable<JsObj>, O> resultConverter;
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;

    public Aggregate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<AggregateIterable<JsObj>, O> resultConverter) {
        this.resultConverter = resultConverter;
        this.collectionSupplier = collectionSupplier;
    }

    @Override
    public Val<O> apply(final MultiMap context,
                        final JsArray m) {
        return Cons.of(() -> {
            try {
                var pipeline = Converters.jsArray2ListOfBson.apply(m);
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection.aggregate(pipeline)));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });

    }
}
