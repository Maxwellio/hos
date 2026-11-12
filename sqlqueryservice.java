package com.example.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SqlQueryService {
    private final Map<String, String> queryCache = new ConcurrentHashMap<>();

    public String getQuery(String queryName) {
        return queryCache.computeIfAbsent(queryName, key -> {
            try (InputStream stream = getClass().getResourceAsStream("/sql/"+queryName+".sql")){
                if (stream == null){
                    throw new FileNotFoundException("Sql file not found:" + queryName);
                }
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
            catch (IOException e){
                throw new RuntimeException("Failed to load sql:" + queryName, e);
            }
        });
    }
}
