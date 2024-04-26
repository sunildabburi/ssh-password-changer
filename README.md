# ssh-password-changer
A simple utility to change ssh password for a host.

This utility is to change password for a host where a user exists and have logged in before with current password, as it uses `passwd` command to change the password.

However, if the user wants to change the password first time they logged in, they could comment out `channel.setCommand("passwd");` in [SSHPasswordChanger](src/main/java/com/dabburi/SSHPasswordChanger.java) and run the program.

## Usage
Run [SSHPasswordChanger](src/main/java/com/dabburi/SSHPasswordChanger.java) with 4 parameters `<host> <username> <current-password> <new-password>`
