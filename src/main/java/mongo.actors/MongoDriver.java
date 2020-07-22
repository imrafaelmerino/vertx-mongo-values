package mongo.actors;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jsonvalues.JsObj;
import java.util.function.Function;
import java.util.function.Supplier;
import static java.util.Objects.requireNonNull;

public class MongoDriver extends AbstractVerticle {


    public Function<String, MongoDatabase> database() {
        return getDatabase;
    }


    public final Function<MongoDatabase, Function<String, MongoCollection<JsObj>>> collection =
            db -> name -> (MongoCollection<JsObj>) getCollection.apply(db)
                                                                .apply(name
                                                                      );


    public Supplier<MongoCollection<JsObj>> collection(String db,
                                                   String collectionName) {
        return () -> {
            MongoDatabase database = database().apply(db);
            return this.collection
                    .apply(database)
                    .apply(collectionName);
        };
    }

    private static volatile com.mongodb.client.MongoClient mongoClient;

    private static Function<String, MongoDatabase> getDatabase;

    private static Function<MongoDatabase, Function<String, MongoCollection<JsObj>>> getCollection;

    private final MongoClientSettings settings;

    public MongoDriver(final MongoClientSettings settings) {
        this.settings = settings;
    }

    @Override
    public void start(final Promise<Void> startPromise) {
        com.mongodb.client.MongoClient result = mongoClient;
        if (result == null) {
            synchronized (MongoDriver.class) {
                if (mongoClient == null) {
                    try {
                        mongoClient = result = MongoClients.create(requireNonNull(settings));
                        getDatabase = name -> mongoClient.getDatabase(requireNonNull(name));
                        getCollection = db -> name -> requireNonNull(db).getCollection(requireNonNull(name),
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
