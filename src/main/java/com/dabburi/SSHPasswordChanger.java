package com.dabburi;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * A simple utility to change ssh password for a host.
 * <br/><br/>
 * This utility is to change password for a host where a user exists and have logged in before with current password,
 * as it uses <b>passwd</b> command to change the password.
 * <br/><br/>
 * However, if the user wants to change the password first time they logged in, they could comment out
 * <b>channel.setCommand("passwd");</b> and run the program.
 */
public class SSHPasswordChanger {

    public static void main(String[] args) throws IOException, InterruptedException, JSchException {
        if (args.length != 4) {
            System.err.println("Expected 4 parameters. <host> <username> <current-password> <new-password>");
            return;
        }
        final String host = args[0];
        final String user = args[1];
        final String currentPassword = args[2];
        final String newPassword = args[3];
        changePassword(host, user, currentPassword, newPassword);
    }

    private static void changePassword(final String host, final String user, final String currentPassword,
                                       final String newPassword) throws IOException, InterruptedException, JSchException {
        JSch jsch = new JSch();
        PrintStream printStream = null;
        try {
            final Session session = jsch.getSession(user, host, 22);
            session.setPassword(currentPassword);
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            final ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("passwd");
            channel.setPty(true);

            final OutputStream out = channel.getOutputStream();
            final InputStream in = channel.getInputStream();
            printStream = new PrintStream(out, true);

            channel.connect();

            byte[] buffer = new byte[1024];
            int length;
            final StringBuilder output = new StringBuilder();
            while (true) {
                while (in.available() > 0 && (length = in.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, length));
                }

                if (output.toString().contains("Old password:")) {
                    printStream.println(currentPassword);
                    output.setLength(0);
                } else if (output.toString().contains("New password:")) {
                    printStream.println(newPassword);
                    output.setLength(0);
                } else if (output.toString().contains("Re-enter")) {
                    printStream.println(newPassword);
                    System.out.println();
                    break;
                }

                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    break;
                }
                Thread.sleep(100);
            }

            channel.disconnect();
            session.disconnect();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }
}