package mongo.actors;

import actors.ActorRef;
import actors.Actors;
import actors.ActorsModule;
import com.mongodb.client.MongoCollection;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class MongoModule extends ActorsModule {
    public static final DeploymentOptions DEFAULT_DEPLOYMENT_OPTIONS = new DeploymentOptions().setWorker(true);

    public final Supplier<MongoCollection<JsObj>> collection;
    protected InsertActors insertActors;
    protected FindActors findActors;
    protected FindAndDeleteActors findAndDeleteActors;
    protected FindAndUpdateActors findAndUpdateActors;
    protected FindAndReplaceActors findAndReplaceActors;
    protected UpdateActors updateActors;
    protected DeleteActors deleteActors;
    protected ReplaceActors replaceActors;
    protected WatcherActors watcherActors;
    protected CountActors countActors;
    protected AggregateActors aggregateActors;

    public MongoModule(final DeploymentOptions deploymentOptions,
                       final Supplier<MongoCollection<JsObj>> collection) {
        super(deploymentOptions);
        this.collection = Objects.requireNonNull(collection);
    }

    public MongoModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(DEFAULT_DEPLOYMENT_OPTIONS);
        this.collection = Objects.requireNonNull(collection);
    }


    @Override
    protected void initModule(final Actors actors) {
        Objects.requireNonNull(actors);
        insertActors = new InsertActors(collection,
                                        deploymentOptions,
                                        actors
        );
        findActors = new FindActors(collection,
                                    deploymentOptions,
                                    actors
        );
        findAndDeleteActors = new FindAndDeleteActors(collection,
                                                      deploymentOptions,
                                                      actors
        );
        findAndUpdateActors = new FindAndUpdateActors(collection,
                                                      deploymentOptions,
                                                      actors
        );
        findAndReplaceActors = new FindAndReplaceActors(collection,
                                                        deploymentOptions,
                                                        actors
        );
        updateActors = new UpdateActors(collection,
                                        deploymentOptions,
                                        actors
        );
        deleteActors = new DeleteActors(collection,
                                        deploymentOptions,
                                        actors
        );
        replaceActors = new ReplaceActors(collection,
                                          deploymentOptions,
                                          actors
        );
        watcherActors = new WatcherActors(collection,
                                          deploymentOptions,
                                          actors
        );
        countActors = new CountActors(collection,
                                      deploymentOptions,
                                      actors
        );
        aggregateActors = new AggregateActors(collection,
                                              deploymentOptions,
                                              actors
        );
    }

    protected <O> Supplier<Future<O>> spawn(final Function<MongoCollection<JsObj>, O> fn) {
        return spawn(fn,
                     deploymentOptions
                    );
    }

    protected <O> Future<ActorRef<JsObj, O>> deploy(final Function<MongoCollection<JsObj>, O> fn) {
        return deploy(fn,
                      deploymentOptions
                     );
    }

    protected <O> Supplier<Future<O>> spawn(final Function<MongoCollection<JsObj>, O> fn,
                                            final DeploymentOptions deploymentOptions) {

        Function<JsObj, O> actor = o -> fn.apply(collection.get());
        return () -> actors.spawn(actor,
                                  deploymentOptions
                                 )
                           .apply(JsObj.empty());
    }

    protected <O> Future<ActorRef<JsObj, O>> deploy(final Function<MongoCollection<JsObj>, O> fn,
                                                    final DeploymentOptions deploymentOptions) {

        Function<JsObj, O> actor = o -> fn.apply(collection.get());
        return actors.deploy(actor,
                             deploymentOptions
                            );
    }
}
