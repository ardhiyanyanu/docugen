package cloud.simpledoc.domain.external;

import cloud.simpledoc.domain.model.Config;
import reactor.core.publisher.Mono;

public interface ConfigStorageInterface {
    Mono<Config> getConfigByReference(String configReference);
    Mono<Config> createConfig(Mono<Config> newConfig);
}
