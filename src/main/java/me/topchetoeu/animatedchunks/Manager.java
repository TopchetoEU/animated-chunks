package me.topchetoeu.animatedchunks;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class Manager<T> {
    public static interface RegisterEvent<T> {
        public void register(Manager<T> manager);
    }
    public static final <T> Event<RegisterEvent<T>> createEvent() {
        return EventFactory.createArrayBacked(RegisterEvent.class, (listeners) -> (manager) -> {
            for (RegisterEvent<T> listener: listeners) {
                listener.register(manager);
            }
        });
    }

    private String currName = null;
    private Map<String, Descriptor<T>> objects = new Hashtable<>();

    /**
     * Registers an object (throws if an object of the same name already exists)
     * @param obj The object to register (may not be null)
     */
    public void register(Descriptor<? extends T> obj) {
        Validate.notNull(obj, "obj may not be null.");

        if (objects.containsKey(obj.getName())) throw new RuntimeException("The ease %s already exists.".formatted(obj.getName()));
        objects.put(obj.getName(), new Descriptor<T>(obj.get(), obj.getName())
            .displayName(obj.getDisplayName())
            .author(obj.getAuthor())
            .description(obj.getDescription())
        );
    }
    
    /**
     * Gets an object by its name (null if such an object isn't registered)
     */
    public Descriptor<T> get(String name) {
        return objects.get(name);
    }
    /**
     * Gets the currently used object's descriptor (never null)
     */
    public Descriptor<T> get() {
        return objects.get(currName);
    }
    /**
     * Gets the currently used object (never null)
     */
    public T getValue() {
        return objects.get(currName).get();
    }
    /**
     * Set the currently used object by its name
     * @param name The name of the animation to use (throws if the name doesn't correspond to a registered animation)
     */
    public void set(String name) {
        Validate.notNull(name, "name may not be null.");
        if (!objects.containsKey(name)) throw new RuntimeException("The ease %s doesn't exist.".formatted(name));
        this.currName = name;
    }

    /**
     * Gets all the currently registered objects
     */
    public Collection<Descriptor<T>> getAll() {
        return Collections.unmodifiableCollection(objects.values());
    }

    public Manager(T _default) {
        register(new Descriptor<>(_default, "default").displayName("Default").author("TopchetoEU"));
        set("default");
    }
}
