package vertx.mongodb.effect.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.Failures;
import vertx.mongodb.effect.UpdateMessage;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class FindOneAndUpdate implements λc<UpdateMessage, JsObj> {

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
    public Val<JsObj> apply(final MultiMap context,final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = this.collectionSupplier.get();

            return Cons.success(session != null ?
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
                                                         )
                               );
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));
        }
    }
}
