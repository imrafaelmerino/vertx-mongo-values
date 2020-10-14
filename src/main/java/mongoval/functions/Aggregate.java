package mongoval.functions;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.MongoValException;
import vertxval.exp.Cons;
import vertxval.exp.Val;
import vertxval.exp.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class Aggregate<O> implements λ<JsArray, O> {

    public final Function<AggregateIterable<JsObj>, O> resultConverter;
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;

    public Aggregate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                     final Function<AggregateIterable<JsObj>, O> resultConverter
                    ) {
        this.resultConverter = resultConverter;
        this.collectionSupplier = collectionSupplier;
    }

    @Override
    public Val<O> apply(final JsArray m) {
        try {
            var pipeline   = Converters.jsArray2ListOfBson.apply(m);
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(resultConverter.apply(collection.aggregate(pipeline)));
        } catch (Throwable exc) {
            return Cons.failure(MongoValException.toMongoValExc.apply(exc));
        }
    }
}
