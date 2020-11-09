package vertx.mongodb.effect.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.Failures;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class FindOneAndDelete implements λc<JsObj, JsObj> {

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
    public Val<JsObj> apply(final MultiMap context,final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));

        try {
            var collection = this.collectionSupplier.get();
            return Cons.success(session != null ?
                                collection.findOneAndDelete(session,
                                                            Converters.jsObj2Bson.apply(query),
                                                            options
                                                           ) :
                                collection
                                        .findOneAndDelete(Converters.jsObj2Bson.apply(query),
                                                          options
                                                         ));
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));

        }
    }
}
