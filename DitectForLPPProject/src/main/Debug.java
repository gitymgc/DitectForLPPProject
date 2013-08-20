package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class Debug {

	public static void main (String args[]) throws Exception{
		new Debug().exec();
	}
	//パラメータ設定
	int mSize = 3;
	int e = 30;
	int N = 10;
	String srcDirPath = "./debug/src/";
	String dstDirPath = "./debug/dst/";

	public void exec() throws Exception{

		File srcDir = new File(srcDirPath);
		File srcFiles[] = srcDir.listFiles();
		for(File srcFile : srcFiles){

			BufferedImage srcImg = ImageIO.read(srcFile);
			WritableRaster srcRas = srcImg.getRaster();
			DataBuffer srcBuf = srcRas.getDataBuffer();

			int w = srcImg.getWidth();
			int h = srcImg.getHeight();

			int src2d[][] =  new int[h][w];
			//グレイスケール取得
			//			ImageDecoder.exec(srcImg, src2d);
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x ++){
					int argb = srcImg.getRGB(x,y);
					src2d[y][x] = (int)(
							0.3 * (argb >> 16 & 0xff) +
							0.6 * (argb >> 8 & 0xff) + 
							0.1 * (argb & 0xff));
				}
			}
			
			//平滑化
			EpsilonFilter.exec(srcImg, src2d, mSize, e, N);
			//エッジ抽出
			LaplacianFilterForThis.exec(srcImg,src2d,mSize);
			
			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					dstBuf.setElem(y*w+x, src2d[y][x]);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);
		}
	}
}
