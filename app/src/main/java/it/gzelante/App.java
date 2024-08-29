package it.gzelante;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import lombok.extern.log4j.Log4j2;
@Log4j2
public class App {

    /**
     * Identifies and terminates potentially unsafe processes based on system process standards to maintain system stability,
     * using runtime commands and process analysis.
     *
     * @param args command line arguments (currently unused)
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] command = {"tasklist", "/v", "/fo", "csv"};
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            if(i != 0) {
                String[] fields = line.split(",");

                String imageName = fields[0].trim();

                if (isSafeToKill(fields)) {
                    String pid = fields[1].trim();
                    String[] killCommand = {"taskkill", "/F", "/PID", pid};
                    log.info(String.join(" ", killCommand)+" "+imageName);
                    Runtime.getRuntime().exec(killCommand);
                }

            }
            i++;
        }
    }
    /**
     * Decides if a process is safe to kill based on its name and type.
     * A process is considered safe to kill if it is not a service and if its name does not contain any of the strings returned by {@link #getSystemProcesses()}.
     * @param processInfo the information about the process, as returned by the "tasklist /v /fo csv" command
     * @return true if the process is safe to kill, false otherwise
     */
    private static boolean isSafeToKill(String[] processInfo) {
        return !processInfo[2].trim().toUpperCase().contains("SERVICE") && Arrays.stream(getSystemProcesses()).noneMatch(i -> processInfo[0].trim().toUpperCase().contains(i.toUpperCase()));
    }

    /**
     * Returns a list of process names that are considered safe to kill.
     *
     * @return a list of process names
     */
    private static final String[] getSystemProcesses() {
        return new String[]{
            "svchost", "explorer", "system", "wininit", "smss", "csrss", "winlogon", "services", "lsass", "lsm", "code", "devenv", "wsl", "java", "gradle", "explorer", "PCBooster", "cmd", "powershell"
        };
    }
}
