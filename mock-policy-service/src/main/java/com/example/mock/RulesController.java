package com.example.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple mock controller that serves a JSON rules file with ETag support. It
 * watches the rules file for changes and updates both the in-memory cache and
 * ETag when the file is modified. This allows for live updates to be picked
 * up by connected SDK instances.
 */
@RestController
public class RulesController {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final AtomicReference<String> lastModifiedETag = new AtomicReference<>();
    private static final AtomicReference<String> cache = new AtomicReference<>();
    private static final File rulesFile =
            new File("src/main/resources/rules.json");

    @PostConstruct
    public void init() throws Exception {
        loadRules();
        watchForChanges();
    }

    @GetMapping("/api/v1/rules")
    public String getRules(HttpServletResponse response) throws Exception {
        String clientETag = response.getHeader("If-None-Match");
        String serverETag = lastModifiedETag.get();

        if (serverETag != null && serverETag.equals(clientETag)) {
            response.setStatus(304);
            return null;
        }

        response.setHeader("ETag", serverETag);
        response.setContentType("application/json");
        return cache.get();
    }

    private void loadRules() throws Exception {
        String content = new String(Files.readAllBytes(rulesFile.toPath()));
        cache.set(content);
        lastModifiedETag.set("v-" + rulesFile.lastModified());
    }

    private void watchForChanges() throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = rulesFile.getParentFile().toPath();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (rulesFile.getName().equals(event.context().toString())) {
                            loadRules();
                        }
                    }
                    key.reset();
                } catch (Exception ignore) {}
            }
        }).start();
    }
}
