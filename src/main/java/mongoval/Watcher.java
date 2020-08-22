package mongoval;


import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import io.vertx.core.AbstractVerticle;
import jsonvalues.JsObj;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Watcher extends AbstractVerticle {

    public final Supplier<MongoCollection<JsObj>> collection;
    public final Consumer<ChangeStreamIterable<JsObj>> consumer;
    private ClientSession session;

    public Watcher(final Supplier<MongoCollection<JsObj>> collection,
                   final Consumer<ChangeStreamIterable<JsObj>> consumer) {
        this.collection = requireNonNull(collection);
        this.consumer = requireNonNull(consumer);
    }

    public Watcher(final Supplier<MongoCollection<JsObj>> collection,
                   final Consumer<ChangeStreamIterable<JsObj>> consumer,
                   final ClientSession session) {
        this(collection,
             consumer);
        this.session = session;
    }

    @Override
    public void start() {
        if (session != null) consumer.accept(collection.get()
                                                       .watch(session));
        else
            consumer.accept(collection.get()
                                      .watch());
    }


}
