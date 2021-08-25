package dev.projectg.geyserupdater.common.util;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class ReflectionUtils {

    /**
     * Get the value of a {@link Field} in a {@link Object}
     * @param obj The Object which contains the Field
     * @param fieldClass The expected Class of the Field
     * @param fieldName The name of the Field
     * @param <T> The expected Type of the Field's value
     * @return The value of the field as Type T.
     * @throws IllegalAccessException If the value was inaccessible, regardless of Field#setAccessible(true)
     * @throws NoSuchFieldException If the Object does not contain the Field specified
     * @throws ClassCastException If the Type of the Field found does not match the fieldClass given
     */
    @Nonnull
    public static <T> T getFieldValue(Object obj, Class<T> fieldClass, String fieldName) throws IllegalAccessException, NoSuchFieldException, ClassCastException {
        Field field = obj.getClass().getDeclaredField(fieldName); // get the field from the object
        field.setAccessible(true); // allow getting the field's value regardless of accessibility
        return fieldClass.cast(field.get(obj)); // get the value and cast it to the desired type
    }
}
