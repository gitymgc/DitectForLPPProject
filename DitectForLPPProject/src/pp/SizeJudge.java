package pp;
import java.awt.image.BufferedImage;

public class SizeJudge {

	static int h;
	static int w;

	public static void exec(Parameter param,BufferedImage srcImg,int[][] neoBin2d,int binW2d[][],int binY2d[][])throws Exception{

		h = srcImg.getHeight();
		w = srcImg.getWidth();

		//サイズ判別
		//最小サイズと最大サイズ設定
		int big = param.big;
		int small = param.small;
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				if(small < binW2d[y][x] && binW2d[y][x] < big){
					binY2d[y][x] = 1;
				}else{
					binY2d[y][x] = 0;
				}
			}
		}
		//			論文の指定処理
		for(;;){
			int cnt = 0; 
			int neoCnt = 0;
			int neoBinY2d[][] = new int[h][w];

			for(int y = param.mh; y < h - param.mh; y++ ){
				for(int x = param.mh; x < w - param.mh; x++){
					int sum = 0;
					for(int my = -param.mh; my <= param.mh;my++){
						for(int mx = -param.mh; mx <= param.mh; mx++){
							sum += binY2d[y+my][x+mx];
						}
					}
					if(0 < sum && 0 < neoBin2d[y][x]){
						neoBinY2d[y][x] = 1;
					}
				}
			}
			for(int y = 0; y < h; y++){
				for(int x = 0; x <w; x++){
					if(binY2d[y][x] ==1){
						cnt++;
					}
					if(neoBinY2d[y][x] == 1){
						neoCnt++;
					}
					binY2d[y][x] = neoBinY2d[y][x];
				}
			}
			if(cnt == neoCnt){
				break;
			}
		}
	}
}
