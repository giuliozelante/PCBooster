package it.gzelante;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import lombok.extern.log4j.Log4j2;
@Log4j2
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Get a list of running processes
        String[] command = {"tasklist", "/v", "/fo", "csv"};
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Parse the output to identify potential candidates for termination
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            if(i != 0) {
                String[] fields = line.split(",");

                // Basic filtering based on image name (highly unreliable)
                String imageName = fields[0].trim();

                // Further analysis required to determine if the process is safe to kill
                // This would involve complex logic based on process description, handle, PID, etc.
                if (isSafeToKill(fields)) {
                    // Kill the process
                    String pid = fields[1].trim();
                    String[] killCommand = {"taskkill", "/F", "/PID", pid};
                    log.info(String.join(" ", killCommand)+" "+imageName);
                    Runtime.getRuntime().exec(killCommand);
                }

            }
            // Parse the CSV line to extract process information
            i++;
        }
    }
    private static boolean isSafeToKill(String[] processInfo) {
        return !processInfo[2].trim().toUpperCase().contains("SERVICE") && Arrays.stream(getSystemProcesses()).noneMatch(i -> processInfo[0].trim().toUpperCase().contains(i.toUpperCase()));
    }

    private static final String[] getSystemProcesses() {
        return new String[]{
            "svchost", "explorer", "system", "wininit", "smss", "csrss", "winlogon", "services", "lsass", "lsm", "code", "devenv", "wsl", "java", "gradle", "explorer"
        };
    }
}
