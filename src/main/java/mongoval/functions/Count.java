package mongoval.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import org.bson.conversions.Bson;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class Count implements Function<JsObj, Long> {

    private final CountOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private static final CountOptions DEFAULT_OPTIONS = new CountOptions();

    public Count(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                 final CountOptions options
                ) {
        this.options = requireNonNull(options);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    public Count(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this.options = DEFAULT_OPTIONS;
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    @Override
    public Long apply(final JsObj queryMessage) {

        Bson                   query      = Converters.jsObj2Bson.apply(requireNonNull(queryMessage));
        MongoCollection<JsObj> collection = requireNonNull(this.collectionSupplier.get());
        return collection.countDocuments(query,
                                         options
                                        );


    }
}
