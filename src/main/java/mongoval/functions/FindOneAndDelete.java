package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import jsonvalues.JsObj;
import mongoval.Converters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class FindOneAndDelete implements Function<JsObj, JsObj> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final FindOneAndDeleteOptions options;
    private ClientSession session;
    private static final FindOneAndDeleteOptions DEFAULT_OPTIONS = new FindOneAndDeleteOptions();


    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier, DEFAULT_OPTIONS);
    }
    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndDeleteOptions options) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndDeleteOptions options,
                            final ClientSession session) {
        this(collectionSupplier,
             options
            );
        this.session = requireNonNull(session);
    }

    @Override
    public JsObj apply(final JsObj doc) {
        MongoCollection<JsObj> collection = this.collectionSupplier.get();
        return session != null ?
               collection.findOneAndDelete(session,
                                           Converters.jsObj2Bson.apply(doc),
                                           options
                                          ) :
               collection
                       .findOneAndDelete(Converters.jsObj2Bson.apply(doc),
                                         options
                                        );
    }
}
