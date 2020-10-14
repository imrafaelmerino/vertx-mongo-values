package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import jsonvalues.JsObj;
import mongoval.MongoValException;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


public class DeleteOne<O> implements λ<JsObj, O> {

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
    public Val<O> apply(final JsObj query) {
        if (query == null) return Cons.failure(new IllegalArgumentException("query is null"));
        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(resultConverter.apply(session != null ?
                                                      collection.deleteOne(session,
                                                                           jsObj2Bson.apply(requireNonNull(query)),
                                                                           options
                                                                          ) :
                                                      collection.deleteOne(jsObj2Bson.apply(requireNonNull(query)),
                                                                           options
                                                                          )
                                                     )
                               );
        } catch (Throwable exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));
        }
    }
}
