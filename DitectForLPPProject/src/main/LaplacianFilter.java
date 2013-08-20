package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class LaplacianFilter {

	public static void main (String args[]) throws Exception{
		new LaplacianFilter().exec();
	}

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
			
			System.out.print(srcBuf.getSize());
			System.out.println(" "+h*w);

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
			
			//オペレータ作成
			int ope[][] = {
					{-1, -1, -1},
					{-1,  8, -1},
					{-1, -1, -1}
			}; 

			//局所処理
			int mSize = 3;
			int mh = mSize/2;

			int dst2d[][] = new int[h][w];

			for(int y = mh; y <h-mh; y++){
				for(int x = mh; x < w-mh; x++){
					int tmp = 0;
					for(int my = -mh; my <=mh; my++){
						for(int mx = -mh; mx <=mh; mx++){
							tmp += src2d[y+my][x+mx] * ope[my+mh][mx+mh];
						}
					}
					dst2d[y][x] = tmp;
				}
			}

			//正規化
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;

			for(int y = 0; y < h; y ++){
				for(int x = 0; x < w; x++){
					if(dst2d[y][x] < min)
						min = dst2d[y][x];
					if(dst2d[y][x] > max)
						max = dst2d[y][x];
				}
			}

			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();


			int nrm2d[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					nrm2d[y][x] = (int)(255 * ((double)(dst2d[y][x] - min) / (max -min)));
					dstBuf.setElem(y*w+x, nrm2d[y][x]);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);
		}
	}
}
