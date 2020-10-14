package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import jsonvalues.JsObj;
import mongoval.MongoValException;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


public class FindOneAndDelete implements λ<JsObj, JsObj> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final FindOneAndDeleteOptions options;
    private ClientSession session;
    private static final FindOneAndDeleteOptions DEFAULT_OPTIONS = new FindOneAndDeleteOptions();


    public FindOneAndDelete(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
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
    public Val<JsObj> apply(final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));

        try {
            var collection = this.collectionSupplier.get();
            return Cons.success(session != null ?
                                collection.findOneAndDelete(session,
                                                            jsObj2Bson.apply(query),
                                                            options
                                                           ) :
                                collection
                                        .findOneAndDelete(jsObj2Bson.apply(query),
                                                          options
                                                         ));
        } catch (Exception exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));

        }
    }
}
