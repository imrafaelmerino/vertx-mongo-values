package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;
import java.util.function.Function;
import java.util.function.Supplier;
import static mongo.actors.Converters.objVal2Bson;
import static java.util.Objects.requireNonNull;


class FindAndDeleteActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public FindAndDeleteActors(final Supplier<MongoCollection<JsObj>> collection,
                               final DeploymentOptions deploymentOptions,
                               final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = requireNonNull(collection);
        this.actors = actors;
    }

    protected Future<ActorRef<JsObj, JsObj>> deployFindOneAndDelete() {
        Function<JsObj,JsObj> fn = o ->
                collection.get().findOneAndDelete(objVal2Bson.apply(o));
        return actors.deploy(fn,deploymentOptions);
    }

    protected Function<JsObj, Future<JsObj>> spawnFindOneAndDelete() {
        Function<JsObj,JsObj> fn = o ->
                collection.get().findOneAndDelete(objVal2Bson.apply(o));
        return actors.spawn(fn,deploymentOptions);
    }
    protected Future<ActorRef<JsObj, JsObj>> deployFindOneAndDelete(final FindOneAndDeleteOptions options,
                                                                    final DeploymentOptions deploymentOptions) {
        Function<JsObj,JsObj> fn = m ->
                collection.get().findOneAndDelete(objVal2Bson.apply(m),
                                                  requireNonNull(options));
        return actors.deploy(fn,
                             requireNonNull(deploymentOptions));
    }

    protected Function<JsObj, Future<JsObj>> spawnFindOneAndDelete(final FindOneAndDeleteOptions options,
                                                                             final DeploymentOptions deploymentOptions) {
        Function<JsObj,JsObj> fn = o ->
                collection.get().findOneAndDelete(objVal2Bson.apply(o),
                                                  requireNonNull(options));
        return actors.spawn(fn,
                            requireNonNull(deploymentOptions));
    }








}
