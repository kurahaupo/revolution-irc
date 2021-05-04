package io.mrarm.irc.config;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.mrarm.irc.R;

public class ServerConfigData {

    private static final String AUTH_LEGACY_PASSWORD = "password";
    public static final String AUTH_SASL = "sasl";
    public static final String AUTH_SASL_EXTERNAL = "sasl_external";

    public String name;
    public UUID uuid;

    public String address;
    public int port;
    public boolean ssl;
    public String charset;
    public String pass;
    public String authMode;
    public String authUser;
    public String authPass;
    public byte[] authCertData;
    public byte[] authCertPrivKey;
    public String authCertPrivKeyType;

    public List<String> nicks;
    public String user;
    public String realname;

    public List<String> autojoinChannels;
    public boolean rejoinChannels = true;
    public List<String> execCommandsConnected;

    public List<IgnoreEntry> ignoreList;

    public long storageLimit;

    public void migrateLegacyProperties() {
        if (authMode != null && authMode.equals(AUTH_LEGACY_PASSWORD)) {
            pass = authPass;
            authPass = null;
            authMode = null;
        }
    }

    public X509Certificate getAuthCert() {
        if (authCertData == null)
            return null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(
                    new ByteArrayInputStream(authCertData));
        } catch (CertificateException e) {
            Log.e("ServerConfigData", "Failed to load cert data");
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getAuthPrivateKey() {
        if (authCertPrivKey == null)
            return null;
        try {
            KeyFactory factory = KeyFactory.getInstance(authCertPrivKeyType);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(authCertPrivKey);
            return factory.generatePrivate(keySpec);
        } catch (GeneralSecurityException e) {
            Log.w("ServerConfigData", "Failed to load private key");
            e.printStackTrace();
        }
        return null;
    }

    public static class IgnoreEntry {

        public String nick;
        public String user;
        public String host;
        public String mesg;
        public String comment;
        public transient boolean compiled = false;
        public transient Pattern nickRegex;
        public transient Pattern userRegex;
        public transient Pattern hostRegex;
        public transient Pattern mesgRegex;
        private transient boolean valid = false;

        public boolean matchDirectMessages = true;
        public boolean matchDirectNotices = true;
        public boolean matchChannelMessages = true;
        public boolean matchChannelNotices = true;

        private static Pattern cp(String str) throws PatternSyntaxException {
            if (str == null || str.isEmpty())
                return null;
            if (    str.startsWith("/") && str.endsWith("/")
                 || str.startsWith("?") && str.endsWith("?")) {
                int l = str.length();
                if (l > 2)
                    return Pattern.compile(str.substr(1,l-1));
                if (l == 2)
                    return null;    // treat "//" and "??" as 'match anything'
            }
            // fall thru if pattern is "/" or "?"
            return SimpleWildcardPattern.compile(str);
        }

        public boolean updateRegexes() throws PatternSyntaxException {
            if (compiled)
                return valid;
            compiled = true;
            valid = false;
            nickRegex = cp(nick);
            userRegex = cp(user);
            hostRegex = cp(host);
            mesgRegex = cp(mesg);
            valid = true;
            return valid;
        }

    }

}
