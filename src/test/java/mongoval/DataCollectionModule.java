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
    private static final String UPDATE_ONE_ADDRESS = "update_one";
    private static final String REPLACE_ONE_ADDRESS = "replace_one";
    private static final String INSERT_ONE_ADDRESS = "insert_one";
    private static final String INSERT_MANY_ADDRESS = "insert_all";
    private static final String DELETE_MANY_ADDRESS = "delete_all";

    public λ<JsObj, String> insertOne;
    public λ<JsObj, JsObj> deleteOne;
    public λ<JsArray, JsArray> insertMany;
    public λ<JsObj, JsObj> deleteMany;
    public λ<FindMessage, Optional<JsObj>> findOne;
    public λ<FindMessage, JsArray> findAll;
    public λ<UpdateMessage, JsObj> findOneAndReplace;
    public λ<UpdateMessage, JsObj> replaceOne;
    public λ<UpdateMessage, JsObj> updateOne;
    public λ<JsObj, Long> count;
    public λ<UpdateMessage, JsObj> updateMany;
    public λ<JsArray, JsArray> aggregate;
    public λ<JsObj, JsObj> findOneAndDelete;
    public λ<UpdateMessage, JsObj> findOneAndUpdate;

    public DataCollectionModule(final Supplier<MongoCollection<JsObj>> collection) {
        super(collection);
    }


    @Override
    protected void define() {
        insertOne = this.<JsObj, String>getDeployedVerticle(INSERT_ONE_ADDRESS).ask();
        insertMany = this.<JsArray, JsArray>getDeployedVerticle(INSERT_MANY_ADDRESS).ask();
        deleteMany = this.<JsObj, JsObj>getDeployedVerticle(DELETE_MANY_ADDRESS).ask();
        λ<FindMessage, JsObj> findOneLambda = deployer.spawnFn(new FindOne(collectionSupplier));
        findOne = m -> findOneLambda.apply(m)
                                    .map(Optional::ofNullable);
        findAll = deployer.spawnFn(new FindAll(collectionSupplier));
        count = deployer.spawnFn(new Count(collectionSupplier));
        deleteOne = this.<JsObj, JsObj>getDeployedVerticle(DELETE_ONE_ADDRESS).ask();
        replaceOne = this.<UpdateMessage, JsObj>getDeployedVerticle(REPLACE_ONE_ADDRESS).ask();
        updateOne = this.<UpdateMessage, JsObj>getDeployedVerticle(UPDATE_ONE_ADDRESS).ask();
        updateMany = deployer.spawnFn(new UpdateMany<>(collectionSupplier,
                                                       updateResult2JsObj
                                      )
                                     );
        findOneAndReplace = deployer.spawnFn(new FindOneAndReplace(collectionSupplier));
        findOneAndDelete = deployer.spawnFn(new FindOneAndDelete(collectionSupplier));
        findOneAndUpdate = deployer.spawnFn(new FindOneAndUpdate(collectionSupplier));
        aggregate = deployer.spawnFn(new Aggregate<>(collectionSupplier,
                                                     aggregateResult2JsArray
                                     )
                                    );
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
        deployFn(DELETE_MANY_ADDRESS,
                 new DeleteMany<>(collectionSupplier,
                                  deleteResult2JsObj
                 )
                );
        deployFn(DELETE_ONE_ADDRESS,
                 new DeleteOne<>(collectionSupplier,
                                 deleteResult2JsObj
                 )
                );

        deployFn(REPLACE_ONE_ADDRESS,
                 new ReplaceOne<>(collectionSupplier,
                                  updateResult2JsObj
                 )
                );
        deployFn(UPDATE_ONE_ADDRESS,
                 new UpdateOne<>(collectionSupplier,
                                 updateResult2JsObj
                 )
                );
    }
}
