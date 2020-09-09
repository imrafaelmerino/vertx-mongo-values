package mongoval;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jsonvalues.JsObj;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class MongoVertxClient extends AbstractVerticle {


    public Function<String, MongoDatabase> getDatabase;

    public final Supplier<MongoCollection<JsObj>> getCollection(String db,
                                                                String collectionName) {
        return () -> {
            MongoDatabase database = getDatabase.apply(requireNonNull(db));
            return requireNonNull(this.getCollectionFromMongoDB)
                    .apply(database)
                    .apply(requireNonNull(collectionName));
        };
    }

    private volatile MongoClient mongoClient;

    private Function<MongoDatabase, Function<String, MongoCollection<JsObj>>> getCollectionFromMongoDB;

    private final MongoClientSettings settings;

    public MongoVertxClient(final MongoClientSettings settings) {
        this.settings = requireNonNull(settings);
    }

    @Override
    public void start(final Promise<Void> startPromise) {
        requireNonNull(startPromise);
        MongoClient result = mongoClient;
        if (result == null) {
            synchronized (MongoVertxClient.class) {
                if (mongoClient == null) {
                    try {
                        mongoClient = result = MongoClients.create(requireNonNull(settings));
                        getDatabase = name -> mongoClient.getDatabase(requireNonNull(name));
                        getCollectionFromMongoDB = db -> name -> requireNonNull(db).getCollection(requireNonNull(name),
                                                                                                  JsObj.class
                                                                                                 );
                        startPromise.complete();
                    } catch (Exception error) {
                        startPromise.fail(error);
                    }
                }
                else {
                    result = mongoClient;
                    startPromise.complete();
                }
            }
        }
        else startPromise.complete();
    }


}
