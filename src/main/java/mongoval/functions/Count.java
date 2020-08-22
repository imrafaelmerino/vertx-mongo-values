package mongoval.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import jsonvalues.JsObj;
import mongoval.Converters;
import org.bson.conversions.Bson;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class Count implements Function<JsObj, Long> {

    public final CountOptions inputs;
    public final Supplier<MongoCollection<JsObj>> collection;

    public Count(final CountOptions inputs,
                 final Supplier<MongoCollection<JsObj>> collection) {
        this.inputs = inputs;
        this.collection = collection;
    }

    public Count(final Supplier<MongoCollection<JsObj>> collection) {
        this.inputs = new CountOptions();
        this.collection = collection;
    }

    @Override
    public Long apply(final JsObj queryMessage) {

        Bson                   query      = Converters.objVal2Bson.apply(queryMessage);
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return collection.countDocuments(query,
                                         inputs
                                        );


    }
}
