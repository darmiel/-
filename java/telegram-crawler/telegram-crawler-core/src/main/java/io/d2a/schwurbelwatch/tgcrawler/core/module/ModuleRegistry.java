/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core.module;

import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.BotMain;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

public class ModuleRegistry {

  private static final Map<String, BotModule> moduleMap = new HashMap<>();
  private static final Reflections reflections = new Reflections("");
  private static final Set<BotModule> enablabledModules = new HashSet<>();

  public static Set<BotModule> findModules() throws IllegalAccessException,
      InstantiationException,
      NoSuchMethodException,
      InvocationTargetException {

    System.out.println("  >>> Finding modules");

    final Set<BotModule> modules = new HashSet<>();
    final Set<Class<?>> classesAnnotation = reflections.getTypesAnnotatedWith(Module.class);

    for (final Class<?> clazz : classesAnnotation) {
      final String name = clazz.getName();
      System.out.println("  >>> + " + name);

      if (!BotModule.class.isAssignableFrom(clazz)) {
        System.out.println("[MOD] Warning: Module " + name
            + " annotated with @Module, but it does not extend BotModule");
        continue;
      }

      System.out.println("[MOD] Found " + name);

      // check if already initiated
      if (moduleMap.containsKey(name)) {
        modules.add(moduleMap.get(name));
        System.out.println("[MOD]  -> from cache");
        continue;
      }

      // create module
      final Constructor<?> constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);

      final BotModule instance = (BotModule) constructor.newInstance();

      // add to result
      modules.add(instance);

      // add to module map
      ModuleRegistry.moduleMap.put(name, instance);

      System.out.println("[MOD]  -> from instance");
    }

    return modules;
  }

  private static void removeFromLoaded(BotModule module) {
    enablabledModules.remove(module);
    enablabledModules.removeIf(mod -> mod.getClass().getName().equals(module.getClass().getName()));
  }

  public static void loadModule(BotModule module) {
    // get info
    if (!module.getClass().isAnnotationPresent(Module.class)) {
      System.out.println("[MOD] Invalid module while registering.");
      return;
    }

    removeFromLoaded(module);

    final Module info = module.getClass().getAnnotation(Module.class);

    long timerStart = System.currentTimeMillis();
    System.out.println("» Enabling and loading module '" + info.name() + " v." + info.version() + "' by " + info.author());

    // load and enable
    try {
      // 1. Send load
      module.onLoad();

      // 2. Find clients
      final Set<TelegramClient> clients = BotMain.getClientRouter().findClients(info.name());
      module.onClientLoad(clients);

      // 3. Update clients
      module.loadClients(clients);

      // 4. Send enable
      module.onEnable();

      System.out.println("  » Done [successful]. (Took " + (System.currentTimeMillis() - timerStart) + " ms)");

      // successfully enabled
      enablabledModules.add(module);
    } catch (Throwable throwable) {
      System.out.println("  » Done [error, see stacktrace]. (Took " + (System.currentTimeMillis() - timerStart) + " ms)");
      throwable.printStackTrace();
    }
  }

  public static void unloadModule(BotModule module) {
    // get info
    if (!module.getClass().isAnnotationPresent(Module.class)) {
      System.out.println("[MOD] Invalid module while registering.");
      return;
    }

    removeFromLoaded(module);

    final Module info = module.getClass().getAnnotation(Module.class);

    long timerStart = System.currentTimeMillis();
    System.out.println("» Disabling module '" + info.name() + " v." + info.version() + "' by " + info.author());

    // load and enable
    try {
      module.onDisable();
      System.out.println("  » Done [successful]. (Took " + (System.currentTimeMillis() - timerStart) + " ms)");
    } catch (Throwable throwable) {
      System.out.println("  » Done [error, see stacktrace]. (Took " + (System.currentTimeMillis() - timerStart) + " ms)");
      throwable.printStackTrace();
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