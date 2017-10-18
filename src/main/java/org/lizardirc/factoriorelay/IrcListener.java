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
import java.util.Objects;

import org.lizardirc.beancounter.utils.IrcColors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class IrcListener<T extends PircBotX> extends ListenerAdapter<T> {
    private final FifoHandler fifoHandler;

    public IrcListener(Path fifoPath) {
        Objects.requireNonNull(fifoPath);
        fifoHandler = new FifoHandler(fifoPath);
    }

    @Override
    public void onMessage(MessageEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getMessage());

        fifoHandler.write("[IRC] (" + channel + ") <" + user + "> " + message);
    }

    @Override
    public void onAction(ActionEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getMessage());

        fifoHandler.write("[IRC] (" + channel + ") * " + user + ' ' + message);
    }

    @Override
    public void onJoin(JoinEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();

        fifoHandler.write("[IRC] " + user + " joined " + channel);
    }

    @Override
    public void onPart(PartEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + user + " parted " + channel + " [" + message + ']');
    }

    @Override
    public void onQuit(QuitEvent<T> event) throws Exception {
        String user = event.getUser().getNick();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + user + " quit [" + message + ']');
    }

    @Override
    public void onNickChange(NickChangeEvent<T> event) throws Exception {
        String oldNick = event.getOldNick();
        String newNick = event.getNewNick();

        fifoHandler.write("[IRC] " + oldNick + " is now known as " + newNick);
    }

    @Override
    public void onKick(KickEvent<T> event) throws Exception {
        String kicker = event.getUser().getNick();
        String victim = event.getRecipient().getNick();
        String channel = event.getChannel().getName();
        String message = prepareMessage(event.getReason());

        fifoHandler.write("[IRC] " + victim + " was kicked from " + channel + " by " + kicker + " [" + message + ']');
    }

    private String prepareMessage(String message) {
        return IrcColors.stripFormatting(message);
    }
}
