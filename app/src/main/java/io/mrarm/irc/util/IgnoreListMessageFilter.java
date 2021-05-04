package io.mrarm.irc.util;

import android.util.Log;

import java.util.regex.PatternSyntaxException;

import io.mrarm.chatlib.dto.MessageInfo;
import io.mrarm.chatlib.irc.MessageFilter;
import io.mrarm.chatlib.irc.ServerConnectionData;
import io.mrarm.irc.config.ServerConfigData;

public class IgnoreListMessageFilter implements MessageFilter {

    private ServerConfigData mConfig;

    public IgnoreListMessageFilter(ServerConfigData server) {
        mConfig = server;
    }

    private static boolean skipIf(String s, Regex r) {
        if (s == null) return true;     // missing string cannot match
        if (r == null) return false;    // regex does not constrain this rule
        return !r.matcher(s);           // skip this rule if it does NOT match
    }

    @Override
    public boolean filter(ServerConnectionData serverConnectionData, String channel, MessageInfo message) {
        // Always accept messages if there's no list
        if (mConfig.ignoreList == null) return true;
        MessagePrefix s = message.getSender();
        String m = message.getMessage();

        // A message with neither a sender nor a body can't match any rule
        if (s == null && m == null) return true;

        for (ServerConfigData.IgnoreEntry entry : mConfig.ignoreList) {
            try {
                if (!entry.updateRegexes())
                    continue;   // Regex compilation previously failed, don't try again
            } catch(PatternSyntaxException e) {
                // Regex compilation failed, so silently ignore this rule.
                // (User must deal with it in edit-ignore screen.)
                continue;
            }

            /* Treat all strings empty as a special case and ignore this rule;
             * otherwise nothing would be excluded, and this rule would match
             * (and ignore) every message. You can still create a rule with a
             * '*' glob or an empty regex if you really want to match
             * everything. */
            if (    entry.nick == null && entry.user == null
                 && entry.host == null && entry.mesg == null)
                continue;

            if (s!=null) {
                if (skipIf(s.getNick(), entry.nickRegex)) continue;
                if (skipIf(s.getUser(), entry.userRegex)) continue;
                if (skipIf(s.getHost(), entry.hostRegex)) continue;
            }
            if (skipIf(m, entry.mesgRegex)) continue;

            Log.d("IgnoreListMessageFilter", "Ignore message: " + s.getNick() + " " + m);
            return false;
        }
        return true;
    }
}
