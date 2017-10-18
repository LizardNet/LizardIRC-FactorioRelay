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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

public class FactorioListener<T extends PircBotX> extends ListenerAdapter<T> {
    private final Path outfilePath;
    private final ScheduledExecutorService ses;

    private T bot;
    private OutfileWatcher watcher = null;
    private Thread watcherThread = null;

    public FactorioListener(Path outfilePath, ScheduledExecutorService ses) {
        this.outfilePath = Objects.requireNonNull(outfilePath);
        this.ses = Objects.requireNonNull(ses);
    }

    @Override
    public void onConnect(ConnectEvent<T> event) throws Exception {
        bot = event.getBot();
        startWatcherThread();
    }

    private void startWatcherThread() {
        watcher = new OutfileWatcher<>(this, outfilePath);
        watcherThread = new Thread(watcher);
        watcherThread.setDaemon(true);
        watcherThread.start();

        System.err.println("Started outfile watcher thread");
    }

    void signalError() {
        System.err.println("An error occurred in the outfile watcher thread; scheduling a restart in 1 minute.");

        watcher.interrupt();
        watcherThread.interrupt();
        watcher = null;
        watcherThread = null;
        ses.schedule(this::startWatcherThread, 1L, TimeUnit.MINUTES);
    }

    void processLogLines(List<String> lines) {
        for (String line : lines) {
            try {
                sendTextToAllChannels(FactorioLogParser.logToIrcMessage(line));
            } catch (FactorioLogParser.NoMatchException e) {
                // Go nowhere, do nothing
            }
        }
    }

    private void sendTextToAllChannels(String text) {
        bot.getUserBot().getChannels()
            .forEach(ch -> ch.send().message(text));
    }
}
