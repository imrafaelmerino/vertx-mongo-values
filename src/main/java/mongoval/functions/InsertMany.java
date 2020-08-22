package mongoval.functions;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.InsertManyResult;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import mongoval.Converters;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class InsertMany<R> implements Function<JsArray, R> {


    private final Supplier<MongoCollection<JsObj>> collection;
    private final InsertManyOptions options;
    private final Function<InsertManyResult, R> resultConverter;
    private ClientSession session;

    public InsertMany(final Supplier<MongoCollection<JsObj>> collection,
                      final InsertManyOptions options,
                      final Function<InsertManyResult, R> resultConverter,
                      final ClientSession session) {
        this(collection,
             options,
             resultConverter
            );
        this.session = session;
    }

    public InsertMany(final Supplier<MongoCollection<JsObj>> collection,
                      final InsertManyOptions options,
                      final Function<InsertManyResult, R> resultConverter) {
        this.collection = requireNonNull(collection);
        this.options = requireNonNull(options);
        this.resultConverter = requireNonNull(resultConverter);
    }

    @Override
    public R apply(final JsArray message) {
        List<JsObj>            docs       = Converters.arrayVal2ListOfObjVal.apply(message);
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

        return session != null ?
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
                                    );
    }
}
