package pp;
import java.awt.image.BufferedImage;

public class MoveToCenter {

	static int h;
	static int w;

	public static void exec(Parameter param,BufferedImage srcImg,int[][] neoBin2d,int binW2d[][])throws Exception{
	
		h = srcImg.getHeight();
		w = srcImg.getWidth();
		

		//重心移動
		//コピー
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				binW2d[y][x] = neoBin2d[y][x];
			}
		}

		int cnt = 0;
		int neoCnt = 0;
		for(;;){
			int tmpW2d[][] = new int [h][w];
			getTmp(binW2d,tmpW2d);

			//局所重心、移動ベクトル算出
			int vect[][][] = new int[h][w][2];
			getVector(param,tmpW2d,vect);

			//ベクトル正規化準備
			int theta[][][]= new int[h][w][2];
			int neoBinW2d[][] = new int[h][w];

			//収束処理
			if(cnt != 0 && cnt == neoCnt){
				getEnd(vect, theta,binW2d,neoBinW2d);
				break;
			}

			//正規化ベクトル取得
			getTheta(vect, theta);
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					neoBinW2d[y+theta[y][x][1]][x+theta[y][x][0]] +=  binW2d[y][x];
				}
			}

			//収束判定
			cnt = 0;
			neoCnt = 0;
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					if(binW2d[y][x] != 0){
						cnt++;
					}
					if(neoBinW2d[y][x] != 0){
						neoCnt++;
					}
				}
			}

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					binW2d[y][x] = neoBinW2d[y][x];
				}
			}
		}
	}

	private static void getTheta(int[][][] vect, int[][][] theta) {
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				int r = vect[y][x][0];
				int s = vect[y][x][1];
				int absR = Math.abs(r);
				int absS = Math.abs(s);
				if(r > 0 && 2*absS < absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = 0;
				}else if( r > 0 && s > 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = 1;
				}else if(r > 0 && s < 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = -1;
				}else if(r < 0 && 2*absS < absR){
					theta[y][x][0] = -1;
					theta[y][x][1] = 0;
				}else if( r < 0 && s > 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = -1;
					theta[y][x][1] = 1;
				}else if( r < 0 && s < 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = -1;
					theta[y][x][1] = -1;
				}else if (s > 0 && 2*absR < absS){
					theta[y][x][0] = 0;
					theta[y][x][1] = 1;
				}else if(s < 0 && 2*absR <= absS){
					theta[y][x][0] = 0;
					theta[y][x][1] = -1;
				}else{
					theta[y][x][0] = 0;
					theta[y][x][1] = 0;
				}
			}
		}


	}

	private static void getTmp(int[][] binW2d, int[][] tmpW2d) {
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				if(binW2d[y][x] > 0){
					tmpW2d[y][x] = 1;
				}else{
					tmpW2d[y][x] = 0;
				}
			}
		}

	}

	private static void getVector(Parameter param, int[][] tmpW2d, int[][][] vect) {

		for(int y = param.mh; y < h-param.mh; y++){
			for(int x = param.mh; x < w-param.mh; x++){
				int r = 0;
				int s = 0;
				for(int my = -param.mh; my <= param.mh; my++){
					for(int mx = -param.mh; mx <= param.mh; mx++){
						if(my == 0 && mx == 0)continue;
						r += mx * tmpW2d[y+my][x+mx];
						s += my * tmpW2d[y+my][x+mx];
					}
				}
				vect[y][x][0] = r;
				vect[y][x][1] = s;
			}
		}

	}

	private static void getEnd(int[][][] vect, int[][][] theta, int[][] binW2d, int[][] neoBinW2d) {
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				int r = vect[y][x][0];
				int s = vect[y][x][1];
				int absR = Math.abs(r);
				int absS = Math.abs(s);
				if(r > 0 && 2*absS < absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = 0;
				}else if( r > 0 && s < 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = 0;
				}else if(r > 0 && s > 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = 1;
					theta[y][x][1] = 1;
				}else if(r < 0 && s > 0 && absR/2 <= absS && absS <= 2*absR){
					theta[y][x][0] = 0;
					theta[y][x][1] = 1;
				}else if(s > 0 && 2*absR < absS){
					theta[y][x][0] = 0;
					theta[y][x][1] = 1;
				}else{
					theta[y][x][0] = 0;
					theta[y][x][1] = 0;
				}
			}
		}
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				neoBinW2d[y+theta[y][x][1]][x+theta[y][x][0]] +=  binW2d[y][x];
			}
		}
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				binW2d[y][x] = neoBinW2d[y][x];
			}
		}
	}
}
