package vertx.mongodb.effect.functions;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.FindMessage;
import vertx.mongodb.effect.Failures;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;
import vertx.effect.λ;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

class Find<O> implements λ<FindMessage, O> {

    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final Function<FindIterable<JsObj>, O> converter;

    public Find(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                final Function<FindIterable<JsObj>, O> converter) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.converter = requireNonNull(converter);
    }


    @Override
    public Val<O> apply(final FindMessage message) {
        if (message == null)
            return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var hint       = message.hint != null ? Converters.jsObj2Bson.apply(message.hint) : null;
            var max        = message.max != null ? Converters.jsObj2Bson.apply(message.max) : null;
            var projection = message.projection != null ? Converters.jsObj2Bson.apply(message.projection) : null;
            var sort       = message.sort != null ? Converters.jsObj2Bson.apply(message.sort) : null;
            var min        = message.min != null ? Converters.jsObj2Bson.apply(message.min) : null;
            var obj        = collectionSupplier.get();
            return Cons.success(converter.apply(requireNonNull(obj).find(Converters.jsObj2Bson.apply(message.filter))
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

                                               ));
        } catch (Throwable exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));
        }
    }
}
