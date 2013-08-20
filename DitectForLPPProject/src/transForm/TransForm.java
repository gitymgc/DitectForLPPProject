package transForm;

import java.awt.image.BufferedImage;

public class TransForm {

	public static void exec(BufferedImage srcImg,int src2d[][] )throws Exception{
		
		int w = srcImg.getWidth();
		int h = srcImg.getHeight();
		
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x ++){
				int argb = srcImg.getRGB(x, y);
				src2d[y][x] = (int)(
						0.3 * (argb >> 16 & 0xff) +
						0.6 * (argb >> 8 & 0xff) +
						0.1 * (argb & 0xff));
			}
		}
	}
}
