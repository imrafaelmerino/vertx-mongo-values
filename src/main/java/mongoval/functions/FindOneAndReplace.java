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


public class FindOneAndReplace implements Function<UpdateMessage, JsObj> {

    private final FindOneAndReplaceOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private ClientSession session;
    private static final FindOneAndReplaceOptions DEFAULT_OPTIONS = new FindOneAndReplaceOptions();


    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                             final FindOneAndReplaceOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }
    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier, DEFAULT_OPTIONS);
    }
    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                             final FindOneAndReplaceOptions options,
                             final ClientSession session) {
        this(collectionSupplier,
             options
            );
        this.session = requireNonNull(session);
    }


    @Override
    public JsObj apply(final UpdateMessage m) {
        MongoCollection<JsObj> collection = requireNonNull(this.collectionSupplier.get());
        return session != null ?
               collection
                       .findOneAndReplace(session,
                                          Converters.jsObj2Bson.apply(m.filter),
                                          m.update,
                                          options
                                         ) :
               collection
                       .findOneAndReplace(Converters.jsObj2Bson.apply(m.filter),
                                          m.update,
                                          options
                                         );
    }
}
