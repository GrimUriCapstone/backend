package grimuri.backend.global;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimiter {

    private final ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser();

        return proxyManager.builder().build(key, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser() {

        // TODO: 정책 결정.
        Refill refill = Refill.intervally(1L, Duration.ofMinutes(1));
        Bandwidth bandwidth = Bandwidth.classic(3L, refill);

        return () -> BucketConfiguration.builder().addLimit(bandwidth).build();
    }
}
