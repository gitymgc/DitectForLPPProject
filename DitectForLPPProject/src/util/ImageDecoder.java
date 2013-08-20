package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

public class ImageDecoder {
	/**
	 *  各BufferedImageタイプの画像ファイルを全て一意にグレイスケールに変換
	 * @param srcImg
	 * @param src2d
	 * @throws Exception
	 */
	public static void exec(BufferedImage srcImg, int[][] src2d)throws Exception{

		WritableRaster srcRas = srcImg.getRaster();
		DataBuffer srcBuf = srcRas.getDataBuffer();

		int h = srcImg.getHeight();
		int w = srcImg.getWidth();

		switch(srcImg.getType()){


		case BufferedImage.TYPE_3BYTE_BGR:
			/**
			 * b,g,r,b,g,r....の順番に各チャンネル3つがh*w個ずつ並んだ配列
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem((y*w+x)*3);
				}
			}
			break;

		case BufferedImage.TYPE_4BYTE_ABGR:
			/**
			 * a,b,g,r,a,b,g,r....の順番に各チャンネル4つがh*w個ずつ並んだ配列
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem((y*w+x)*4+1);
				}
			}
			break;

		case  BufferedImage.TYPE_USHORT_565_RGB:
			/**
			 * rgb3チャンネルがバイト配列でまとめられた一つの値が、h*w個ならんだ配列
			 * バイト配列の1~5桁が青、6~11桁が緑、12~16桁が赤のチャンネルを表す。
			 * それぞれの値には所有する桁数で重みがつけられているので、、
			 * 重みで割ることで、正規化された輝度値の割合を取得することが出来る。
			 * グレイスケール文字は、3チャンネルの輝度値が全て同じ割合なので、
			 * いずれかのチャンネルから取得した割合に255をかけることで、、8bitの配列による輝度値を取得することが出来る。
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem(y*w+x);
				}
			}
			//いずれかのチャンネルの倍率取得
			int red[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					red[y][x] = src2d[y][x] >>11 & 0xff;
				// 8.22 = 255 / 31
				// 8.22 = (2^8-1) / (2^5-1)
				src2d[y][x] = (int) (red[y][x]*8);
				}
			}
			break;
		case BufferedImage.TYPE_USHORT_555_RGB:
			/**
			 * rgb3チャンネルがバイト配列でまとめられた一つのint値が、h*w個ならんだ配列
			 * バイト配列の1~5桁が青、6~10桁が緑、11~15桁が赤のチャンネルを表す。
			 * それぞれの値には限られた桁数で色を表すための重みがつけられているので、、
			 * 重みで割ることで、正規化された輝度値の割合を取得することが出来る。
			 * グレイスケール文字は、3チャンネルの輝度値が全て同じ割合なので、
			 * いずれかのチャンネルから取得した割合に255をかけることで、、8bitの配列による輝度値を取得することが出来る。
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem(y*w+x);
				}
			}

			//いずれかのチャンネルの倍率取得
			int red2[][] = new int[h][w];
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					red2[y][x] = src2d[y][x] >>10 & 0xff;
				// 8.22 = 255 / 31
				// 8.22 = (2^8-1) / (2^5-1)
				src2d[y][x] = (int) (red2[y][x]*8);
				}
			}
			break;
		case  BufferedImage.TYPE_BYTE_GRAY:
			/**
			 * 同様のデータ型なので、そのまま値を代入
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem(y*w+x);
				}
			}
			break;
		case  BufferedImage.TYPE_USHORT_GRAY:
			/**
			 * 0~65535の階調で色が示されているため、その割合を0~255に当て嵌めればよい。
			 * ので、256で割るだけ。
			 */
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem(y*w+x);
					src2d[y][x] = src2d[y][x]/256;
				}
			}
			break;
		case  BufferedImage.TYPE_BYTE_BINARY:
			/**
			 * 通常TYPE_BYTE_BINARYでは、8ピクセルの値(0か1)が八桁のバイト配列にまとめられて格納されているので、
			 * 階調をもたず、各ピクセルの輝度値は、その値をbitShiftにより分解して0か255を新たに格納しなおす。
			 * が、
			 * すぐ下のif文内に入るものは、2ピクセルごと八桁のバイト配列にまとまっており、
			 * そのため1ピクセルはバイト配列4桁分(0~15)の階調を持つ、
			 * この値を、0~255に当てはめることで、
			 * バイナリデータながら、階調を持ったデータの復元が出来る。
			 * 
			 */
			if(srcBuf.getSize() != h*(w/8)){
				//まずバイト配列を取得する
				//2048
				int bin2d[][] = new int[h][w/2];
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w/2; x++){
						bin2d[y][x] = srcBuf.getElem(y*(w/2)+x);
					}
				}
				int sep2d[][] = new int[h][w];
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w/2; x++){
						for(int b = 0; b < 2; b++){
							sep2d[y][2*x+b] = bin2d[y][x]>>4-(4*b) & 0x0f;
						}
					}
				}
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w; x++){
						// 17 = 255 / 15
						// 17 = (2^8-1) / (2^4-1)
						src2d[y][x] = (int) (sep2d[y][x]*17);
					}
				}


			}else{
				int bin2d[][] = new int[h][w/8];
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w/8; x++){
						bin2d[y][x] = srcBuf.getElem(y*(w/8)+x);
					}
				}
				for(int y = 0; y < h; y++){
					for(int x = 0; x < w/8;x++){
						for(int b = 0; b < 8; b++){
							src2d[y][x*8+b] = bin2d[y][x]>>7-b & 0x01;
						if(src2d[y][x*8+b] != 0)
							src2d[y][x*8+b] = 255;
						}
					}
				}
			}
			break;


		case  BufferedImage.TYPE_BYTE_INDEXED:
			/**
			 * このデータ型は、輝度値が217~255の間の39階調を使用して表されている。
			 * その輝度値217~255(0~39)をという数値をそのままTYPE_BYTE_GRAYで使用すると、
			 * 階調がかたよってしまうので、0~255の値に変換して出力する。
			 */
			int max = Integer.MIN_VALUE;
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = srcBuf.getElem(y*w+x);
					if(src2d[y][x] > max){
						max = src2d[y][x];
					}
				}
			}
			int maxV = Integer.MIN_VALUE;
			int minV = Integer.MAX_VALUE;
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					if(src2d[y][x] > maxV){
						maxV = src2d[y][x];
					}
					if(src2d[y][x] < minV){
						minV = src2d[y][x];
					}
				}
			}
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					src2d[y][x] = (255*(src2d[y][x]-minV))/(maxV-minV);
				}
			}
			break;
		}


	}

}
