package vertx.mongodb.effect.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import io.vertx.core.MultiMap;
import vertx.effect.λc;
import vertx.mongodb.effect.Converters;
import vertx.mongodb.effect.Failures;
import vertx.mongodb.effect.UpdateMessage;
import jsonvalues.JsObj;
import vertx.effect.exp.Cons;
import vertx.effect.Val;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


public class FindOneAndReplace implements λc<UpdateMessage, JsObj> {

    private final FindOneAndReplaceOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private static final FindOneAndReplaceOptions DEFAULT_OPTIONS = new FindOneAndReplaceOptions();


    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                             final FindOneAndReplaceOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    public FindOneAndReplace(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
            );
    }



    @Override
    public Val<JsObj> apply(final MultiMap context,final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));

        try {
            var collection = requireNonNull(this.collectionSupplier.get());
            return Cons.success(
                                collection
                                        .findOneAndReplace(Converters.jsObj2Bson.apply(message.filter),
                                                           message.update,
                                                           options
                                                          )
                               );
        } catch (Exception exc) {
            return Cons.failure(Failures.toMongoValExc.apply(exc));

        }
    }
}
