package it.gzelante;

import java.util.Arrays;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class App {

    public static void main(String[] args) {
        ProcessHandle.allProcesses()
            .filter(App::isSafeToKill)
            .forEach(process -> {
                long pid = process.pid();
                String command = process.info().command().orElse("Unknown");
                log.info("Terminating process: PID {} - {}", pid, command);
                process.destroyForcibly();
            });
    }

    private static boolean isSafeToKill(ProcessHandle process) {
        String command = process.info().command().orElse("").toLowerCase();
        return !command.contains("service") &&
               Arrays.stream(getSystemProcesses()).noneMatch(command::contains);
    }

    private static String[] getSystemProcesses() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] windowsProcesses = {
            "svchost", "explorer", "system", "wininit", "smss", "csrss", "winlogon",
            "services", "lsass", "lsm", "devenv", "wsl", "cmd", "powershell"
        };
        String[] macProcesses = {
            "launchd", "kernel_task", "WindowServer", "loginwindow", "Finder",
            "SystemUIServer", "Dock", "Spotlight", "coreaudiod", "mds"
        };
        String[] linuxProcesses = {
            "systemd", "init", "kthreadd", "kworker", "rcu_sched", "bash",
            "sshd", "dbus-daemon", "NetworkManager", "rsyslogd"
        };
        // Common non-OS related processes
        String[] commonProcesses = {"java", "gradle"};

        String[] osSpecificProcesses;
        if (os.contains("win")) {
            osSpecificProcesses = windowsProcesses;
        } else if (os.contains("mac")) {
            osSpecificProcesses = macProcesses;
        } else if (os.contains("nux") || os.contains("nix") || os.contains("aix")) {
            osSpecificProcesses = linuxProcesses;
        } else {
            // Default to an empty array if OS is not recognized
            osSpecificProcesses = new String[0];
        }

        // Combine OS-specific processes with common processes
        String[] result = new String[osSpecificProcesses.length + commonProcesses.length];
        System.arraycopy(osSpecificProcesses, 0, result, 0, osSpecificProcesses.length);
        System.arraycopy(commonProcesses, 0, result, osSpecificProcesses.length, commonProcesses.length);

        return result;
    }
}
