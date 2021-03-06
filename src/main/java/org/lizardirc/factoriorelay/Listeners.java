/*
 * LIZARDIRC/FACTORIORELAY
 * By the LizardIRC Development Team (see AUTHORS.txt file)
 *
 * Copyright (C) 2017 by the LizardIRC Development Team. Some rights reserved.
 *
 * License GPLv3+: GNU General Public License version 3 or later (at your choice):
 * <http://gnu.org/licenses/gpl.html>. This is free software: you are free to
 * change and redistribute it at your will provided that your redistribution, with
 * or without modifications, is also licensed under the GNU GPL. (Although not
 * required by the license, we also ask that you attribute us!) There is NO
 * WARRANTY FOR THIS SOFTWARE to the extent permitted by law.
 *
 * Note that this is an official project of the LizardIRC IRC network.  For more
 * information about LizardIRC, please visit our website at
 * <https://www.lizardirc.org>.
 *
 * This project contains code from and components derived from the
 * LizardIRC/Beancounter IRC bot <https://www.lizardirc.org/?page=beancounter>,
 * which is also licensed GNU GPLv3+.
 *
 * This is an open source project. The source Git repositories, which you are
 * welcome to contribute to, can be found here:
 * <https://gerrit.fastlizard4.org/r/gitweb?p=LizardIRC%2FFactorioRelay.git;a=summary>
 * <https://git.fastlizard4.org/gitblit/summary/?r=LizardIRC/FactorioRelay.git>
 *
 * Gerrit Code Review for the project:
 * <https://gerrit.fastlizard4.org/r/#/q/project:LizardIRC/FactorioRelay,n,z>
 *
 * Alternatively, the project source code can be found on the PUBLISH-ONLY mirror
 * on GitHub: <https://github.com/LizardNet/LizardIRC-FactorioRelay>
 *
 * Note: Pull requests and patches submitted to GitHub will be transferred by a
 * developer to Gerrit before they are acted upon.
 */

package org.lizardirc.factoriorelay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

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
    private final Path fifoPath;
    private final Path outfilePath;
    private final ScheduledExecutorService ses;

    public Listeners(ListenerManager<T> listenerManager, Properties properties, Path fifoPath, Path outfilePath, ScheduledExecutorService ses) {
        this.listenerManager = listenerManager;
        this.properties = properties;
        this.fifoPath = fifoPath;
        this.outfilePath = outfilePath;
        this.ses = ses;
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

        ownListeners.add(new IrcListener<>(fifoPath));
        ownListeners.add(new FactorioListener<>(outfilePath, ses));
        ownListeners.forEach(listenerManager::addListener);
    }
}
