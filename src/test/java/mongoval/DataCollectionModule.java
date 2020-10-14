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
    protected void initialize() {
        insertOne = this.ask(INSERT_ONE_ADDRESS);
        insertMany = this.ask(INSERT_MANY_ADDRESS);
        deleteMany = this.ask(DELETE_MANY_ADDRESS);
        λ<FindMessage, JsObj> findOneLambda = vertxRef.spawn("findOne",
                                                             new FindOne(collectionSupplier)
                                                            );
        findOne = m -> findOneLambda.apply(m)
                                    .map(Optional::ofNullable);
        findAll = vertxRef.spawn("findAll",
                                 new FindAll(collectionSupplier)
                                );
        count = vertxRef.spawn("count",
                               new Count(collectionSupplier)
                              );
        deleteOne = this.ask(DELETE_ONE_ADDRESS);
        replaceOne = this.ask(REPLACE_ONE_ADDRESS);
        updateOne = this.ask(UPDATE_ONE_ADDRESS);
        updateMany = vertxRef.spawn("updateMany",
                                    new UpdateMany<>(collectionSupplier,
                                                     updateResult2JsObj
                                    )
                                   );
        findOneAndReplace = vertxRef.spawn("findAndReplace",
                                           new FindOneAndReplace(collectionSupplier)
                                          );
        findOneAndDelete = vertxRef.spawn("findOneAndDelete",
                                          new FindOneAndDelete(collectionSupplier)
                                         );
        findOneAndUpdate = vertxRef.spawn("findOneAndUpdate",
                                          new FindOneAndUpdate(collectionSupplier)
                                         );
        aggregate = vertxRef.spawn("aggregate",
                                   new Aggregate<>(collectionSupplier,
                                                   aggregateResult2JsArray
                                   )
                                  );


    }


    @Override
    protected void deploy() {
        this.deploy(INSERT_ONE_ADDRESS,
                    new InsertOne<>(collectionSupplier,
                                    insertOneResult2HexId
                    )
                   );
        this.deploy(INSERT_MANY_ADDRESS,
                    new InsertMany<>(collectionSupplier,
                                     insertManyResult2JsArrayOfHexIds
                    )
                   );
        this.deploy(DELETE_MANY_ADDRESS,
                    new DeleteMany<>(collectionSupplier,
                                     deleteResult2JsObj
                    )
                   );
        this.deploy(DELETE_ONE_ADDRESS,
                    new DeleteOne<>(collectionSupplier,
                                    deleteResult2JsObj
                    )
                   );

        this.deploy(REPLACE_ONE_ADDRESS,
                    new ReplaceOne<>(collectionSupplier,
                                     updateResult2JsObj
                    )
                   );
        this.deploy(UPDATE_ONE_ADDRESS,
                    new UpdateOne<>(collectionSupplier,
                                    updateResult2JsObj
                    )
                   );

    }
}
