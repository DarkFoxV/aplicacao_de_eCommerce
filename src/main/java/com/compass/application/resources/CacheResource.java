package com.compass.application.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/caches")
public class CacheResource {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/{cacheName}")
    public ResponseEntity<Object> getCacheContent(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Map<Object, Object> content = new HashMap<>();
            ((Map<Object, Object>) cache.getNativeCache()).keySet().forEach(key -> {
                content.put(key, ((Map<Object, Object>) cache.getNativeCache()).get(key));
            });
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}