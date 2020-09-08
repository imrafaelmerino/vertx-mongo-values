package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import jsonvalues.JsObj;
import mongoval.Converters;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class DeleteOne<O> implements Function<JsObj, O> {

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
    public O apply(final JsObj filter) {
        MongoCollection<JsObj> collection = requireNonNull(this.collectionSupplier.get());
        return resultConverter.apply(session
                                             != null ? collection.deleteOne(session,
                                                                            Converters.jsObj2Bson.apply(requireNonNull(filter)),
                                                                            options
                                                                           ) :
                                     collection.deleteOne(Converters.jsObj2Bson.apply(requireNonNull(filter)),
                                                          options
                                                         )
                                    );
    }
}
