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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lizardirc.beancounter.utils.IrcColors;

public final class FactorioLogParser {
    private static final String REGEX_TIMESTAMP = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
    private static final String REGEX_JOIN = "\\[JOIN] ([^ ]+) joined the game";
    private static final String REGEX_LEAVE = "\\[LEAVE] ([^ ]+) left the game";
    private static final String REGEX_CHAT = "\\[CHAT] ([^ ]+): (.*)$";
    private static final String REGEX_RESEARCH_START = "mlogger: Started research of \"([^\"]+)\"";
    private static final String REGEX_RESEARCH_FINISH = "mlogger: Research finished for \"([^\"]+)\"";
    private static final String REGEX_PLAYER_RIP = "mlogger: Player ([^ ]+) died(?: due to (.*))?$";
    private static final String REGEX_ROCKET_LAUNCHED = "mlogger: A rocket was launched";

    private static final Pattern PATTERN_JOIN_MESSAGE = Pattern.compile(REGEX_TIMESTAMP + ' ' + REGEX_JOIN);
    private static final Pattern PATTERN_LEAVE_MESSAGE = Pattern.compile(REGEX_TIMESTAMP + ' ' + REGEX_LEAVE);
    private static final Pattern PATTERN_CHAT_MESSAGE = Pattern.compile(REGEX_TIMESTAMP + ' ' + REGEX_CHAT);
    private static final Pattern PATTERN_RESEARCH_START = Pattern.compile(REGEX_RESEARCH_START);
    private static final Pattern PATTERN_RESEARCH_FINISH = Pattern.compile(REGEX_RESEARCH_FINISH);
    private static final Pattern PATTERN_PLAYER_RIP = Pattern.compile(REGEX_PLAYER_RIP);
    private static final Pattern PATTERN_ROCKET_LAUNCHED = Pattern.compile(REGEX_ROCKET_LAUNCHED);

    private FactorioLogParser() {
        throw new IllegalStateException("No instantiation for you!");
    }

    public static String logToIrcMessage(String message) throws NoMatchException {
        Matcher matcher;
        Objects.requireNonNull(message);

        matcher = PATTERN_CHAT_MESSAGE.matcher(message);
        if (matcher.matches()) {
            String username = matcher.group(1);
            String text = matcher.group(2);

            if (!"<server>".equals(username)) {
                return '<' + IrcColors.RED + username + IrcColors.RESET + "> " + text;
            }
        }

        matcher = PATTERN_JOIN_MESSAGE.matcher(message);
        if (matcher.matches()) {
            String username = matcher.group(1);

            return "[" + IrcColors.YELLOW + username + " connected to the LizardNet Factorio server" + IrcColors.RESET + ']';
        }

        matcher = PATTERN_LEAVE_MESSAGE.matcher(message);
        if (matcher.matches()) {
            String username = matcher.group(1);

            return "[" + IrcColors.YELLOW + username + " disconnected from the LizardNet Factorio server" + IrcColors.RESET + ']';
        }

        matcher = PATTERN_RESEARCH_START.matcher(message);
        if (matcher.matches()) {
            String research = matcher.group(1);

            return "[" + IrcColors.CYAN + research + " research started" + IrcColors.RESET + ']';
        }

        matcher = PATTERN_RESEARCH_FINISH.matcher(message);
        if (matcher.matches()) {
            String research = matcher.group(1);

            return "[" + IrcColors.CYAN + research + " research " + IrcColors.UNDERLINE + "completed" + IrcColors.RESET + ']';
        }

        matcher = PATTERN_PLAYER_RIP.matcher(message);
        if (matcher.matches()) {
            String username = matcher.group(1);
            String cause = matcher.group(2);

            if (cause != null) {
                return "[" + IrcColors.RED + username + " was killed by a " + cause + ".  gg rip" + IrcColors.RESET + ']';
            } else {
                return "[" + IrcColors.RED + username + " was killed by unknown causes" + IrcColors.RESET + ']';
            }
        }

        matcher = PATTERN_ROCKET_LAUNCHED.matcher(message);
        if (matcher.matches()) {
             return "[" + IrcColors.CYAN + "A rocket was launched!" + IrcColors.RESET + ']';
        }

        throw new NoMatchException();
    }

    @SuppressWarnings("unused")
    public static class NoMatchException extends Exception {
        public NoMatchException() {
            super();
        }

        public NoMatchException(String message) {
            super(message);
        }

        public NoMatchException(String message, Throwable cause) {
            super(message, cause);
        }

        public NoMatchException(Throwable cause) {
            super(cause);
        }

        protected NoMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
