package mongoval.functions;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import jsonvalues.JsObj;
import mongoval.Converters;
import mongoval.FindMessage;
import org.bson.conversions.Bson;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

class Find<O> implements Function<FindMessage, O> {
    
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<FindIterable<JsObj>, O> converter;

    public Find(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                final Function<FindIterable<JsObj>, O> converter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.converter = requireNonNull(converter);
    }


    @Override
    public O apply(final FindMessage message) {
        Bson hint       = message.hint != null ? Converters.jsObj2Bson.apply(message.hint) : null;
        Bson max        = message.max != null ? Converters.jsObj2Bson.apply(message.max) : null;
        Bson projection = message.projection != null ? Converters.jsObj2Bson.apply(message.projection) : null;
        Bson sort       = message.sort != null ? Converters.jsObj2Bson.apply(message.sort) : null;
        Bson min        = message.min != null ? Converters.jsObj2Bson.apply(message.min) : null;
        return converter.apply(requireNonNull(collectionSupplier.get()).find(Converters.jsObj2Bson.apply(message.filter))
                                                                       .hint(hint)
                                                                       .max(max)
                                                                       .projection(projection)
                                                                       .sort(sort)
                                                                       .min(min)
                                                                       .batchSize(message.batchSize)
                                                                       .comment(message.comment)
                                                                       .hintString(message.hintString)
                                                                       .limit(message.limit)
                                                                       .skip(message.skip)
                                                                       .maxTime(message.maxTime,
                                                                        MILLISECONDS
                                                                       )
                                                                       .maxAwaitTime(message.maxAwaitTime,
                                                                             MILLISECONDS
                                                                            )
                                                                       .partial(message.partial)
                                                                       .showRecordId(message.showRecordId)
                                                                       .oplogReplay(message.oplogReplay)
                                                                       .noCursorTimeout(message.noCursorTimeout)
                              );
    }
}
