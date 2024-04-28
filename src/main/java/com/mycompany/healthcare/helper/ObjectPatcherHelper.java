/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.healthcare.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for patching objects by copying non-null values from one object
 * to another.
 *
 * @author Amandha
 */
public class ObjectPatcherHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectPatcherHelper.class);

    /**
     * Copies non-null values from the fields of an incomplete object to the
     * corresponding fields of an existing object.
     *
     * @param <T> The type of the objects being patched.
     * @param existingObject The existing object to be patched.
     * @param incompleteObject The incomplete object containing values to be
     * copied.
     * @throws IllegalAccessException If an error occurs while accessing the
     * fields of the objects.
     */
    public static <T> void objectPatcher(T existingObject, T incompleteObject) throws IllegalAccessException {
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = existingObject.getClass();

        // traverse class hierarchy to include inherited fields
        while (currentClass != null && currentClass != Object.class) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                allFields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }

        LOGGER.debug("Loop through all fields of the object class hierarchy");
        for (Field field : allFields) {
            Object value = field.get(incompleteObject);
            LOGGER.debug("Field {} , New value: {}", field.getName(), value);
            if (value != null) {
                Object oldValue = field.get(existingObject); // Get the old value before updating
                LOGGER.debug("Updating field {}. Old value: {}, New value: {}", field.getName(), oldValue, value);
                field.set(existingObject, value); // Set the new value of the changed property
            }
            field.setAccessible(false);
        }
    }

}
