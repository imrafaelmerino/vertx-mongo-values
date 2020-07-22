package mongo.actors;


import actors.Actors;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import io.vertx.core.*;
import jsonvalues.JsObj;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WatcherActors {
    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public WatcherActors(final Supplier<MongoCollection<JsObj>> collection,
                         final DeploymentOptions deploymentOptions,
                         final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = Objects.requireNonNull(collection);
        this.actors = actors;
    }


    public Future<String> deployWatcher(final Consumer<ChangeStreamIterable<JsObj>> consumer){

        AbstractVerticle watcher = new AbstractVerticle() {

            @Override
            public void start()  {
                consumer.accept(collection.get().watch());
            }
        };




        return actors.deploy(watcher);

    }

    public Future<String> deployWatcher(final Consumer<ChangeStreamIterable<JsObj>> consumer,
                                        final List<? extends Bson> pipeline){

        AbstractVerticle watcher = new AbstractVerticle() {

            @Override
            public void start()  {
                consumer.accept(collection.get().watch(pipeline));
            }
        };




        return actors.deploy(watcher);

    }

    public Future<String> deployWatcher(final Consumer<ChangeStreamIterable<JsObj>> consumer,
                                        final List<? extends Bson> pipeline,
                                        final ClientSession session){

        AbstractVerticle watcher = new AbstractVerticle() {

            @Override
            public void start()  {
                consumer.accept(collection.get().watch(session,pipeline));
            }
        };




        return actors.deploy(watcher);

    }

    public Future<String> deployWatcher(final Consumer<ChangeStreamIterable<JsObj>> consumer,
                                        final ClientSession session){

        AbstractVerticle watcher = new AbstractVerticle() {

            @Override
            public void start()  {
                consumer.accept(collection.get().watch(session));
            }
        };




        return actors.deploy(watcher);

    }


}
