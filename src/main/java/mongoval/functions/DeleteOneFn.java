package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import jsonvalues.JsObj;
import mongoval.Converters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class DeleteOneFn<O> implements Function<JsObj, O> {

    public final Supplier<MongoCollection<JsObj>> collection;
    public final Function<DeleteResult, O> resultConverter;
    public final DeleteOptions options;
    private ClientSession session;


    public DeleteOneFn(final Supplier<MongoCollection<JsObj>> collection,
                       final Function<DeleteResult, O> resultConverter,
                       final DeleteOptions options) {
        this.collection = requireNonNull(collection);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }

    public DeleteOneFn(final Supplier<MongoCollection<JsObj>> collection,
                       final Function<DeleteResult, O> resultConverter,
                       final DeleteOptions options,
                       final ClientSession session) {
        this(collection,
             resultConverter,
             options);
        this.session = requireNonNull(session);
    }

    @Override
    public O apply(final JsObj filter) {
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return resultConverter.apply(session
                                             != null ? collection.deleteOne(session,
                                                                            Converters.objVal2Bson.apply(filter),
                                                                            options
                                                                           ) :
                                     collection.deleteOne(Converters.objVal2Bson.apply(filter),
                                                          options
                                                         )
                                    );
    }
}
