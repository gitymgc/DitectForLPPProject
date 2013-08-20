package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class TestEpsilonFilter {

	public static void main(String[] args) throws Exception{

		new TestEpsilonFilter().exec();
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

			//局所処理準備
			int mSize = 3;
			int mh = mSize/2;
			int q = ((int)Math.pow((2*mh +1),2))-1;
			int e = 30;
			int dst2d[][] = new int[h][w];

			//平滑化
			int N = 10;
			for(int i = 0; i < N; i++){
				for(int y = mh; y <h-mh; y++){
					for(int x = mh; x < w-mh; x++){

						int tmp = 0;

						for(int my = -mh; my <= mh; my++){
							for(int mx = -mh; mx <= mh; mx++){
								//真ん中飛ばす
								if(my == 0 && mx == 0)continue;
								if(Math.abs(src2d[y][x] - src2d[y+my][x+mx]) <= e){
									tmp += src2d[y+my][x+mx];
								}else{
									tmp += src2d[y][x];
								}
							}
						}
						dst2d[y][x] = (int)((double)tmp/q);
						src2d[y][x] = dst2d[y][x];
					}
				}
			}

			LaplacianFilter.exec(srcImg,dst2d,mSize);

			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					dstBuf.setElem(y*w+x, dst2d[y][x]);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);
		}
	}

}
