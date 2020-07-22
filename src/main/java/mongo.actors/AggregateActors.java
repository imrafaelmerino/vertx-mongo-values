package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class AggregateActors {

    public final DeploymentOptions deploymentOptions;
    public final Actors actors;
    public final Supplier<MongoCollection<JsObj>> collection;

    public AggregateActors(final Supplier<MongoCollection<JsObj>> collection,
                           final DeploymentOptions deploymentOptions,
                           final Actors actors) {
        this.deploymentOptions = deploymentOptions;
        this.collection = requireNonNull(collection);
        this.actors = actors;
    }

    protected <O> Future<ActorRef<JsArray, O>> deployAggregate(final DeploymentOptions deploymentOptions,
                                                               final Function<AggregateIterable<JsObj>, O> resultConverter) {
        requireNonNull(resultConverter);

        Function<JsArray, O> fn = m ->
        {
            List<Bson>             pipeline   = Converters.arrayVal2ListOfBsonVal.apply(m);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.aggregate(pipeline));
        };
        return actors.deploy(fn,
                             deploymentOptions
                            );
    }

    protected <O> Function<JsArray, Future<O>> spawnAggregate(final DeploymentOptions deploymentOptions,
                                                                        final Function<AggregateIterable<JsObj>, O> resultConverter) {
        requireNonNull(resultConverter);

        Function<JsArray, O> fn = m ->
        {
            List<Bson>             pipeline   = Converters.arrayVal2ListOfBsonVal.apply(m);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.aggregate(pipeline));
        };
        return actors.spawn(fn,
                            deploymentOptions
                           );
    }

    protected <O> Future<ActorRef<JsArray, O>> deployAggregate(final Function<AggregateIterable<JsObj>, O> resultConverter) {
        requireNonNull(resultConverter);

        Function<JsArray, O> fn = m ->
        {
            List<Bson>             pipeline   = Converters.arrayVal2ListOfBsonVal.apply(m);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.aggregate(pipeline));
        };
        return actors.deploy(fn,
                             deploymentOptions
                            );
    }


    protected <O> Function<JsArray, Future<O>> spawnAggregate(final Function<AggregateIterable<JsObj>, O> resultConverter) {
        requireNonNull(resultConverter);

        Function<JsArray, O> fn = m ->
        {
            List<Bson>             pipeline   = Converters.arrayVal2ListOfBsonVal.apply(m);
            MongoCollection<JsObj> collection = requireNonNull(this.collection.get());
            return
                    resultConverter.apply(collection.aggregate(pipeline));
        };
        return actors.spawn(fn,
                            deploymentOptions
                           );
    }


}
