package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;

import java.util.function.Supplier;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.requireNonNull;


public class Count implements λc<JsObj, Long> {

    private final CountOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private static final CountOptions DEFAULT_OPTIONS = new CountOptions();

    public Count(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                 final CountOptions options
    ) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    public Count(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this.options = DEFAULT_OPTIONS;
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    @Override
    public Val<Long> apply(final MultiMap context,
                           final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));


        return Cons.of(() -> {
            try {
                var queryBson = Converters.jsObj2Bson.apply(requireNonNull(query));
                var collection = requireNonNull(this.collectionSupplier.get());
                return succeededFuture(collection.countDocuments(queryBson,
                                                                 options)
                );
            } catch (Exception exc) {
                return failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });


    }
}
