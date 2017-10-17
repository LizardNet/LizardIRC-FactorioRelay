package org.lizardirc.factoriorelay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.lizardirc.beancounter.SetModesOnConnectListener;
import org.lizardirc.beancounter.hooks.CommandHandler;
import org.lizardirc.beancounter.hooks.CommandListener;
import org.lizardirc.beancounter.hooks.Fantasy;
import org.lizardirc.beancounter.hooks.MultiCommandHandler;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;

public class Listeners<T extends PircBotX> {
    private final Set<Listener<T>> ownListeners = new HashSet<>();

    private final ListenerManager<T> listenerManager;
    private final Properties properties;

    public Listeners(ListenerManager<T> listenerManager, Properties properties) {
        this.listenerManager = listenerManager;
        this.properties = properties;
    }

    public void register() {
        String fantasyString = properties.getProperty("fantasyString", "?");
        String modesOnConnect = properties.getProperty("autoModes", "");

        List<CommandHandler<T>> handlers = new ArrayList<>();

        MultiCommandHandler<T> commands = new MultiCommandHandler<>(handlers);
        ownListeners.add(new Fantasy<>(new CommandListener<>(commands), fantasyString));
        if (!modesOnConnect.isEmpty()) {
            ownListeners.add(new SetModesOnConnectListener<>(modesOnConnect));
        }
        ownListeners.forEach(listenerManager::addListener);
    }
}
