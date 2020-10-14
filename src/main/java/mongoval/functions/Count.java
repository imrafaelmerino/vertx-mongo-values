package mongoval.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import jsonvalues.JsObj;
import mongoval.MongoValException;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


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
            var queryBson  = jsObj2Bson.apply(requireNonNull(query));
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(collection.countDocuments(queryBson,
                                                          options
                                                         ));
        } catch (Exception exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));
        }


    }
}
