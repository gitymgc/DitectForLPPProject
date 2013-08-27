package main;

import java.awt.image.BufferedImage;

public class LaplacianFilterForThis {

	public static void exec(BufferedImage srcImg,int smth2d[][],int edge2d[][],int mSize) throws Exception{

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

		int dst2d[][] = new int[h][w];

		for(int y = mh; y <h-mh; y++){
			for(int x = mh; x < w-mh; x++){
				int tmp = 0;
				for(int my = -mh; my <=mh; my++){
					for(int mx = -mh; mx <=mh; mx++){
						tmp += smth2d[y+my][x+mx] * ope[my+mh][mx+mh];
					}
				}
				if(tmp > smth2d[y][x]){
					dst2d[y][x] = 1;
				}else{
					dst2d[y][x] = 0;
				}
				//				dst2d[y][x] = tmp;
			}
		}

		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				edge2d[y][x] = dst2d[y][x];
			}
		}

		//正規化
		//		int max = Integer.MIN_VALUE;
		//		int min = Integer.MAX_VALUE;
		//		int sum = 0;
		//		int ave = 0;
		//
		//		for(int y = 0; y < h; y ++){
		//			for(int x = 0; x < w; x++){
		//				if(dst2d[y][x] < min)
		//					min = dst2d[y][x];
		//				if(dst2d[y][x] > max)
		//					max = dst2d[y][x];
		//			}
		//		}
		//		
		//		int nrm2d[][] = new int[h][w];
		//		for(int y = 0; y < h; y++){
		//			for(int x = 0; x < w; x++){
		//				nrm2d[y][x] = (int)(255 * ((double)(dst2d[y][x] - min) / (max -min)));
		//				sum += nrm2d[y][x];
		//			}
		//		}
		//		ave = sum/(h*w);
		//		for(int y = 0; y < h; y++){
		//			for(int x = 0; x < w; x++){
		//				edge2d[y][x] = (nrm2d[y][x] > ave)?1:0;
		//			}
		//		}
	}
}
