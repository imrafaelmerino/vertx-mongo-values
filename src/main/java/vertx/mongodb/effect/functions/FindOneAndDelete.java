package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;

import vertx.effect.λc;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.Converters.jsObj2Bson;


public class FindOneAndDelete implements λc<JsObj, JsObj> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final FindOneAndDeleteOptions options;
    private static final FindOneAndDeleteOptions DEFAULT_OPTIONS = new FindOneAndDeleteOptions();

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
        );
    }

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndDeleteOptions options) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }


    @Override
    public Val<JsObj> apply(final MultiMap context,
                            final JsObj query) {
        if (query == null) return Val.fail(new IllegalArgumentException("query is null"));
        return Val.effect(() -> {
            try {
                var collection = this.collectionSupplier.get();
                return Future.succeededFuture(collection.findOneAndDelete(jsObj2Bson.apply(query),
                                                                          options
                ));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));

            }
        });

    }
}
