package noiseExtraction;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args)throws Exception{

		new Main().exec();

	}

	//パラメータ設定
	int mSize = 3;
	int maxNum = 3;
	int minNum = 5;
	String srcDirPath = "./debug/src/";
	String dstDirPath = "./debug/dst/";

	int h;
	int w;
	int mh;

	public void exec()throws Exception{

		File srcDir = new File(srcDirPath);
		File srcFiles[] = srcDir.listFiles();
		for(File srcFile : srcFiles){

			BufferedImage srcImg = ImageIO.read(srcFile);

			w = srcImg.getWidth();
			h = srcImg.getHeight();

			int src2d[][] =  new int[h][w];
			//グレイスケール取得
			//			ImageDecoder.exec(srcImg, src2d);
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x ++){
					int argb = srcImg.getRGB(x, y);
					src2d[y][x] = (int)(
							0.3 * (argb >> 16 & 0xff) +
							0.6 * (argb >> 8 & 0xff) +
							0.1 * (argb & 0xff));
				}
			}

			mh = mSize/2;
			int max2d[][] = new int[h][w];
			MaxFilter(src2d,max2d,maxNum);

			String dstFilePath = dstDirPath + srcFile.getName();
			String dstElem[] = srcFile.getName().split("\\.");
			File dstFile = new File(dstFilePath);
			BufferedImage dstImg = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster dstRas = dstImg.getRaster();
			DataBuffer dstBuf = dstRas.getDataBuffer();

			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					dstBuf.setElem(y*w+x, max2d[y][x]);
				}
			}
			ImageIO.write(dstImg, "bmp", dstFile);


		}
	}

	private void MaxFilter(int[][] src2d, int[][] max2d,int num) {

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
}

