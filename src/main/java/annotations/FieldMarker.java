package annotations;

import java.lang.annotation.*;

/**
 * Annotations for the variables of a class
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMarker {
    /**
     * @return The name of the column in the database
     */
    String columnName();

    /**
     * This field should only be assigned to numbers as of right now
     * @return The integer value of the ID for the computer
     */
    boolean isKey() default false;
}
