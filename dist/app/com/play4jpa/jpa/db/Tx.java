package com.play4jpa.jpa.db;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wraps the annotated action in an JPA transaction without a commit.
 *
 * @author Jens (mail@jensjaeger.com)
 */
@With(TxAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tx {
    String value() default "default";

    boolean readOnly() default false;
}
