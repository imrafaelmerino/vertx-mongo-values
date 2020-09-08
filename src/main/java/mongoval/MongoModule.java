package mongoval;


import com.mongodb.client.MongoCollection;
import io.vertx.core.DeploymentOptions;
import jsonvalues.JsObj;
import vertxval.VertxModule;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class MongoModule extends VertxModule {
    private static final DeploymentOptions DEFAULT_DEPLOYMENT_OPTIONS = new DeploymentOptions().setWorker(true);
    public final Supplier<MongoCollection<JsObj>> collectionSupplier;


    public MongoModule(final Supplier<MongoCollection<JsObj>> collectionSupplier,
                       final DeploymentOptions deploymentOptions
                       ) {
        super(deploymentOptions);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }

    public MongoModule(final Supplier<MongoCollection<JsObj>> collectionSupplier) {
        super(DEFAULT_DEPLOYMENT_OPTIONS);
        this.collectionSupplier = requireNonNull(collectionSupplier);
    }



}
