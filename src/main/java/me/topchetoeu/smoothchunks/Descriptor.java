package me.topchetoeu.smoothchunks;

import org.apache.commons.lang3.Validate;

public final class Descriptor<T> implements Cloneable {
    public static interface StringModifier {
        String modify(String original);
    }

    private final T val;
    private final String name;
    private String displayName = null;
    private String author = null;
    private String description = null;

    /**
     * Gets the value this descriptor describes
     */
    public T get() {
        return val;
    }
    /**
     * Gets the author of the described object (null if not specified)
     */
    public String getAuthor() {
        return author;
    }
    /**
     * Gets the display name of the described object (null if not specified)
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * Gets the name of the described object (never null)
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the name of the described object (never null)
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the author, if not specified, will return a default value
     */
    public String getAuthorOrDefault() {
        if (author == null) return "Someone";
        return author;
    }
    /**
     * Gets the display name, if not specified, will return a default value
     */
    public String getDisplayNameOrDefault() {
        if (displayName == null) return name;
        return displayName;
    }
    /**
     * Gets the author, if not specified, will return a default value
     */
    public String getDescriptionOrDefault() {
        if (description == null) return "Something that does stuff (probably).";
        return description;
    }

    /**
     * Sets the author name
     * @param author The new author name (may be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> author(String author) {
        this.author = author;
        return this;
    }
    /**
     * Sets the author name
     * @param modifier The modifier that will be used to gain the new author name value (may not be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> author(StringModifier modifier) {
        Validate.notNull(modifier, "modifier may not be null.");
        this.author = modifier.modify(this.author);
        return this;
    }

    /**
     * Sets the display name
     * @param dn The new display name (may be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> displayName(String dn) {
        this.displayName = dn;
        return this;
    }
    /**
     * Sets the display name
     * @param modifier The modifier that will be used to gain the new display name value (may not be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> displayName(StringModifier modifier) {
        Validate.notNull(modifier, "modifier may not be null.");
        this.displayName = modifier.modify(this.displayName);
        return this;
    }

    public Descriptor<T> clone() {
        return new Descriptor<T>(val, name)
            .author(author)
            .description(description)
            .displayName(displayName);
    }

    /**
     * Sets the description
     * @param desc The new description (may be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> description(String desc) {
        this.description = desc;
        return this;
    }
    /**
     * Sets the description
     * @param modifier The modifier that will be used to gain the new description value (may not be null)
     * @return The instance upon which this method was called
     */
    public Descriptor<T> description(StringModifier modifier) {
        Validate.notNull(modifier, "modifier may not be null.");
        this.description = modifier.modify(this.description);
        return this;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Descriptor && ((Descriptor<?>)other).name == name;
    }

    public Descriptor(T val, String name) {
        Validate.notNull(val, "val may not be null.");
        Validate.notNull(name, "name may not be null.");

        this.val = val;
        this.name = name;

    }
}
