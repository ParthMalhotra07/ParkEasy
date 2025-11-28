package com.parking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonStore {

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> List<T> read(String fileName, Class<T> clazz) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return new ArrayList<>();
            }
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