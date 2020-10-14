package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.MongoValException;
import mongoval.UpdateMessage;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class FindOneAndReplace implements λ<UpdateMessage, JsObj> {

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
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
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
    public Val<JsObj> apply(final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(session != null ?
                                collection
                                        .findOneAndReplace(session,
                                                           Converters.jsObj2Bson.apply(message.filter),
                                                           message.update,
                                                           options
                                                          ) :
                                collection
                                        .findOneAndReplace(Converters.jsObj2Bson.apply(message.filter),
                                                           message.update,
                                                           options
                                                          )
                               );
        } catch (Exception exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));

        }
    }
}
