package mongoval.functions;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import jsonvalues.JsObj;
import mongoval.UpdateMessage;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static mongoval.Converters.objVal2Bson;


public class UpdateMany<O> implements Function<UpdateMessage, O> {

    public final UpdateOptions options;
    public final Supplier<MongoCollection<JsObj>> collection;
    public final Function<UpdateResult, O> resultConverter;
    private ClientSession session;

    public UpdateMany(final UpdateOptions options,
                      final Supplier<MongoCollection<JsObj>> collection,
                      final Function<UpdateResult, O> resultConverter) {
        this.options = requireNonNull(options);
        this.collection = requireNonNull(collection);
        this.resultConverter = requireNonNull(resultConverter);
    }

    public UpdateMany(final UpdateOptions options,
                      final Supplier<MongoCollection<JsObj>> collection,
                      final Function<UpdateResult, O> resultConverter,
                      final ClientSession session) {
        this(options,
             collection,
             resultConverter
            );
        this.session = requireNonNull(session);
    }

    @Override
    public O apply(final UpdateMessage message) {

        MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
        return session != null ?
               resultConverter.apply(collection.updateMany(session,
                                                           objVal2Bson.apply(message.filter),
                                                           objVal2Bson.apply(message.update),
                                                           options
                                                          )
                                    ) :
               resultConverter.apply(collection.updateMany(objVal2Bson.apply(message.filter),
                                                           objVal2Bson.apply(message.update),
                                                           options
                                                          )
                                    )
                ;
    }
}
