package com.parking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonStore {

    private final ObjectMapper mapper;

    public JsonStore() {
        this.mapper = new ObjectMapper();
        // FIXED: This line allows Java to read/write Time correctly
        this.mapper.registerModule(new JavaTimeModule());
        // FIXED: This forces time to look like "2025-11-26T10:00:00" instead of numbers
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> List<T> read(String fileName, Class<T> clazz) {
        try {
            File file = new File(fileName);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public <T> void write(String fileName, List<T> data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}