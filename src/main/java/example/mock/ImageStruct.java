package example.mock;

import lombok.Data;

@Data
public class ImageStruct {

	public ImageStruct(String url) {
		this.url = url;
	}

	private String url;
}
