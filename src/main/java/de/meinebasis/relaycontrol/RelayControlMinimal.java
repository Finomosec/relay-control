package de.meinebasis.relaycontrol;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.OutputStreamWriter;
import java.util.Properties;

public class RelayControlMinimal {

    /**
     * The relay command can be passed as first argument.
     * If no argument is passed, the command is read from the properties file.
     */
    public static void main(String[] args) throws Exception {
        final var classLoader = RelayControlMinimal.class.getClassLoader();
        final var properties = new Properties();
        properties.load(classLoader.getResourceAsStream("RelayControl.properties"));
        final var telnetTimeout = Integer.parseInt(properties.getProperty("telnetTimeout"));
        final var relayIp = properties.getProperty("relayIp");
        final var relayPort = Integer.parseInt(properties.getProperty("relayPort"));
        var relayCommand = properties.getProperty("relayCommand");
        if (args.length > 0) {
            relayCommand = args[0];
        }
        final var telnet = new TelnetClient();
        try {
            telnet.setConnectTimeout(telnetTimeout);
            telnet.setDefaultTimeout(telnetTimeout);
            telnet.connect(relayIp, relayPort);

            final OutputStreamWriter out = new OutputStreamWriter(telnet.getOutputStream());
            out.write(relayCommand);
            out.flush();
        } finally {
            telnet.disconnect();
        }
    }

}
