package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.Failures;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class Count implements λ<JsObj, Long> {

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
    public Val<Long> apply(final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));
        try {
            var queryBson  = Converters.jsObj2Bson.apply(requireNonNull(query));
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(collection.countDocuments(queryBson,
                                                          options
                                                         ));
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));
        }


    }
}