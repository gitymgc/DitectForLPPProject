package baseImageCreation;

import java.awt.image.BufferedImage;

import main.EpsilonFilter;
import main.LaplacianFilterForThis;
import characterCandidateDitectionProcess.Parameter;

public class BaseImageCreation {

	//パラメータ設定
	static int mSize;
	static int e ;
	static int N ;

	static int h;
	static int w;
	static int mh;
	static int mhForBin;
	/**
	 * ベース画像作成
	 * @param param
	 * @param srcImg
	 * @param src2d
	 * @param bin2d
	 * @throws Exception
	 */
	public static void exec(Parameter param,BufferedImage srcImg,int src2d[][],int bin2d[][]) throws Exception{

		
		e = param.epsilon;
		N = param.smthNum;
		
		mSize = param.mSize;
		mh = param.mh;
		mhForBin = param.mhForBin;
		
		w = srcImg.getWidth();
		h = srcImg.getHeight();

		//平滑化
		int smth2d[][] = new int[h][w];
		EpsilonFilter.exec(srcImg, src2d,smth2d, mSize, e, N);
		//エッジ抽出
		int edge2d[][] = new int[h][w];
		LaplacianFilterForThis.exec(srcImg,smth2d,edge2d,mSize);

		//しきい値候補決定
		int preBord2d[][] = new int[h][w];
		getCandidateBorder(preBord2d,edge2d,smth2d);

		//各画素ごとのしきい値を決定
		int bord2d[][] =  new int[h][w];
		getBoｒderLine(preBord2d,bord2d);

		//二値化実行
		for(int y = 0; y < h; y ++){
			for(int x = 0; x < w; x++){
				if(smth2d[y][x] < bord2d[y][x]){
					bin2d[y][x] = 1;
				}
			}

		}
	}

	private static void getBoｒderLine(int[][] preBord2d, int[][] bord2d) {
		for(int y = mhForBin; y < h-mhForBin; y++){
			for(int x = mhForBin; x < w-mhForBin; x++){
				int sum = 0;
				int cnt = 0;
				for(int my = -mhForBin; my <= mhForBin; my++){
					for(int mx = -mhForBin; mx <= mhForBin; mx++){
						if(my == 0 && mx == 0)continue;
						if(preBord2d[y+my][x+mx] > 0){
							sum += preBord2d[y+my][x+mx];
							cnt++;
						}
					}
				}
				if(cnt != 0){
					bord2d[y][x] = (sum/cnt);
				}
			}
		}

	}

	private static void getCandidateBorder(int[][] preBord2d, int[][] edge2d, int[][] smth2d) {
		for(int y = mh; y < h-mh; y++){
			for(int x = mh; x < w-mh; x++){
				if(edge2d[y][x] == 1){
					int min = Integer.MAX_VALUE;
					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){
							if(my == 0 && mx == 0)continue;
							if(smth2d[y+my][x+mx] < min){
								min = smth2d[y+my][x+mx];
							}
						}
					}
					preBord2d[y][x] = (smth2d[y][x] + min)/2;
				}else{
					preBord2d[y][x] = 0;
				}
			}
		}

	}
}
