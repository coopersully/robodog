package me.coopersully.robodog.database;

import me.coopersully.Commons;

@Deprecated
public record Guest(String id, String name, String business, String note) {

    public Guest(String id, String name, String business, String note) {

        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Guest constructed with a faulty id, \"" + id + "\".");
        }
        this.id = id;

        this.name = Commons.formatForSQL(name).toUpperCase();
        this.business = Commons.formatForSQL(business).toUpperCase();
        this.note = Commons.formatForSQL(note);
    }
}
