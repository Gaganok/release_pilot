package org.task.pilot.handler;

import org.task.pilot.domain.command.Command;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Handles {

  Class<? extends Command<?>> value();

}
