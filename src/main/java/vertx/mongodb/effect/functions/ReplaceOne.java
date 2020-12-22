package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class ReplaceOne<O> implements λc<UpdateMessage, O> {

    private final Function<UpdateResult, O> resultConverter;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private final ReplaceOptions options;
    public static final ReplaceOptions DEFAULT_OPTIONS = new ReplaceOptions();


    public ReplaceOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter,
                      final ReplaceOptions options) {
        this.resultConverter = requireNonNull(resultConverter);
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    public ReplaceOne(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                      final Function<UpdateResult, O> resultConverter
                     ) {
        this(collectionSupplier,
             resultConverter,
             DEFAULT_OPTIONS
            );
    }


    @Override
    public Val<O> apply(final MultiMap context,
                        final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(resultConverter.apply(
                    collection.replaceOne(Converters.jsObj2Bson.apply(message.filter),
                                          message.update,
                                          options
                                         )
                                                     ));
        } catch (Exception exc) {
            return Cons.failure(Functions.toMongoValExc.apply(exc));

        }
    }
}
