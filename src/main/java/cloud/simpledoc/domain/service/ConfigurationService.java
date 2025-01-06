package cloud.simpledoc.domain.service;

import cloud.simpledoc.domain.external.ConfigStorageInterface;
import cloud.simpledoc.domain.model.Config;
import cloud.simpledoc.domain.model.exception.AlreadyExistingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConfigurationService {
    @Autowired
    ConfigStorageInterface configStorage;

    public Mono<Config> createConfig(Config newConfig) {
        return configStorage.getConfigByReference(newConfig.getReference())
                .flatMap(config -> {
                    if (config != null) return Mono.error(new AlreadyExistingException(""));
                    return Mono.just(newConfig);
                })
                .defaultIfEmpty(newConfig)
                .flatMap(config -> configStorage.createConfig(Mono.just(newConfig)));
    }
}
