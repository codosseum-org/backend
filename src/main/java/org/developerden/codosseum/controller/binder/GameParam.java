package org.developerden.codosseum.controller.binder;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface GameParam {
    String value() default "id"; // The path variable name containing the UUID
}