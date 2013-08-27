package image.filter;

import java.awt.image.BufferedImage;

public class LaplacianFilter {

	public static void exec(BufferedImage srcImg,int gra2d[][],int dst2d[][],int mSize) throws Exception{

		int w = srcImg.getWidth();
		int h = srcImg.getHeight();

		//オペレータ作成
		int ope[][] = {
				{-1, -1, -1},
				{-1,  8, -1},
				{-1, -1, -1}
		}; 

		//局所処理
		int mh = mSize/2;

		for(int y = mh; y <h-mh; y++){
			for(int x = mh; x < w-mh; x++){
				int tmp = 0;
				for(int my = -mh; my <=mh; my++){
					for(int mx = -mh; mx <=mh; mx++){
						tmp += gra2d[y+my][x+mx] * ope[my+mh][mx+mh];
					}
				}
				dst2d[y][x] = tmp;
			}
		}

	}
}
