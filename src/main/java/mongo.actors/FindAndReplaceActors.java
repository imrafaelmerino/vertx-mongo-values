package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;

import java.util.function.Function;
import java.util.function.Supplier;

import static mongo.actors.Converters.objVal2Bson;
import static java.util.Objects.requireNonNull;


class FindAndReplaceActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public FindAndReplaceActors(final Supplier<MongoCollection<JsObj>> collection,
                                final DeploymentOptions deploymentOptions,
                                final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = requireNonNull(collection);
        this.actors = actors;
    }

    protected Future<ActorRef<UpdateMessage, JsObj>> deployFindOneAndReplace() {
        Function<UpdateMessage, JsObj> fn = m ->
                collection.get()
                          .findOneAndReplace(objVal2Bson.apply(m.filter),
                                             m.update
                                            );
        return actors.deploy(fn,
                             deploymentOptions
                            );
    }

    protected Function<UpdateMessage, Future<JsObj>> spawnFindOneAndReplace() {
        Function<UpdateMessage, JsObj> fn = m ->
                collection.get()
                          .findOneAndReplace(objVal2Bson.apply(m.filter),
                                             m.update
                                            );
        return actors.spawn(fn,
                            deploymentOptions
                           );
    }

    protected Future<ActorRef<UpdateMessage, JsObj>> deployFindOneAndReplace(final FindOneAndReplaceOptions options,
                                                                             final DeploymentOptions deploymentOptions) {
        Function<UpdateMessage, JsObj> fn = m ->
                collection.get()
                          .findOneAndReplace(objVal2Bson.apply(m.filter),
                                             m.update,
                                             requireNonNull(options)
                                            );
        return actors.deploy(fn,
                             requireNonNull(deploymentOptions)
                            );
    }

    protected Function<UpdateMessage, Future<JsObj>> spawnFindOneAndReplace(final FindOneAndReplaceOptions options,
                                                                                      final DeploymentOptions deploymentOptions) {
        Function<UpdateMessage, JsObj> fn = m ->
                collection.get()
                          .findOneAndReplace(objVal2Bson.apply(m.filter),
                                             m.update,
                                             requireNonNull(options)
                                            );
        return actors.spawn(fn,
                            deploymentOptions
                           );
    }

}
