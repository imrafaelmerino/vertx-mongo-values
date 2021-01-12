package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import jsonvalues.JsObj;
import vertx.effect.Val;
import vertx.effect.exp.Cons;
import vertx.effect.λc;
import vertx.mongodb.effect.UpdateMessage;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static vertx.mongodb.effect.Converters.jsObj2Bson;


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
    public Val<JsObj> apply(final MultiMap context,
                            final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));


        return Cons.of(() -> {
            try {
                var collection = requireNonNull(this.collectionSupplier.get());
                return Future.succeededFuture(collection
                                                      .findOneAndReplace(jsObj2Bson.apply(message.filter),
                                                                         message.update,
                                                                         options
                                                      ));

            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }


        });
    }
}
