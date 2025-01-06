package cloud.simpledoc.storage.mongo;

import cloud.simpledoc.storage.mongo.model.ConfigurationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ConfigRepository extends ReactiveMongoRepository<ConfigurationEntity, String> {
}
