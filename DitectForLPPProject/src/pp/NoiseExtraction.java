package pp;

import image.filter.LaplacianFilter;

import java.awt.image.BufferedImage;


public class NoiseExtraction {

	//パラメータ設定

	static int maxNum;
	static int minNum;
	static int expNum;

	static int h;
	static int w;
	static int mSize;
	static int mh;
	static int mhForBin;

	/**
	 * ノイズ抽出
	 * @param param
	 * @param srcImg
	 * @param src2d
	 * @param bin2d
	 * @throws Exception
	 * 
	 */
	public static void exec(Parameter param,BufferedImage srcImg,int src2d[][],int bin2d[][])throws Exception{

		maxNum = param.maxNum;
		minNum = param.minNum;
		expNum = param.expNum;

		mSize = param.mSize;
		mh = param.mh;
		mhForBin = param.mhForBin2;

		w = srcImg.getWidth();
		h = srcImg.getHeight();

		int max2d[][] = new int[h][w];
		MaxFilter(src2d,max2d,maxNum);

		int min2d[][] = new int[h][w];
		MinFilter(max2d,min2d,minNum);

		//エッジ検出
		int lap2d[][] = new int[h][w];
		LaplacianFilter.exec(srcImg,min2d,lap2d,mSize);
		int edge2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				if(lap2d[y][x] > min2d[y][x]){
					edge2d[y][x] = 1;
				}else{
					edge2d[y][x] = 0;
				}
			}
		}


		//しきい値候補決定
		int preBord2d[][] = new int[h][w];
		getCandidateBorder(preBord2d,edge2d,min2d);

		//各画素ごとのしきい値を決定
		int bord2d[][] =  new int[h][w];
		getBoｒderLine(preBord2d,bord2d);

		//二値化実行
		for(int y = 0; y < h; y ++){
			for(int x = 0; x < w; x++){
				if(min2d[y][x] < bord2d[y][x]){
					bin2d[y][x] = 1;
				}
			}
		}

		//膨張処理
		Expansion(bin2d,expNum);

	}


	private static void Expansion(int[][] bin2d, int num) {

		int tmp2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				tmp2d[y][x] = bin2d[y][x];
			}
		}

		for(int i = 0; i < num; i++){
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					int sum = 0;
					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){
							if(my == 0 && mx ==0)continue;
							sum += tmp2d[y+my][x+mx];
						}
					}
					if(sum > 0){
						bin2d[y][x] = 1;
					}
				}
			}
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					tmp2d[y][x] = bin2d[y][x];
				}
			}
		}
	}


	private static void MinFilter(int[][] max2d, int[][] min2d, int num) {

		int tmp2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				tmp2d[y][x] = max2d[y][x];
			}
		}

		for(int i = 0; i < num; i++){
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){

					int min = Integer.MAX_VALUE;

					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){
							if(my == 0 && mx ==0)continue;
							if(tmp2d[y+my][x+mx] < min){
								min = tmp2d[y+my][x+mx];
							}
						}
					}
					min2d[y][x] = min;
				}
			}
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					tmp2d[y][x] = min2d[y][x];
				}
			}
		}
	}

	private static void MaxFilter(int[][] src2d, int[][] max2d,int num) {

		int tmp2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				tmp2d[y][x] = src2d[y][x];
			}
		}

		for(int i = 0; i < num; i++){
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){

					int max = Integer.MIN_VALUE;

					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){
							if(my == 0 && mx ==0)continue;
							if(tmp2d[y+my][x+mx] > max){
								max = tmp2d[y+my][x+mx];
							}
						}
					}
					max2d[y][x] = max;
				}
			}
			for(int y = mh; y < h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					tmp2d[y][x] = max2d[y][x];
				}
			}
		}
	}

	private static void getCandidateBorder(int[][] preBord2d, int[][] edge2d, int[][] min2d) {
		for(int y = mh; y < h-mh; y++){
			for(int x = mh; x < w-mh; x++){
				if(edge2d[y][x] == 1){
					int min = Integer.MAX_VALUE;
					for(int my = -mh; my <= mh; my++){
						for(int mx = -mh; mx <= mh; mx++){
							if(my == 0 && mx == 0)continue;
							if(min2d[y+my][x+mx] < min){
								min = min2d[y+my][x+mx];
							}
						}
					}
					preBord2d[y][x] = (min2d[y][x] + min)/2;
				}else{
					preBord2d[y][x] = 0;
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
}

