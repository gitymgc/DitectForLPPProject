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

			int neoBin2d[][] = new int[h][w];
			CharacterCandidateDitection.exec(param, srcImg, binB2d, binN2d, neoBin2d);
			
			
			
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
