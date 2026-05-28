import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SkinFile {

    String path;
    boolean isSlim;

    public String armType() {
        return isSlim ? "slim" : "classic";
    }

    public boolean setSkin(String skinPath) {

        skinPath = skinPath.replace("~", System.getProperty("user.home")); //I love linux

        if (!skinPath.endsWith(".png")) {
            System.out.println("[!] File must be a .png");
            return false;
        }

        File file = new File(skinPath);
        if (!file.exists()) {
            System.out.println("[!] File does not exist");
            return false;
        }

        try {
            BufferedImage img = ImageIO.read(file);

            // Verify dimensions
            if (img.getWidth() != 64 || img.getHeight() != 64) {
                System.out.println("[!] Skin must be 64x64 pixels");
                return false;
            }

            // Check the alpha value of the pixel at (54, 20) to determine if the skin is slim or classic
            int argb = img.getRGB(54, 20);
            int alpha = (argb >> 24) & 0xFF;
            isSlim = alpha != 255;

            // Confirm skin type
            String response = System.console().readLine("It looks like this skin is designed to have " + armType() + " arms. Is this correct? (Y/IDK/N): ");
            if (response.equalsIgnoreCase("N")) isSlim = !isSlim;
            System.out.println("Selecting " + armType() + " arms...");

        } catch (Exception e) {
            System.out.println("[!] Failed to read image: " + e.getMessage());
            return false;
        }

        path = skinPath;
        return true;
    }
}
