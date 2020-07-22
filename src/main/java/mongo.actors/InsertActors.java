package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsArray;
import jsonvalues.JsObj;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

class InsertActors {

    private final DeploymentOptions deploymentOptions;
    private final Actors actors;
    private final Supplier<MongoCollection<JsObj>> collection;

    public InsertActors(final Supplier<MongoCollection<JsObj>> collection,
                        final DeploymentOptions deploymentOptions,
                        final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = Objects.requireNonNull(collection);
        this.actors = actors;
    }


    public <R> Future<ActorRef<JsObj, R>> deployInsertOne(final InsertOneOptions options,
                                                          final DeploymentOptions deploymentOptions,
                                                          final Function<InsertOneResult, R> resultConverter) {
        Objects.requireNonNull(options);
        Function<JsObj, R> c = m -> resultConverter.apply(Objects.requireNonNull(collection.get())
                                                                 .insertOne(m,
                                                                            options
                                                                           ));
        return actors.deploy(c,
                             deploymentOptions
                            );
    }

    public <R> Future<ActorRef<JsObj, R>> deployInsertOne(final Function<InsertOneResult, R> resultConverter) {
        Function<JsObj, R> c = m -> resultConverter.apply(Objects.requireNonNull(collection.get())
                                                                 .insertOne(m));
        return actors.deploy(c,
                             deploymentOptions
                            );
    }

    public <R> Function<JsObj, Future<R>> spawnInsertOne(final Function<InsertOneResult, R> resultConverter) {
        Function<JsObj, R> c = m -> resultConverter.apply(Objects.requireNonNull(collection.get())
                                                                 .insertOne(m));
        return actors.spawn(c,
                            deploymentOptions
                           );


    }

    public <R> Function<JsObj, Future<R>> spawnInsertOne(final InsertOneOptions options,
                                                                   final DeploymentOptions deploymentOptions,
                                                                   final Function<InsertOneResult, R> resultConverter) {
        Objects.requireNonNull(options);
        Function<JsObj, R> c = m -> {

            return resultConverter.apply(Objects.requireNonNull(collection.get())
                                                .insertOne(
                                                        m,
                                                        options
                                                          ));
        };
        return actors.spawn(c,
                            deploymentOptions
                           );
    }

    public <R> Future<ActorRef<JsArray, R>> deployInsertMany(final InsertManyOptions options,
                                                             final DeploymentOptions deploymentOptions,
                                                             final Function<InsertManyResult, R> resultConverter) {
        Objects.requireNonNull(options);
        Function<JsArray, R> c = m -> {
            List<JsObj> docs = Converters.arrayVal2ListOfObjVal.apply(m);

                return resultConverter.apply(Objects.requireNonNull(collection.get())
                                                    .insertMany(docs,
                                                                options
                                                               ));
        };
        return actors.deploy(c,
                             deploymentOptions
                            );
    }

    public <R> Future<ActorRef<JsArray, R>> deployInsertMany(final Function<InsertManyResult, R> resultConverter) {
        Function<JsArray, R> c = m -> {
            List<JsObj> docs = Converters.arrayVal2ListOfObjVal.apply(m);
            return resultConverter.apply(Objects.requireNonNull(collection.get())
                                                .insertMany(docs
                                                           ));

        };
        return actors.deploy(c,
                             deploymentOptions
                            );
    }

    public <R> Function<JsArray, Future<R>> spawnInsertMany(final InsertManyOptions options,
                                                                      final DeploymentOptions deploymentOptions,
                                                                      final Function<InsertManyResult, R> resultConverter) {
        Objects.requireNonNull(options);
        Function<JsArray, R> c = m -> {
            List<JsObj> docs = Converters.arrayVal2ListOfObjVal.apply(m);

            return resultConverter.apply(Objects.requireNonNull(collection.get())
                                                .insertMany(docs,
                                                            options
                                                           ));
        };
        return actors.spawn(c,
                             deploymentOptions
                            );
    }

    public <R> Function<JsArray, Future<R>> spawnInsertMany(final Function<InsertManyResult, R> resultConverter) {
        Function<JsArray, R> c = m -> {
            List<JsObj> docs = Converters.arrayVal2ListOfObjVal.apply(m);
            return resultConverter.apply(Objects.requireNonNull(collection.get())
                                                .insertMany(docs
                                                           ));

        };
        return actors.spawn(c,
                             deploymentOptions
                            );
    }


}
