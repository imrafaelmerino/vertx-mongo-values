package mongoval;

import com.mongodb.client.MongoCollection;
import jsonvalues.JsArray;
import jsonvalues.JsObj;
import mongoval.functions.*;
import vertxval.exp.λ;

import java.util.Optional;
import java.util.function.Supplier;

import static mongoval.Converters.*;


public class DataCollectionModule extends MongoModule {

    private static final String DELETE_ONE_ADDRESS = "delete_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    public λ<JsObj, String> insertOne;
    public λ<JsObj, JsObj> deleteOne;
    public λ<JsArray, JsArray> insertMany;
    public λ<FindMessage, Optional<JsObj>> findOne;
    public λ<FindMessage, JsArray> findAll;
    public λ<UpdateMessage, JsObj> findOneAndReplace;
    private λ<JsObj, JsObj> findOneAndDelete;
    private λ<UpdateMessage, JsObj> findOneAndUpdate;

    public DataCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }


    @Override
    protected void define() {
        insertOne = this.<JsObj, String>getDeployedVerticle(INSERT_ONE_ADDRESS).ask();
        insertMany = this.<JsArray, JsArray>getDeployedVerticle(INSERT_MANY_ADDRESS).ask();
        λ<FindMessage, JsObj> findOneLambda = deployer.spawnFn(new FindOne(collectionSupplier));
        findOne = m -> findOneLambda.apply(m)
                                    .map(Optional::ofNullable);
        findAll = deployer.spawnFn(new FindAll(collectionSupplier));
        deleteOne = this.<JsObj, JsObj>getDeployedVerticle(DELETE_ONE_ADDRESS).ask();
        findOneAndReplace = deployer.spawnFn(new FindOneAndReplace(collectionSupplier));
        findOneAndDelete = deployer.spawnFn(new FindOneAndDelete(collectionSupplier));
        findOneAndUpdate = deployer.spawnFn(new FindOneAndUpdate(collectionSupplier));
    }

    @Override
    protected void deploy() {
        deployFn(INSERT_ONE_ADDRESS,
                 new InsertOne<>(collectionSupplier,
                                 insertOneResult2HexId
                 )
                );
        deployFn(INSERT_MANY_ADDRESS,
                 new InsertMany<>(collectionSupplier,
                                 insertManyResult2JsArrayOfHexIds
                 )
                );
        deployFn(DELETE_ONE_ADDRESS,
                 new DeleteOne<>(collectionSupplier,
                                 deleteResult2JsObj
                 )
                );

    }
}
