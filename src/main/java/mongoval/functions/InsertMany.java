package mongoval.functions;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.InsertManyResult;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.MongoValException;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsArray2ListOfJsObj;

public class InsertMany<R> implements λ<JsArray, R> {


    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final InsertManyOptions options;
    private final Function<InsertManyResult, R> resultConverter;
    private ClientSession session;

    public InsertMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final InsertManyOptions options,
                      final Function<InsertManyResult, R> resultConverter,
                      final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = session;
    }

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
    public Val<R> apply(final JsArray message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var docs       = jsArray2ListOfJsObj.apply(message);
            var collection = requireNonNull(collectionSupplier.get());

            return Cons.success(session != null ?
                                resultConverter.apply(collection
                                                              .insertMany(session,
                                                                          docs,
                                                                          options
                                                                         )
                                                     ) :
                                resultConverter.apply(collection
                                                              .insertMany(docs,
                                                                          options
                                                                         )
                                                     ));
        } catch (Throwable exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));

        }
    }
}
