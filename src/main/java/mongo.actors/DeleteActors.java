package mongo.actors;
import actors.ActorRef;
import actors.Actors;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import jsonvalues.JsObj;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


class DeleteActors {

  public final Supplier<MongoCollection<JsObj>> collection;
  public final DeploymentOptions deploymentOptions;
  public final Actors actors;

  public DeleteActors(final Supplier<MongoCollection<JsObj>> collection,
                      final DeploymentOptions deploymentOptions,
                      final Actors actors) {
    this.deploymentOptions = deploymentOptions;
    this.collection = collection;
    this.actors = actors;
  }



  public <O> Future<ActorRef<JsObj, O>> deployDeleteOne(final DeleteOptions options,
                                                        final DeploymentOptions deploymentOptions,
                                                        final Function<DeleteResult,O> resultConverter){
    requireNonNull(options);
    Function<JsObj,O> deleteOne = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return
             resultConverter.apply(collection
                       .deleteOne(Converters.objVal2Bson.apply(filter),
                                  options)) ;
    };
    return actors.deploy(deleteOne,deploymentOptions);
  }

  public <O> Future<ActorRef<JsObj, O>> deployDeleteOne(final Function<DeleteResult,O> resultConverter){
    Function<JsObj,O> delete = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return resultConverter.apply(collection.deleteOne(Converters.objVal2Bson.apply(filter)));
    };
    return actors.deploy(delete,deploymentOptions);
  }


  public <O> Function<JsObj, Future<O>> spawnDeleteOne(final DeleteOptions options,
                                                                 final DeploymentOptions deploymentOptions,
                                                                 final Function<DeleteResult,O> resultConverter){
    requireNonNull(options);
    Function<JsObj,O> deleteOne = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return
              resultConverter.apply(collection
                                            .deleteOne(Converters.objVal2Bson.apply(filter),
                                                       options)) ;
    };
    return actors.spawn(deleteOne,deploymentOptions);
  }

  public <O> Function<JsObj, Future<O>> spawnDeleteOne(final Function<DeleteResult,O> resultConverter){
    Function<JsObj,O> delete = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return resultConverter.apply(collection.deleteOne(Converters.objVal2Bson.apply(filter)));
    };
    return actors.spawn(delete,deploymentOptions);
  }





  public <O> Future<ActorRef<JsObj, O>> deployDeleteMany(final DeleteOptions options,
                                                        final DeploymentOptions deploymentOptions,
                                                        final Function<DeleteResult,O> resultConverter){
    requireNonNull(options);
    Function<JsObj,O> deleteOne = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return
             resultConverter.apply(collection
                                           .deleteMany(Converters.objVal2Bson.apply(filter),
                                                      options));
    };
    return actors.deploy(deleteOne,deploymentOptions);
  }

  public <O> Future<ActorRef<JsObj, O>> deployDeleteMany(final Function<DeleteResult,O> resultConverter){
    Function<JsObj,O> delete = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return resultConverter.apply(collection.deleteMany(Converters.objVal2Bson.apply(filter)));
    };
    return actors.deploy(delete,deploymentOptions);
  }


  public <O> Function<JsObj, Future<O>> spawnDeleteMany(final DeleteOptions options,
                                                                  final DeploymentOptions deploymentOptions,
                                                                  final Function<DeleteResult,O> resultConverter){
    requireNonNull(options);
    Function<JsObj,O> deleteOne = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return
              resultConverter.apply(collection
                                            .deleteMany(Converters.objVal2Bson.apply(filter),
                                                        options));
    };
    return actors.spawn(deleteOne,deploymentOptions);
  }

  public <O> Function<JsObj, Future<O>> spawnDeleteMany(final Function<DeleteResult,O> resultConverter){
    Function<JsObj,O> delete = filter -> {
      MongoCollection<JsObj> collection = requireNonNull(this.collection.get());

      return resultConverter.apply(collection.deleteMany(Converters.objVal2Bson.apply(filter)));
    };
    return actors.spawn(delete,deploymentOptions);
  }
}
