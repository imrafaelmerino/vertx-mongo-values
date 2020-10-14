package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import jsonvalues.JsObj;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.jsObj2Bson;


public class DeleteMany<O> implements λ<JsObj, O> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<DeleteResult, O> resultConverter;
    private final DeleteOptions options;
    private ClientSession session;
    private static final DeleteOptions DEFAULT_OPTIONS = new DeleteOptions();


    public DeleteMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<DeleteResult, O> resultConverter,
                      final DeleteOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }

    public DeleteMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<DeleteResult, O> resultConverter) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }


    public DeleteMany(final Supplier<MongoCollection<JsObj>> collectionSupplier,
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
                                                      collection.deleteMany(session,
                                                                            jsObj2Bson.apply(requireNonNull(query)),
                                                                            options
                                                                           ) :
                                                      collection.deleteMany(jsObj2Bson.apply(requireNonNull(query)),
                                                                            options
                                                                           )
                                                     )
                               );
        } catch (Exception exc) {
            return Cons.failure(exc);
        }
    }
}
