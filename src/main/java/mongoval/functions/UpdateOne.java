package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.JsObj;
import mongoval.MongoValException;
import mongoval.UpdateMessage;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


public class UpdateOne<O> implements λ<UpdateMessage, O> {

    public final Supplier<MongoCollection<JsObj>> collectionSupplier;
    public final Function<UpdateResult, O> resultConverter;
    public final UpdateOptions options;
    private ClientSession session;
    private static final UpdateOptions DEFAULT_OPTIONS = new UpdateOptions();

    public UpdateOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<UpdateResult, O> resultConverter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = DEFAULT_OPTIONS;
    }

    public UpdateOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<UpdateResult, O> resultConverter,
                     final UpdateOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }


    public UpdateOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<UpdateResult, O> resultConverter,
                     final UpdateOptions options,
                     final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = Objects.requireNonNull(session);
    }

    @Override
    public Val<O> apply(final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(session != null ?
                                resultConverter.apply(collection.updateOne(session,
                                                                           jsObj2Bson.apply(message.filter),
                                                                           jsObj2Bson.apply(message.update),
                                                                           options
                                                                          )
                                                     ) :
                                resultConverter.apply(collection.updateOne(jsObj2Bson.apply(message.filter),
                                                                           jsObj2Bson.apply(message.update),
                                                                           options
                                                                          )
                                                     )
                               );
        } catch (Exception exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));

        }

    }
}
