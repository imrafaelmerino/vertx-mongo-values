package mongoval.functions;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import mongoval.Converters;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class Aggregate<O> implements Function<JsArray, O> {

    public final Function<AggregateIterable<JsObj>, O> resultConverter;
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;

    public Aggregate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<AggregateIterable<JsObj>, O> resultConverter
                     ) {
        this.resultConverter = resultConverter;
        this.collectionSupplier = collectionSupplier;
    }

    @Override
    public O apply(final JsArray m) {
        List<Bson>             pipeline   = Converters.jsArray2ListOfBson.apply(m);
        MongoCollection<JsObj> collection = requireNonNull(this.collectionSupplier.get());
        return resultConverter.apply(collection.aggregate(pipeline));
    }
}
