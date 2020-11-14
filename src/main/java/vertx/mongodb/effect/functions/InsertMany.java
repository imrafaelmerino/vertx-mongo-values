package vertx.mongodb.effect.functions;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.InsertManyResult;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.Failures;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

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
    public Val<R> apply(final MultiMap context,final JsArray message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var docs       = Converters.jsArray2ListOfJsObj.apply(message);
            var collection = requireNonNull(collectionSupplier.get());

            return Cons.success(resultConverter.apply(collection
                                                              .insertMany(docs,
                                                                          options
                                                                         )
                                                     ));
        } catch (Throwable exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));

        }
    }
}
