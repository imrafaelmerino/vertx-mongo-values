package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;

import vertx.effect.λc;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.Converters.jsObj2Bson;


public class DeleteOne<O> implements λc<JsObj, O> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<DeleteResult, O> resultConverter;
    private final DeleteOptions options;
    private static final DeleteOptions DEFAULT_OPTIONS = new DeleteOptions();


    public DeleteOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<DeleteResult, O> resultConverter,
                     final DeleteOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }

    public DeleteOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<DeleteResult, O> resultConverter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = DEFAULT_OPTIONS;
    }

    @Override
    public Val<O> apply(final MultiMap context,
                        final JsObj query) {
        if (query == null) return Val.fail(new IllegalArgumentException("query is null"));

        return Val.effect(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection.deleteOne(jsObj2Bson.apply(requireNonNull(query)),
                                                                                         options
                                                                    )
                                              )
                );

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });
    }
}
