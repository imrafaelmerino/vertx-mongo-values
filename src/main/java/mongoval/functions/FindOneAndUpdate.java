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
import static java.util.Objects.requireNonNull;


public class FindOneAndUpdate implements Function<UpdateMessage, JsObj> {

    private final FindOneAndUpdateOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private ClientSession session;
    private static final FindOneAndUpdateOptions DEFAULT_OPTIONS = new FindOneAndUpdateOptions();


    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
    }

    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndUpdateOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndUpdateOptions options,
                            final ClientSession session) {
        this(collectionSupplier,
             options
            );
        this.session = requireNonNull(session);
    }

    @Override
    public JsObj apply(final UpdateMessage message) {
        MongoCollection<JsObj> collection = this.collectionSupplier.get();
        requireNonNull(message);

        return session != null ?
               collection
                       .findOneAndUpdate(session,
                                         Converters.jsObj2Bson.apply(message.filter),
                                         Converters.jsObj2Bson.apply(message.update),
                                         options
                                        ) :
               collection
                       .findOneAndUpdate(Converters.jsObj2Bson.apply(message.filter),
                                         Converters.jsObj2Bson.apply(message.update),
                                         options
                                        );
    }
}
