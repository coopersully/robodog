package me.coopersully.robodog.events.forms;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GenericButtonPressed extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        System.out.println("Button with an id of " + event.getButton().getId() + " was pressed.");
    }


}
