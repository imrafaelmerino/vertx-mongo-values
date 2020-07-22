package mongo.actors;

import actors.ActorRef;
import com.mongodb.client.MongoCollection;
import io.vertx.core.Future;
import jsonvalues.JsObj;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static mongo.actors.Converters.insertOneResult2HexId;

public class DataCollectionModule extends MongoModule{


    Function<JsObj,Future<String>> insertOne;
    Function<FindMessage,Future<Optional<JsObj>>> findOne;

    public DataCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }

    @Override
    protected void defineActors(final List<Object> futures) {
       insertOne =  ((ActorRef<JsObj, String>) futures.get(0)).ask();
       findOne = ((ActorRef<FindMessage,JsObj>) futures.get(1)).ask().andThen(fut->fut.map(Optional::ofNullable));
    }



    @Override
    protected List<Future> deployActors()
    {
        return Arrays.asList(insertActors.deployInsertOne(insertOneResult2HexId),

                             findActors.deployFindOne());
    }
}
