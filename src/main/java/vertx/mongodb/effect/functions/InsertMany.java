package vertx.mongodb.effect.functions;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.InsertManyResult;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class InsertMany<R> implements λc<JsArray, R> {


    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final InsertManyOptions options;
    private final Function<InsertManyResult, R> resultConverter;


    public InsertMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<InsertManyResult, R> resultConverter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = new InsertManyOptions();
        this.resultConverter = requireNonNull(resultConverter);
    }

    public InsertMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<InsertManyResult, R> resultConverter,
                      final InsertManyOptions options
    ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
        this.resultConverter = requireNonNull(resultConverter);
    }


    @Override
    public Val<R> apply(final MultiMap context,
                        final JsArray message) {
        if (message == null) return Val.fail(new IllegalArgumentException("message is null"));

        return Val.effect(() -> {
            try {
                var docs = Converters.jsArray2ListOfJsObj.apply(message);
                var collection = requireNonNull(collectionSupplier.get());

                return Future.succeededFuture(resultConverter.apply(collection
                                                                            .insertMany(docs,
                                                                                        options
                                                                            ))
                );
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));

            }

        });
    }
}