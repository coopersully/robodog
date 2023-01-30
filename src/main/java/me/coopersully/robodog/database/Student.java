package me.coopersully.robodog.database;

import me.coopersully.Commons;

@Deprecated
public record Student(String id, String name, String email, String year, String note) {

    public Student(String id, String name, String email, String year, String note) {

        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Student constructed with a faulty id, \"" + id + "\".");
        }
        this.id = id;

        this.name = Commons.formatForSQL(name).toUpperCase();

        if (!email.contains("@")) {
            throw new RuntimeException("ServerMember constructed with a faulty email, \"" + email + "\".");
        }
        this.email = Commons.formatForSQL(email).toLowerCase();

        try {
            Integer.parseInt(year);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Student constructed with a faulty year, \"" + year + "\".");
        }
        if (year.length() == 2) year = "20" + year;
        if (year.length() != 4) {
            throw new RuntimeException("Student constructed with a faulty year, \"" + year + "\".");
        }
        this.year = year;
        this.note = Commons.formatForSQL(note);
    }
}
