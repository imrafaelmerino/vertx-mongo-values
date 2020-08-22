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
    public final Supplier<MongoCollection<JsObj>> collection;

    public Aggregate(final Function<AggregateIterable<JsObj>, O> resultConverter,
                     final Supplier<MongoCollection<JsObj>> collection) {
        this.resultConverter = resultConverter;
        this.collection = collection;
    }

    @Override
    public O apply(final JsArray m) {
        List<Bson>             pipeline   = Converters.arrayVal2ListOfBsonVal.apply(m);
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return resultConverter.apply(collection.aggregate(pipeline));
    }
}
