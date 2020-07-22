package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static mongo.actors.Converters.objVal2Bson;
import static java.util.Objects.requireNonNull;


class UpdateActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public UpdateActors(final Supplier<MongoCollection<JsObj>> collection,
                        final DeploymentOptions deploymentOptions,
                        final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = Objects.requireNonNull(collection);
        this.actors = actors;
    }


    public <O> Future<ActorRef<UpdateMessage, O>> deployUpdateOne(final UpdateOptions options,
                                                                  final DeploymentOptions deploymentOptions,
                                                                  final Function<UpdateResult, O> resultConverter) {
        Objects.requireNonNull(options);
        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.updateOne(objVal2Bson.apply(message.filter),
                                                               objVal2Bson.apply(message.update),
                                                               options
                                                              ));
        };

        return actors.deploy(updateFn,
                             deploymentOptions
                            );
    }

    public <O> Future<ActorRef<UpdateMessage, O>> deployUpdateOne(final Function<UpdateResult, O> resultConverter) {

        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return resultConverter.apply(collection.updateOne(objVal2Bson.apply(message.filter),
                                                              objVal2Bson.apply(message.update)
                                                             ));
        };

        return actors.deploy(updateFn,
                             deploymentOptions
                            );
    }

    public <O> Function<UpdateMessage, Future<O>> spawnUpdateOne(final UpdateOptions options,
                                                                           final DeploymentOptions deploymentOptions,
                                                                           final Function<UpdateResult, O> resultConverter) {
        Objects.requireNonNull(options);
        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.updateOne(objVal2Bson.apply(message.filter),
                                                               objVal2Bson.apply(message.update),
                                                               options
                                                              ));
        };

        return actors.spawn(updateFn,
                            deploymentOptions
                           );
    }

    public <O> Function<UpdateMessage, Future<O>> spawnUpdateOne(final Function<UpdateResult, O> resultConverter) {

        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return resultConverter.apply(collection.updateOne(objVal2Bson.apply(message.filter),
                                                              objVal2Bson.apply(message.update)
                                                             ));
        };

        return actors.spawn(updateFn,
                            deploymentOptions
                           );
    }


    public <O> Future<ActorRef<UpdateMessage, O>> deployUpdateMany(final UpdateOptions options,
                                                                   final DeploymentOptions deploymentOptions,
                                                                   final Function<UpdateResult, O> resultConverter) {
        Objects.requireNonNull(options);
        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.updateMany(objVal2Bson.apply(message.filter),
                                                                objVal2Bson.apply(message.update),
                                                                options
                                                               ));
        };

        return actors.deploy(updateFn,
                             deploymentOptions
                            );
    }

    public <O> Future<ActorRef<UpdateMessage, O>> deployUpdateMany(final Function<UpdateResult, O> resultConverter) {

        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return resultConverter.apply(collection.updateMany(objVal2Bson.apply(message.filter),
                                                               objVal2Bson.apply(message.update)
                                                              ));
        };

        return actors.deploy(updateFn,
                             deploymentOptions
                            );
    }

    public <O> Function<UpdateMessage, Future<O>> spawnUpdateMany(final UpdateOptions options,
                                                                            final DeploymentOptions deploymentOptions,
                                                                            final Function<UpdateResult, O> resultConverter) {
        Objects.requireNonNull(options);
        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.updateMany(objVal2Bson.apply(message.filter),
                                                                objVal2Bson.apply(message.update),
                                                                options
                                                               ));
        };

        return actors.spawn(updateFn,
                            deploymentOptions
                           );
    }

    public <O> Function<UpdateMessage, Future<O>> spawnUpdateMany(final Function<UpdateResult, O> resultConverter) {

        Function<UpdateMessage, O> updateFn = message -> {
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return resultConverter.apply(collection.updateMany(objVal2Bson.apply(message.filter),
                                                               objVal2Bson.apply(message.update)
                                                              ));
        };

        return actors.spawn(updateFn,
                            deploymentOptions
                           );
    }


}
