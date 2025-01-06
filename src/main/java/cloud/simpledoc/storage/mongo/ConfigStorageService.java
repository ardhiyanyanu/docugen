package cloud.simpledoc.storage.mongo;

import cloud.simpledoc.domain.external.ConfigStorageInterface;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.storage.mongo.model.ConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConfigStorageService implements ConfigStorageInterface {
    @Autowired
    ConfigRepository configRepository;

    @Override
    public Mono<Config> createConfig(Mono<Config> newConfig) {
        return newConfig.map(ConfigurationEntity::fromConfig)
                .flatMap(configurationEntity -> configRepository.save(configurationEntity))
                .map(ConfigurationEntity::toConfig);
    }

    @Override
    public Mono<Config> getConfigByReference(String configReference) {
        return configRepository.findById(configReference).map(ConfigurationEntity::toConfig);
    }
}
