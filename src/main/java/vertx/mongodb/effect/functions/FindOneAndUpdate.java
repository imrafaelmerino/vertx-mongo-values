package vertx.mongodb.effect.functions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
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


public class FindOneAndUpdate implements λc<UpdateMessage, JsObj> {

    private final FindOneAndUpdateOptions options;
    private final Supplier<MongoCollection<JsObj>> collectionSupplier;
    private static final FindOneAndUpdateOptions DEFAULT_OPTIONS = new FindOneAndUpdateOptions();


    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        this(collectionSupplier,
             DEFAULT_OPTIONS
        );
    }

    public FindOneAndUpdate(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                            final FindOneAndUpdateOptions options) {
        this.collectionSupplier = requireNonNull(collectionSupplier);
        this.options = requireNonNull(options);
    }

    @Override
    public Val<JsObj> apply(final MultiMap context,
                            final UpdateMessage message) {
        if (message == null) return Cons.failure(new IllegalArgumentException("message is null"));


        return Cons.of(() -> {
            try {
                var collection = this.collectionSupplier.get();

                return Future.succeededFuture(collection
                                                      .findOneAndUpdate(jsObj2Bson.apply(message.filter),
                                                                        jsObj2Bson.apply(message.update),
                                                                        options
                                                      ));
            } catch (Exception exc) {
                return Future.failedFuture(Functions.toMongoValExc.apply(exc));
            }
        });
    }
}
