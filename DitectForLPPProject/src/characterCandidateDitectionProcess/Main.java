package characterCandidateDitectionProcess;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import noiseExtraction.NoiseExtraction;
import transForm.TransForm;
import baseImageCreation.BaseImageCreation;
public class Main {

	public static void main(String[] args)throws Exception{
		new Main().exec();
	}
	//パラメータ設定
	Parameter param = new Parameter(3,10,30,10,13,10,3);

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
			int binC2d[][] = new int[h][w];
			BaseImageCreation.exec(param,srcImg, src2d, binC2d);
			//ノイズ抽出
			int binN2d[][] = new int[h][w];
			NoiseExtraction.exec(param, srcImg, src2d, binN2d);
			//文字候抽出
			
			
			
			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					dstBuf.setElem(y*w+x, (binN2d[y][x] != 0)?255:0);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);
			


		}
	}
}
