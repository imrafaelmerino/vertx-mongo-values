package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import static mongo.actors.Converters.objVal2Bson;

import static java.util.Objects.requireNonNull;

class FindActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public FindActors(final Supplier<MongoCollection<JsObj>> collection,
                      final DeploymentOptions deploymentOptions,
                      final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = requireNonNull(collection);
        this.actors = actors;
    }

    protected Future<ActorRef<FindMessage, JsArray>> deployFind() {
        return _deployFind(deploymentOptions,JsArray::ofIterable);
    }

    protected Future<ActorRef<FindMessage, JsObj>> deployFindOne() {
        return _deployFind(deploymentOptions,
                           MongoIterable::first);
    }

    protected Function<FindMessage, Future<JsArray>> spawnFind() {
        return _spawnFind(deploymentOptions,JsArray::ofIterable);
    }

    protected Function<FindMessage, Future<JsObj>> spawnFindOne() {
        return _spawnFind(deploymentOptions,MongoIterable::first);
    }


    protected Future<ActorRef<FindMessage, JsArray>> deployFind(final DeploymentOptions deploymentOptions) {
        return _deployFind(deploymentOptions,JsArray::ofIterable);
    }

    protected Future<ActorRef<FindMessage, JsObj>> deployFindOne(final DeploymentOptions deploymentOptions) {
        return _deployFind(deploymentOptions,MongoIterable::first);
    }


    protected Function<FindMessage, Future<JsArray>> spawnFind(final DeploymentOptions deploymentOptions) {
        return _spawnFind(deploymentOptions,JsArray::ofIterable);
    }


    protected Function<FindMessage, Future<JsObj>> spawnFindOne(final DeploymentOptions deploymentOptions) {
        return _spawnFind(deploymentOptions,MongoIterable::first);
    }


    private <O> Future<ActorRef<FindMessage, O>> _deployFind(final DeploymentOptions deploymentOptions,
                                                             final Function<FindIterable<JsObj>, O> converter) {


        return actors.deploy(findFn(converter),
                             deploymentOptions
                            );
    }

    private <O> Function<FindMessage, Future<O>> _spawnFind(final DeploymentOptions deploymentOptions,

                                                                      final Function<FindIterable<JsObj>, O> converter) {
        return actors.spawn(findFn(converter),
                            deploymentOptions
                           );
    }


    private <O> Function<FindMessage, O> findFn(final Function<FindIterable<JsObj>,O> converter) {
        return message ->
        {
            Bson hint  = message.hint !=null ? objVal2Bson.apply(message.hint):null;
            Bson max = message.max != null ? objVal2Bson.apply(message.max) : null;
            Bson  projection  = message.projection != null ? objVal2Bson.apply(message.projection) : null;
            Bson  sort        = message.sort != null ? objVal2Bson.apply(message.sort):null;
            Bson  min         = message.min != null ? objVal2Bson.apply(message.min):null;
            return converter.apply(requireNonNull(this.collection.get()).find(objVal2Bson.apply(message.filter))
                                                                           .hint(hint)
                                                                           .max(max)
                                                                           .projection(projection)
                                                                           .sort(sort)
                                                                           .min(min)
                                                                           .batchSize(message.batchSize)
                                                                           .comment(message.comment)
                                                                           .hintString(message.hintString)
                                                                           .limit(message.limit)
                                                                           .skip(message.skip)
                                                                           .maxTime(message.maxTime,
                                                                                    TimeUnit.MILLISECONDS
                                                                            )
                                                                           .maxAwaitTime(message.maxAwaitTime,
                                                                                         TimeUnit.MILLISECONDS
                                                                                        )
                                                                           .partial(message.partial)
                                                                           .showRecordId(message.showRecordId)
                                                                           .oplogReplay(message.oplogReplay)
                                                                           .noCursorTimeout(message.noCursorTimeout)
                                     );
        };
    }

}
