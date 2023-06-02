package grimuri.backend.global;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.Signature;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimiter {

    private final ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String email, Signature signature, Long capacity, Long refillTokens, Duration duration) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplier(capacity, refillTokens, duration);

        String key = email + "," + signature.getName();

        return proxyManager.builder().build(key, configSupplier);
    }

    public Bucket resolveBucket(Signature signature, Long capacity, Long refillTokens, Duration duration) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplier(capacity, refillTokens, duration);

        return proxyManager.builder().build(signature.getName(), configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplier(Long capacity, Long refillTokens, Duration duration) {
        Refill refill = Refill.intervally(refillTokens, duration);
        Bandwidth bandwidth = Bandwidth.classic(capacity, refill);

        return () -> BucketConfiguration.builder().addLimit(bandwidth).build();
    }
}
