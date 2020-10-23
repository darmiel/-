/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.module;

import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.reflections.Reflections;

public class ModuleRegistry {

  private static final String packagePrefix = "";
  private static final Reflections reflections = new Reflections(packagePrefix);

  @Getter
  private static final Set<BotModule> enablabledModules = new HashSet<>();

  public static final String CONSOLE_PREFIX = AnsiColor.ANSI_CYAN_BACKGROUND +
      AnsiColor.ANSI_WHITE + "(MOD)" +
      AnsiColor.ANSI_RESET + " ";

  public static Set<BotModule> findModules() throws IllegalAccessException,
      InstantiationException,
      NoSuchMethodException,
      InvocationTargetException {

    Logger.debug(CONSOLE_PREFIX + "Looking for modules in packages "
        + "prefixed by '" + packagePrefix + "' ...");

    final Set<BotModule> modules = new HashSet<>(); // result

    final Set<Class<?>> classesAnnotation = reflections.getTypesAnnotatedWith(Module.class);
    for (final Class<?> clazz : classesAnnotation) {
      final String name = clazz.getName();

      if (!BotModule.class.isAssignableFrom(clazz)) {
        Logger.warn(CONSOLE_PREFIX + name + " annotated with @Module, "
            + "but it does not extend BotModule");
        continue;
      }

      // create module
      final Constructor<?> constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);

      final BotModule instance = (BotModule) constructor.newInstance();

      // add to result
      modules.add(instance);
    }

    return modules;
  }

  public static void loadModule(BotModule module) {
    // get info
    if (!module.getClass().isAnnotationPresent(Module.class)) {
      Logger.warn(CONSOLE_PREFIX + "Annotation not present.");
      return;
    }

    final Module info = module.getClass().getAnnotation(Module.class);

    final long timerStart = System.currentTimeMillis();

    Logger.info(CONSOLE_PREFIX + "Loading & enabling '" +
        info.name() + " (v." + info.version() + ")' by " + info.author());

    // load and enable
    try {
      // 1. Send load
      module.onLoad();

      // 4. Send enable
      module.onEnable();

      // print success message
      Logger.success(CONSOLE_PREFIX + "Took " + (System.currentTimeMillis() - timerStart) + " ms");

      // successfully enabled
      enablabledModules.add(module);
    } catch (Throwable throwable) {
      Logger.error(CONSOLE_PREFIX + "Took " + (System.currentTimeMillis() - timerStart) + " ms)");
      Logger.error(throwable);
    }
  }

  public static void unloadModule(BotModule module) {
    // get info
    if (!module.getClass().isAnnotationPresent(Module.class)) {
      Logger.error(CONSOLE_PREFIX + "Invalid module while registering.");
      return;
    }

    final Module info = module.getClass().getAnnotation(Module.class);

    final long timerStart = System.currentTimeMillis();
    Logger.info(CONSOLE_PREFIX + "Disabling '" + info.name() + " (v." + info.version() + ")' by " + info.author());

    // load and enable
    try {
      module.onDisable();
      Logger.success(CONSOLE_PREFIX + "Took " + (System.currentTimeMillis() - timerStart) + " ms");
    } catch (Throwable throwable) {
      Logger.error(CONSOLE_PREFIX + "Took " + (System.currentTimeMillis() - timerStart) + " ms)");
      Logger.error(throwable);
    }
  }

  public static void loadModules() throws InvocationTargetException,
      NoSuchMethodException,
      InstantiationException,
      IllegalAccessException {

    findModules().forEach(ModuleRegistry::loadModule);
  }

  public static void unloadModules() throws InvocationTargetException,
      NoSuchMethodException,
      InstantiationException,
      IllegalAccessException {

    findModules().forEach(ModuleRegistry::unloadModule);
  }

  public static void unloadModulesUnsafe() {
    try {
      unloadModules();
    } catch (Throwable ignored) {
    }
  }

}