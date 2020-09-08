package mongoval.functions;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.InsertOneResult;
import jsonvalues.JsObj;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNull;

public class InsertOne<R> implements Function<JsObj, R> {


    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final InsertOneOptions options;
    private final Function<InsertOneResult, R> resultConverter;
    private ClientSession session;
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

    public InsertOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final InsertOneOptions options,
                     final Function<InsertOneResult, R> resultConverter,
                     final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = requireNonNull(session);
    }


    @Override
    public R apply(final JsObj message) {
        requireNonNull(message);
        MongoCollection<JsObj> collection = requireNonNull(this.collectionSupplier.get());

        return session != null ?
               resultConverter.apply(collection
                                             .insertOne(session,
                                                        message,
                                                        options
                                                       )
                                    ) :
               resultConverter.apply(collection
                                             .insertOne(message,
                                                        options
                                                       )
                                    );

    }
}
