package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class BaseImageCreation {

	public static void main (String args[]) throws Exception{
		new BaseImageCreation().exec();
	}
	//パラメータ設定
		int mSize = 3;
		int e = 30;
		int N = 10;
		String srcDirPath = "./debug/src/";
		String dstDirPath = "./debug/dst/";

		int h;
		int w;
		int mh = mSize/2;
		int mhForBin = 10;
		
		public void exec() throws Exception{

			File srcDir = new File(srcDirPath);
			File srcFiles[] = srcDir.listFiles();
			for(File srcFile : srcFiles){

				BufferedImage srcImg = ImageIO.read(srcFile);
				WritableRaster srcRas = srcImg.getRaster();
				DataBuffer srcBuf = srcRas.getDataBuffer();

				w = srcImg.getWidth();
				h = srcImg.getHeight();

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
				int bin2d[][] = new int[h][w];
				for(int y = 0; y < h; y ++){
					for(int x = 0; x < w; x++){
						if(smth2d[y][x] < bord2d[y][x]){
							bin2d[y][x] = 255;
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
						dstBuf.setElem(y*w+x, bin2d[y][x]);
					}
				}
				ImageIO.write(dstImg, "bmp", dstFile);
			}
		}

		private void getBoｒderLine(int[][] preBord2d, int[][] bord2d) {
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

		private void getCandidateBorder(int[][] preBord2d, int[][] edge2d, int[][] smth2d) {
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
