package mongoval;

import com.mongodb.client.MongoCollection;
import jsonvalues.JsObj;
import mongoval.functions.FindOne;
import mongoval.functions.InsertOne;
import vertxval.exp.λ;

import java.util.Optional;
import java.util.function.Supplier;

import static mongoval.Converters.insertOneResult2HexId;


public class DataCollectionModule extends MongoModule {


    private final String INSERT_ONE_ADDRESS = "insert_one";
    public λ<JsObj, String> insertOne;
    public λ<FindMessage, Optional<JsObj>> findOne;

    public DataCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }


    @Override
    protected void define() {
        insertOne = this.<JsObj, String>getDeployedVerticle(INSERT_ONE_ADDRESS).ask();
        λ<FindMessage, JsObj> lambda = deployer.spawnFn(new FindOne(collection));
        findOne = m -> lambda.apply(m)
                             .map(Optional::ofNullable);


    }

    @Override
    protected void deploy() {
        deployFn(INSERT_ONE_ADDRESS,
                 new InsertOne<>(collection,
                                 insertOneResult2HexId
                 )
                );
    }
}
