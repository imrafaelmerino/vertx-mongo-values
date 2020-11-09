package vertx.mongodb.effect.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.UpdateMessage;
import jsonvalues.JsObj;
import vertx.mongodb.effect.Failures;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class UpdateMany<O> implements λc<UpdateMessage, O> {

    private final UpdateOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<UpdateResult, O> resultConverter;
    private ClientSession session;
    private static final UpdateOptions DEFAULT_OPTIONS = new UpdateOptions();


    public UpdateMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter
                     ) {
        this.options = DEFAULT_OPTIONS;
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
    }

    public UpdateMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final UpdateOptions options
                     ) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
    }

    public UpdateMany(final UpdateOptions options,
                      final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = requireNonNull(session);
    }

    @Override
    public Val<O> apply(final MultiMap context,final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(session != null ?
                                resultConverter.apply(collection.updateMany(session,
                                                                            Converters.jsObj2Bson.apply(message.filter),
                                                                            Converters.jsObj2Bson.apply(message.update),
                                                                            options
                                                                           )
                                                     ) :
                                resultConverter.apply(collection.updateMany(Converters.jsObj2Bson.apply(message.filter),
                                                                            Converters.jsObj2Bson.apply(message.update),
                                                                            options
                                                                           )
                                                     )
                               );
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));

        }
    }
}
