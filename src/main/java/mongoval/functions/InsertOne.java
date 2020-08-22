package mongoval.functions;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.InsertOneResult;
import jsonvalues.JsObj;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class InsertOne<R> implements Function<JsObj, R> {


    private final Supplier<MongoCollection<JsObj>> collection;
    private final InsertOneOptions options;
    private final Function<InsertOneResult, R> resultConverter;
    private ClientSession session;

    public InsertOne(final Supplier<MongoCollection<JsObj>> collection,
                     final Function<InsertOneResult, R> resultConverter) {
        this(collection,
             new InsertOneOptions(),
             resultConverter
            );
    }

    public InsertOne(final Supplier<MongoCollection<JsObj>> collection,
                     final InsertOneOptions options,
                     final Function<InsertOneResult, R> resultConverter) {
        this.collection = requireNonNull(collection);
        this.options = requireNonNull(options);
        this.resultConverter = requireNonNull(resultConverter);
    }

    public InsertOne(final Supplier<MongoCollection<JsObj>> collection,
                     final InsertOneOptions options,
                     final Function<InsertOneResult, R> resultConverter,
                     final ClientSession session) {
        this(collection,
             options,
             resultConverter
            );
        this.session = requireNonNull(session);
    }

    public InsertOne(final Supplier<MongoCollection<JsObj>> collection,
                     final Function<InsertOneResult, R> resultConverter,
                     final ClientSession session) {
        this(collection,
             new InsertOneOptions(),
             resultConverter
            );
        this.session = requireNonNull(session);
    }

    @Override
    public R apply(final JsObj message) {
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

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
