import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private String properties; // path to the properties file
    private String accountsPath; // path to accounts file
    private String accountsUsername; // username of the account to change skin for
    private int launcher; // which launcher? account files are formatted differently
    /*
    1: Minecraft Launcher
    2: Prism Launcher
     */
    static final int LAUNCHER_MAX = 2;
    // TO-DO: in future, enumerate launcher so config reflects choice

    public Config(String propertiesFile) {
        properties = propertiesFile;
        if (loadProperties(propertiesFile)) {
            if (checkUsernameExists(accountsUsername)) {
                System.out.println("Properties file loaded successfully!");
                return;
            }
            else {
                System.out.print("[!] Invalid properties file. ");
            }
        }
        else {
            System.out.print("[!] No properties file found. ");
        }
        System.out.println("Creating a new one...");
        createProperties(propertiesFile);
    }

    private boolean loadProperties(String propertiesFile) {
        try {
            // Find file
            Properties props = new Properties();
            props.load(Files.newInputStream(Path.of(propertiesFile)));

            // Read values
            accountsPath = props.getProperty("accountsPath");
            accountsUsername = props.getProperty("accountsUsername");
            launcher = Integer.parseInt(props.getProperty("launcher"));

            return true; // File exists and was loaded successfully
        }
        catch (Exception e) {
            return false; // File could not be loaded
        }
    }

    private void createProperties(String fileName) {

        boolean pass;

        // Select launcher
        do {
            pass = true;

            // Take input
            try {
                System.out.println("1: Minecraft Launcher");
                System.out.println("2: Prism Launcher");
                launcher = Integer.parseInt(System.console().readLine("Which launcher do you use? (see options above): "));
            } catch (NumberFormatException e) {
                launcher = 0;
            }
            
            if (launcher > LAUNCHER_MAX || launcher < 1) {
                System.out.println("[!] Invalid selection");
                pass = false;
            }

        } while (!pass);

        // Handle different launcher types
        String expected = "IF EVERYTHING GOES RIGHT WITH LAUNCHER SELECTION THIS SHOULD NEVER BE SEEN";
        switch (launcher) {
            case 1 -> expected = "launcher_accounts.json"; // Minecraft Launcher
            case 2 -> expected = "accounts.json"; // Prism Launcher
        }
        createHelperAccountsPath(expected); // Get accounts file path
        createHelperAccountsUsername(); //get account username
        saveProperties(); // Save properties file
    }

    private void createHelperAccountsPath(String expected) {
        
        boolean pass;

        // Get accounts file path
        do {
            pass = false;

            System.out.print("Please enter the path to " + expected + ": ");
            accountsPath = System.console().readLine();

            if (!accountsPath.endsWith(".json")) {
                System.out.println("[!] Please select a .json file");
            }
            else if (!new java.io.File(accountsPath).exists()) {
                System.out.println("[!] File does not exist");
            }
            else { pass = true; }

        } while (!pass);
    }

    private void createHelperAccountsUsername() {
        boolean passiveAggressiveMode = false;
        do {
            if(!passiveAggressiveMode) { System.out.print("Please enter account username: "); }
            else { System.out.print("Please enter VALID account username: "); }
            accountsUsername = System.console().readLine();
            passiveAggressiveMode = true;
        } while (!checkUsernameExists(accountsUsername));
    }

    private void saveProperties() {
        try {
            Properties props = new Properties();
            props.setProperty("accountsPath", accountsPath);
            props.setProperty("accountsUsername", accountsUsername);
            props.setProperty("launcher", Integer.toString(launcher));
            props.store(Files.newOutputStream(Path.of(properties)), null);
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private boolean checkUsernameExists(String username) {
        try {
            String accountsFile = Files.readString(Path.of(accountsPath));
            return accountsFile.contains(username);
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
            return false;
        }
    }

    public boolean isTokenExpired() {
        try {
            String json = Files.readString(Path.of(accountsPath));

            int nameIdx = json.indexOf("\"" + accountsUsername + "\"");
            int yggIdx = json.indexOf("\"ygg\"", nameIdx);
            int expIdx = json.indexOf("\"exp\"", yggIdx);
            int start = json.indexOf(":", expIdx) + 1;
            while (json.charAt(start) == ' ') start++;
            int end = start;
            while (Character.isDigit(json.charAt(end))) end++;

            long exp = Long.parseLong(json.substring(start, end));
            return System.currentTimeMillis() / 1000 > exp;

        } catch (Exception e) {
            return true; // assume expired if we can't check
        }
    }

    public String getToken() {

        switch (launcher) {
            
            // Minecraft Launcher
            case 1 -> {
                System.out.println("MINECRAFT LAUNCHER NOT IMPLEMENTED YET");
                System.exit(1);
                return "bruh";
            }

            // Prism Launcher
            case 2 -> {
                try {
                    String json = Files.readString(Path.of(accountsPath));

                    int nameIdx = json.indexOf("\"" + accountsUsername + "\"");
                    int yggIdx = json.indexOf("\"ygg\"", nameIdx);
                    int tokenIdx = json.indexOf("\"token\"", yggIdx);
                    int start = json.indexOf("\"", tokenIdx + 7) + 1;
                    int end = json.indexOf("\"", start);

                    return json.substring(start, end);
                } catch (Exception e) {
                    System.out.println("[!] Failed to read token: " + e.getMessage());
                    System.exit(1);
                    return null;
                }
            }

            default -> {
                System.out.println("[!] Invalid launcher selection in config");
                System.exit(1);
                return "bruh";
            }
        }
    }
}
