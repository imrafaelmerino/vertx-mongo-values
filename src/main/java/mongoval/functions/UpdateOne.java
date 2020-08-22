package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.JsObj;
import mongoval.UpdateMessage;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.objVal2Bson;


public class UpdateOne<O> implements Function<UpdateMessage, O> {

    public final Supplier<MongoCollection<JsObj>> collection;
    public final Function<UpdateResult, O> resultConverter;
    public final UpdateOptions options;
    private ClientSession session;

    public UpdateOne(final Supplier<MongoCollection<JsObj>> collection,
                     final Function<UpdateResult, O> resultConverter,
                     final UpdateOptions options) {
        this.collection = requireNonNull(collection);
        this.resultConverter = requireNonNull(resultConverter);
        this.options = requireNonNull(options);
    }


    public UpdateOne(final Supplier<MongoCollection<JsObj>> collection,
                     final Function<UpdateResult, O> resultConverter,
                     final UpdateOptions options,
                     final ClientSession session) {
        this(collection,
             resultConverter,
             options
            );
        this.session = Objects.requireNonNull(session);
    }

    @Override
    public O apply(final UpdateMessage message) {
        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return session != null ?
               resultConverter.apply(collection.updateOne(session,
                                                          objVal2Bson.apply(message.filter),
                                                          objVal2Bson.apply(message.update),
                                                          options
                                                         )
                                    ) :
               resultConverter.apply(collection.updateOne(objVal2Bson.apply(message.filter),
                                                          objVal2Bson.apply(message.update),
                                                          options
                                                         )
                                    );

    }
}
