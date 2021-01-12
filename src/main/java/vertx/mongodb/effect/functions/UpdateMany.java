package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.Converters.jsObj2Bson;


public class UpdateMany<O> implements λc<UpdateMessage, O> {

    private final UpdateOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<UpdateResult, O> resultConverter;
    private static final UpdateOptions DEFAULT_OPTIONS = new UpdateOptions();


    public UpdateMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter
    ) {
        this.options = DEFAULT_OPTIONS;
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
    }

    public UpdateMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final UpdateOptions options
    ) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
    }


    @Override
    public Val<O> apply(final MultiMap context,
                        final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        return Cons.of(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(resultConverter.apply(collection.updateMany(jsObj2Bson.apply(message.filter),
                                                                                          jsObj2Bson.apply(message.update),
                                                                                          options
                                                                    )
                ));

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));

            }
        });
    }
}
