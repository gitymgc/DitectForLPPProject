package pp;

import java.awt.image.BufferedImage;


public class CharacterCandidateDitection {

	static int h;
	static int w;
	/**
	 * 
	 * @param param
	 * @param srcImg
	 * @param binB2d
	 * @param binN2d
	 * @param neoBin2d
	 * @throws Exception
	 */
	public static void exec(Parameter param,BufferedImage srcImg,int binB2d[][], int binN2d[][],int neoBin2d[][])throws Exception{


		h = srcImg.getHeight();
		w = srcImg.getWidth();

		//文字候補抽出
		//ベース画像輝度値コピー
		int bin2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				bin2d[y][x] = binB2d[y][x];
			}
		}
		//ベース画像とノイズ抽出画像の差分画像作成
		int binDef2d[][] = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x =0; x < w; x++){
				if(binB2d[y][x] == 1 && binN2d[y][x] == 0){
					binDef2d[y][x] = 1;
				}
			}
		}

		//差分画像コピー
		for(int y = 0; y < h; y++){
			for(int x = 0; x <w; x++){
				neoBin2d[y][x] = binDef2d[y][x];
			}
		}

		//連結領域消去
		int T = param.T;
		for(int i = 0; i < T; i++){

			for(int y = param.mh ; y < h-param.mh; y++){
				for(int x = param.mh; x < w - param.mh; x++){
					for(int my = -param.mh; my <= param.mh;my++) {
						for(int mx = -param.mh; mx <= param.mh; mx++){
							if(my == 0 && mx == 0)continue;
							if(binDef2d[y][x] == 0 && bin2d[y][x] == 1){
								neoBin2d[y+my][x+mx] = 0;
							}
						}
					}
				}
			}

			for(int y = param.mh ; y < h-param.mh; y++){
				for(int x = param.mh; x < w - param.mh; x++){
					bin2d[y][x] = binDef2d[y][x];
					binDef2d[y][x] = neoBin2d[y][x];
				}
			}
		}
	}
}

