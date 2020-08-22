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
    
    private final Supplier<MongoCollection<JsObj>> collection;
    private final Function<FindIterable<JsObj>, O> converter;

    public Find(final Supplier<MongoCollection<JsObj>> collection,
                final Function<FindIterable<JsObj>, O> converter) {
        this.collection = requireNonNull(collection);
        this.converter = requireNonNull(converter);
    }


    @Override
    public O apply(final FindMessage message) {
        Bson hint       = message.hint != null ? Converters.objVal2Bson.apply(message.hint) : null;
        Bson max        = message.max != null ? Converters.objVal2Bson.apply(message.max) : null;
        Bson projection = message.projection != null ? Converters.objVal2Bson.apply(message.projection) : null;
        Bson sort       = message.sort != null ? Converters.objVal2Bson.apply(message.sort) : null;
        Bson min        = message.min != null ? Converters.objVal2Bson.apply(message.min) : null;
        return converter.apply(requireNonNull(collection.get()).find(Converters.objVal2Bson.apply(message.filter))
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
