package com.lab.app;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CpuService {

    /** Burns CPU on `threads` threads for `seconds` by counting primes. */
    public Map<String, Object> burn(int seconds, int threads) throws Exception {
        seconds = Math.min(Math.max(seconds, 1), 300);
        threads = Math.min(Math.max(threads, 1), 64);
        long end = System.nanoTime() + seconds * 1_000_000_000L;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Long>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                long primes = 0, n = 2;
                while (System.nanoTime() < end) {
                    if (isPrime(n++)) primes++;
                }
                return primes;
            }));
        }
        long total = 0;
        for (Future<Long> f : futures) total += f.get();
        pool.shutdown();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("seconds", seconds);
        out.put("threads", threads);
        out.put("primesFound", total);
        out.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        return out;
    }

    private boolean isPrime(long n) {
        if (n < 2) return false;
        for (long i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }
}
