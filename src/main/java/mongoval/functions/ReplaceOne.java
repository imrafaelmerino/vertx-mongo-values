package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.JsObj;
import mongoval.MongoValException;
import mongoval.UpdateMessage;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


public class ReplaceOne<O> implements λ<UpdateMessage, O> {

    private final Function<UpdateResult, O> resultConverter;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final ReplaceOptions options;
    private ClientSession session;
    public static final ReplaceOptions DEFAULT_OPTIONS = new ReplaceOptions();


    public ReplaceOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final ReplaceOptions options,
                      final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = requireNonNull(session);
    }


    public ReplaceOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final ReplaceOptions options) {
        this.resultConverter = requireNonNull(resultConverter);
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    public ReplaceOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter
                     ) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }


    @Override
    public Val<O> apply(final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(session != null ?
                                resultConverter.apply(
                                        collection.replaceOne(session,
                                                              jsObj2Bson.apply(message.filter),
                                                              message.update,
                                                              options
                                                             )
                                                     ) :
                                resultConverter.apply(
                                        collection.replaceOne(jsObj2Bson.apply(message.filter),
                                                              message.update,
                                                              options
                                                             )
                                                     ));
        } catch (Exception exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));

        }
    }
}
