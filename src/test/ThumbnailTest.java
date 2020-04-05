import com.clivelewis.drivefx.driveAPI.DriveCommonService;
import com.google.api.services.drive.model.File;

public class ThumbnailTest {
	public static void main(String[] args) {
		try{
			File file = DriveCommonService.getFileById("1r1vZxxok41HH7-v7AEL-o7NzkiSSG0RM");
			System.out.println(file.getHasThumbnail());
			System.out.println(file.getThumbnailLink());
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
