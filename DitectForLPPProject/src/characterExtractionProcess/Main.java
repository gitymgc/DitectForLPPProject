package characterExtractionProcess;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import noiseExtraction.NoiseExtraction;
import transForm.TransForm;
import baseImageCreation.BaseImageCreation;
import characterCandidateDitectionProcess.CharacterCandidateDitection;
import characterCandidateDitectionProcess.Parameter;
public class Main {

	public static void main(String[] args)throws Exception{
		new Main().exec();
	}
	//パラメータ設定
	Parameter param = new Parameter(3,15,30,10,15,12,10,3,50);

	String srcDirPath = "./debug/src/";
	String dstDirPath = "./debug/dst/";

	int h;
	int w;

	public void exec()throws Exception{
		File srcDir = new File(srcDirPath);
		File srcFiles[] = srcDir.listFiles();
		for(File srcFile : srcFiles){
			BufferedImage srcImg = ImageIO.read(srcFile);

			w = srcImg.getWidth();
			h = srcImg.getHeight();

			//グレイスケール化
			int src2d[][] = new int[h][w];
			TransForm.exec(srcImg,src2d);

			//ベース画像作成
			int binB2d[][] = new int[h][w];
			BaseImageCreation.exec(param,srcImg, src2d, binB2d);

			//ノイズ抽出
			int lowGra2d[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x <w; x++){
					if((src2d[y][x] -50) > 0){
						lowGra2d[y][x] = (int)(src2d[y][x] - 50);
					}else{
						lowGra2d[y][x] = 0;
					}
				}
			}

			//反転
			int opt2d[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					opt2d[y][x] = 255 - src2d[y][x];
				}
			}

			int binN2d[][] = new int[h][w];
			//			NoiseExtractionOpt.exec(param, srcImg, src2d, binN2d);
			//			NoiseExtraction.exec(param, srcImg, opt2d, binN2d);
			NoiseExtraction.exec(param, srcImg, lowGra2d, binN2d);

			//文字候補抽出
			int neoBin2d[][] = new int[h][w];
			CharacterCandidateDitection.exec(param, srcImg, binB2d, binN2d, neoBin2d);

			//文字抽出
			//重心移動
			//コピー
			int binW2d[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					binW2d[y][x] = neoBin2d[y][x];
				}
			}
			//局所重心、移動ベクトル算出
			int vect[][][] = new int[h][w][2];
			for(int y = param.mh; y < h-param.mh; y++){
				for(int x = param.mh; x < w-param.mh; x++){
					int r = 0;
					int s = 0;
					for(int my = -param.mh; my < param.mh; my++){
						for(int mx = -param.mh; mx < param.mh; mx++){
							if(my == 0 && mx == 0)continue;
							r += mx * binW2d[y+my][x+mx];
							s += my * binW2d[y+my][x+mx];
						}
					}
					vect[y][x][0] = r;
					vect[y][x][1] = s;
				}
			}
			//ベクトル正規化
			int theta[][][]= new int[h][w][2];
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
					}else if(r > 0 && s < 0 && absR/2 <= absS && absS <= absR){
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


			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					//					dstBuf.setElem(y*w+x, lowGra2d[y][x]);
					dstBuf.setElem(y*w+x, (neoBin2d[y][x] != 0)?255:0);
					//										dstBuf.setElem(y*w+x, (binDef2d[y][x] != 0)?255:0);
					//										dstBuf.setElem(y*w+x, (binN2d[y][x] != 0)?255:0);
					//										dstBuf.setElem(y*w+x, (binB2d[y][x] != 0)?255:0);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);



		}
	}
}
