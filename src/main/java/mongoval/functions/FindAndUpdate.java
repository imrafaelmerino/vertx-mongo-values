package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class FindAndUpdate implements Function<UpdateMessage, JsObj> {

    public final FindOneAndUpdateOptions options;
    public final Supplier<MongoCollection<JsObj>> collection;
    private ClientSession session;


    public FindAndUpdate(final Supplier<MongoCollection<JsObj>> collection,
                         final FindOneAndUpdateOptions options) {
        this.collection = requireNonNull(collection);
        this.options = options;
    }

    public FindAndUpdate(final FindOneAndUpdateOptions options,
                         final Supplier<MongoCollection<JsObj>> collection,
                         final ClientSession session) {
        this(collection,
             options
            );
        this.session = session;
    }

    @Override
    public JsObj apply(final UpdateMessage m) {
        MongoCollection<JsObj> collection = this.collection.get();
        return session != null ?
               collection
                       .findOneAndUpdate(session,
                                         Converters.objVal2Bson.apply(m.filter),
                                         Converters.objVal2Bson.apply(m.update),
                                         options
                                        ) :
               collection
                       .findOneAndUpdate(Converters.objVal2Bson.apply(m.filter),
                                         Converters.objVal2Bson.apply(m.update),
                                         options
                                        );
    }
}
