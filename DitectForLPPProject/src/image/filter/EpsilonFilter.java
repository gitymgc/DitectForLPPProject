package image.filter;

import java.awt.image.BufferedImage;

public class EpsilonFilter {

	public static void exec(BufferedImage srcImg,int gra2d[][],int smth2d[][], int mSize,int e,int N) throws Exception{

		int w = srcImg.getWidth();
		int h = srcImg.getHeight();

		//局所処理準備
		int mh = mSize/2;
		int q = ((int)Math.pow((2*mh +1),2))-1;
		int dst2d[][] = new int[h][w];
		
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x ++){
				smth2d[y][x] = gra2d[y][x];
			}
		}
		
		//平滑化
		for(int i = 0; i < N; i++){
			for(int y = mh; y <h-mh; y++){
				for(int x = mh; x < w-mh; x++){

					int tmp = 0;

					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){

							//真ん中飛ばす
							if(my == 0 && mx == 0)continue;
							if(Math.abs(smth2d[y][x] - smth2d[y+my][x+mx]) <= e){
								tmp += smth2d[y+my][x+mx];
							}else{
								tmp += smth2d[y][x];
							}
						}
					}
					dst2d[y][x] = (int)((double)tmp/q);
				}
			}
			for(int y = mh; y <h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					smth2d[y][x] = dst2d[y][x];
				}
			}
		}
	}
}
