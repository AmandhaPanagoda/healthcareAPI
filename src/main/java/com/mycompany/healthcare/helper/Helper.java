/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.helper;

import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Amandha
 * @param <T>
 */
public class Helper<T> {
    public int getNextId(List<T> list, Function<T, Integer> getIdFunction) {
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
}

