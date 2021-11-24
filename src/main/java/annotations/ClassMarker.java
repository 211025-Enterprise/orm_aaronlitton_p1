package annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassMarker {
    /**
     * @return the name, if given for the table of the database
     */
    String className();
}