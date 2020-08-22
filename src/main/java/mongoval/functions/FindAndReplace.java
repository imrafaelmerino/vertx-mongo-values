package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class FindAndReplace implements Function<UpdateMessage, JsObj> {

    public final FindOneAndReplaceOptions options;
    public final Supplier<MongoCollection<JsObj>> collection;
    public ClientSession session;

    public FindAndReplace(final Supplier<MongoCollection<JsObj>> collection,
                          final FindOneAndReplaceOptions options) {
        this.collection = requireNonNull(collection);
        this.options = requireNonNull(options);
    }

    public FindAndReplace(final FindOneAndReplaceOptions options,
                          final Supplier<MongoCollection<JsObj>> collection,
                          final ClientSession session) {
        this(collection,
             options
            );
        this.session = session;
    }


    @Override
    public JsObj apply(final UpdateMessage m) {
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return session != null ?
               collection
                       .findOneAndReplace(session,
                                          Converters.objVal2Bson.apply(m.filter),
                                          m.update,
                                          options
                                         ) :
               collection
                       .findOneAndReplace(Converters.objVal2Bson.apply(m.filter),
                                          m.update,
                                          options
                                         );
    }
}
