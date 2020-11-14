package vertx.mongodb.effect.functions;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.InsertOneResult;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.Failures;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class InsertOne<R> implements λc<JsObj, R> {


    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final InsertOneOptions options;
    private final Function<InsertOneResult, R> resultConverter;
    private static final InsertOneOptions DEFAULT_OPTIONS = new InsertOneOptions();


    public InsertOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<InsertOneResult, R> resultConverter) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }

    public InsertOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<InsertOneResult, R> resultConverter,
                     final InsertOneOptions options
                    ) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
        this.resultConverter = requireNonNull(resultConverter);
    }

    @Override
    public Val<R> apply(final MultiMap context,
                        final JsObj message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());

            return Cons.success(resultConverter.apply(collection
                                                              .insertOne(message,
                                                                         options
                                                                        )
                                                     ));
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));

        }

    }
}
