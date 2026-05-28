public class Main {

    public static void main(String[] args) {
        
        Config config = new Config("config.properties");

        if(config.isTokenExpired()) {
            System.out.println("TOKEN IS EXPIRED, please launch minecraft and then try again.");
            System.exit(1);
        }

        SkinFile skin = new SkinFile();
        while(!skin.setSkin(System.console().readLine("Enter the path to your skin file: ")));

        ApiClient client = new ApiClient();
        try {
            int response = client.uploadSkin(config.getToken(), skin.path, skin.armType());
            if(response == 200) {
                System.out.println("Skin uploaded successfully!");
            } else {
                System.out.println("Failed to upload skin, response code: " + response);
            }
        } catch (Exception e) {
            System.out.println("[!] " + e.getMessage());
            client.printLast();
        }
    }
}
