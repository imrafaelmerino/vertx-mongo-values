package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class FindOneAndDelete implements Function<JsObj, JsObj> {

    public final Supplier<MongoCollection<JsObj>> collection;
    private final FindOneAndDeleteOptions options;
    private ClientSession session;

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collection,
                            final FindOneAndDeleteOptions options) {
        this.options = options;
        this.collection = requireNonNull(collection);
    }

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collection,
                            final FindOneAndDeleteOptions options,
                            final ClientSession session) {
        this(collection,
             options
            );
        this.session = session;
    }

    @Override
    public JsObj apply(final JsObj o) {
        MongoCollection<JsObj> collection = this.collection.get();
        return session != null ?
               collection.findOneAndDelete(session,
                                           Converters.objVal2Bson.apply(o),
                                           options
                                          ) :
               collection
                       .findOneAndDelete(Converters.objVal2Bson.apply(o),
                                         options
                                        );
    }
}
