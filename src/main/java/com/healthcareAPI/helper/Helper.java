/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.healthcareAPI.helper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The methods in this class are common helpers that can be used across the application
 * 
 * @author Amandha
 * @param <T>
 */
public class Helper<T> {
    
    // Return the next ID of the passed List
    public int getNextId(List<T> list, Function<T, Integer> getIdFunction) {
        if (list.isEmpty()) {
            return 1; // If the list is empty, return 1 as the next ID
        }
        
        int maxId = Integer.MIN_VALUE;

        // Iterate through the list to find the maximum ID
        for (T item : list) {
            int id = getIdFunction.apply(item);
            if (id > maxId) {
                maxId = id;
            }
        }

        // Increment the maximum ID to get the next available ID
        return maxId + 1;
    }
    
    // Return the next ID of the passed Map
    public <K> int getNextId(Map<K, ?> map) {

        if (!(map.keySet().iterator().next() instanceof Integer)) {
            throw new IllegalArgumentException("Key type must be Integer");
        }

        int maxId = map.keySet().stream()
                .mapToInt(key -> (Integer) key)
                .max()
                .orElse(0);

        return maxId + 1;
    }
}
