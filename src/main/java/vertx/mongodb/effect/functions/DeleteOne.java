package vertx.mongodb.effect.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import jsonvalues.JsObj;
import vertx.mongodb.effect.Failures;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class DeleteOne<O> implements λc<JsObj, O> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<DeleteResult, O> resultConverter;
    private final DeleteOptions options;
    private ClientSession session;
    private static final DeleteOptions DEFAULT_OPTIONS = new DeleteOptions();


    public DeleteOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<DeleteResult, O> resultConverter,
                     final DeleteOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }

    public DeleteOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<DeleteResult, O> resultConverter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = DEFAULT_OPTIONS;
    }


    public DeleteOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<DeleteResult, O> resultConverter,
                     final DeleteOptions options,
                     final ClientSession session) {
        this(collectionSupplier,
             resultConverter,
             options
            );
        this.session = requireNonNull(session);
    }

    @Override
    public Val<O> apply(final MultiMap context,final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));
        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(resultConverter.apply(session != null ?
                                                      collection.deleteOne(session,
                                                                           Converters.jsObj2Bson.apply(requireNonNull(query)),
                                                                           options
                                                                          ) :
                                                      collection.deleteOne(Converters.jsObj2Bson.apply(requireNonNull(query)),
                                                                           options
                                                                          )
                                                     )
                               );
        } catch (Throwable exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));
        }
    }
}
