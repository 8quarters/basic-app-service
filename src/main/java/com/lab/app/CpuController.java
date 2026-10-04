package com.lab.app;

import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CpuController {
    private final CpuService cpu;
    public CpuController(CpuService cpu) { this.cpu = cpu; }

    // GET /api/cpu/burn?seconds=20&threads=2
    @GetMapping("/cpu/burn")
    public Map<String, Object> burn(@RequestParam(defaultValue = "10") int seconds,
                                    @RequestParam(defaultValue = "1") int threads) throws Exception {
        Map<String, Object> r = new LinkedHashMap<>(cpu.burn(seconds, threads));
        r.put("host", InetAddress.getLocalHost().getHostName());
        return r;
    }

    // GET /api/info  -> which instance/pod/container served the request
    @GetMapping("/info")
    public Map<String, Object> info() throws Exception {
        Runtime rt = Runtime.getRuntime();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("host", InetAddress.getLocalHost().getHostName());
        m.put("processors", rt.availableProcessors());
        m.put("maxMemoryMB", rt.maxMemory() / 1024 / 1024);
        m.put("javaVersion", System.getProperty("java.version"));
        m.put("awsRegion", System.getenv("AWS_REGION"));
        m.put("lambdaFunction", System.getenv("AWS_LAMBDA_FUNCTION_NAME"));
        m.put("k8sNode", System.getenv("NODE_NAME"));
        return m;
    }
}
