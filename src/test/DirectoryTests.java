import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryTests {
	public static void main(String[] args) {
		Path path = Paths.get("tokens/StoredCredential");
		System.out.println(path.toAbsolutePath());
	}
}
