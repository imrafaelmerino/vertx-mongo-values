package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;
import org.bson.conversions.Bson;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class CountActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public CountActors(final Supplier<MongoCollection<JsObj>> collection,
                       final DeploymentOptions deploymentOptions,
                       final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = requireNonNull(collection);
        this.actors = actors;
    }


    protected  Future<ActorRef<JsObj, Long>> deployCount(final CountOptions inputs,
                                                         final DeploymentOptions deploymentOptions) {
        requireNonNull(inputs);

        Function<JsObj, Long> fn = queryMessage ->
        {
            Bson                   query      = Converters.objVal2Bson.apply(queryMessage);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                   collection.countDocuments(query,
                                             inputs
                                        );
        };
        return actors.deploy(fn,
                             deploymentOptions
                            );
    }


    protected  Future<ActorRef<JsObj, Long>> deployCount() {
        Function<JsObj, Long> fn = query -> {
            Bson                bson = Converters.objVal2Bson.apply(query);
            return requireNonNull(collection.get()).countDocuments(bson);

        };
        return actors.deploy(fn,
                             deploymentOptions
                            );
    }

    protected Function<JsObj, Future<Long>> spawnCount(final CountOptions inputs,
                                                                 final DeploymentOptions deploymentOptions) {
        requireNonNull(inputs);

        Function<JsObj, Long> fn = queryMessage ->
        {
            Bson                   query      = Converters.objVal2Bson.apply(queryMessage);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    collection.countDocuments(query,
                                              inputs
                                             );
        };
        return actors.spawn(fn,
                             deploymentOptions
                            );
    }


    protected Function<JsObj, Future<Long>> spawnCount() {
        Function<JsObj, Long> fn = query -> {
            Bson                bson = Converters.objVal2Bson.apply(query);
            return requireNonNull(collection.get()).countDocuments(bson);

        };
        return actors.spawn(fn,
                             deploymentOptions
                            );
    }

}
