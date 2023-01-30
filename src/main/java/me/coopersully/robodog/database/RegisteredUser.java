package me.coopersully.robodog.database;

import me.coopersully.Commons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class RegisteredUser {
    private final @NotNull String id;
    private int type = -1;
    private final @NotNull String name;
    private @Nullable String email;
    private @Nullable String business;
    private int grad_year = -1;
    private final @NotNull String note;

    public RegisteredUser(@NotNull String id, int type, @NotNull String name, @Nullable String email, @Nullable String business, String grad_year, @Nullable String note) {
        // Validate, reformat, and assign user identifier
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("User constructed with a faulty id, \"" + id + "\".");
        }
        this.id = id;

        // Validate, reformat, and assign position type
        if (type < 0 || type > 3) {
            throw new RuntimeException("User constructed with a faulty type " + type);
        }
        this.type = type;

        // Validate, reformat, and assign user's name
        this.name = Commons.formatForSQL(name).toUpperCase();;

        // Validate, reformat, and assign user's school email
        if (email != null) {
            if (!email.contains("@")) {
            throw new RuntimeException("User constructed with a faulty email, \"" + email + "\".");
            }
            this.email = Commons.formatForSQL(email).toLowerCase();
        }

        // Validate, reformat, and assign user's business
        if (business != null) {
            this.business = Commons.formatForSQL(business).toUpperCase();
        }

        // Validate, reformat, and assign user's graduation year
        if (grad_year != null) {
            if (grad_year.length() != 4) {
                throw new RuntimeException("Student constructed with a faulty year, \"" + grad_year + "\".");
            }
            try {
                this.grad_year = Integer.parseInt(grad_year);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Student constructed with a faulty year, \"" + grad_year + "\".");
            }
        }

        this.note = Commons.formatForSQL(Commons.blankIfNull(note));
    }

    public @NotNull String id() {
        return id;
    }

    public int type() {
        return type;
    }

    public @NotNull String name() {
        return name;
    }

    public @Nullable String email() {
        return email;
    }

    public @Nullable String business() {
        return business;
    }

    public int grad_year() {
        return grad_year;
    }

    public @NotNull String note() {
        return note;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RegisteredUser) obj;
        return Objects.equals(this.id, that.id) &&
                this.type == that.type &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.business, that.business) &&
                this.grad_year == that.grad_year &&
                Objects.equals(this.note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, email, business, grad_year, note);
    }

    @Override
    public String toString() {
        return "RegisteredUser[" +
                "id=" + id + ", " +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "email=" + email + ", " +
                "business=" + business + ", " +
                "grad_year=" + grad_year + ", " +
                "note=" + note + ']';
    }

}
