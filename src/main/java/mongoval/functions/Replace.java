package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.JsObj;
import mongoval.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.objVal2Bson;


public class Replace<O> implements Function<UpdateMessage, O> {

    public final Function<UpdateResult, O> resultConverter;
    public final Supplier<MongoCollection<JsObj>> collection;
    public final ReplaceOptions options;
    private ClientSession session;

    public Replace(final Function<UpdateResult, O> resultConverter,
                   final Supplier<MongoCollection<JsObj>> collection,
                   final ReplaceOptions options,
                   final ClientSession session) {
        this(resultConverter,
             collection,
             options
            );
        this.session = requireNonNull(session);
    }

    public Replace(final Function<UpdateResult, O> resultConverter,
                   final Supplier<MongoCollection<JsObj>> collection,
                   final ClientSession session) {
        this(resultConverter,
             collection,
             new ReplaceOptions()
            );
        this.session = requireNonNull(session);
    }

    public Replace(final Function<UpdateResult, O> resultConverter,
                   final Supplier<MongoCollection<JsObj>> collection,
                   final ReplaceOptions options) {
        this.resultConverter = requireNonNull(resultConverter);
        this.collection = requireNonNull(collection);
        this.options = requireNonNull(options);
    }

    public Replace(final Function<UpdateResult, O> resultConverter,
                   final Supplier<MongoCollection<JsObj>> collection) {
        this(resultConverter,
             collection,
             new ReplaceOptions()
            );
    }


    @Override
    public O apply(final UpdateMessage message) {
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return session != null ?
               resultConverter.apply(
                       collection.replaceOne(session,
                                             objVal2Bson.apply(message.filter),
                                             message.update,
                                             options
                                            )
                                    ) :
               resultConverter.apply(
                       collection.replaceOne(objVal2Bson.apply(message.filter),
                                             message.update,
                                             options
                                            )
                                    );
    }
}
