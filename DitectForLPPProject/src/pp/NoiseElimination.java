package pp;
import java.awt.image.BufferedImage;

public class NoiseElimination {

	static int h;
	static int w;

	public static void exec(Parameter param,BufferedImage srcImg,int binY2d[][],int binZ2d[][])throws Exception{

		w = srcImg.getWidth();
		h = srcImg.getHeight();

		//ノイズ除去

		for(int i = 0; i < 3; i++){
			int tmpZ2d[][] = new int[h][w];
			for(int y = param.mh; y < h-param.mh; y++){
				for(int x = param.mh; x < w-param.mh; x++){
					int sum = 0;
					for(int my = -param.mh; my <= param.mh; my++){
						for(int mx = -param.mh; mx <= param.mh; mx++){
							if(binY2d[y][x] == 0)continue;
							if(my == 0 && mx == 0)continue;
							sum += binY2d[y+my][x+mx];
						}
					}
					if(binY2d[y][x] == 0)continue;
					if(sum >= 3){
						tmpZ2d[y][x] = 1;
					}else{
						tmpZ2d[y][x] = 0;
					}
				}
			}
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					binY2d[y][x] = tmpZ2d[y][x];
				}
			}
		}
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				binZ2d[y][x] = binY2d[y][x];
			}
		}
	}
}
