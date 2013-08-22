package characterCandidateDitectionProcess;

public class Parameter {

	//メンバ変数で定義
	
	//共用
	public int mSize;
	public int mh;
	public int mhForBin;

	//BaseImageCreation用
	public int epsilon;
	public int smthNum;

	//NioseExtraction用
	public int maxNum;
	public int minNum;
	public int mhForBin2;
	public int expNum;
	
	//文字候補抽出用
	public int T;


	/**
	 * パラメータ
	 * @param mSize
	 * @param mhForBin
	 * @param epsilon
	 * @param smthNum
	 * @param maxNum
	 * @param minNum
	 * @param mhForBin2
	 * @param expNum
	 * @param T
	 */
	public Parameter(int mSize,int mhForBin,int epsilon, int smthNum,int maxNum,int minNum,int mhForBin2,int expNum,int T){
		//コンクラスタで値を格納
		this.mSize = mSize;
		this.mh = mSize/2;
		this.mhForBin = mhForBin;

		this.epsilon = epsilon;
		this.smthNum = smthNum;

		this.maxNum = maxNum;
		this.minNum = minNum;
		this.mhForBin2 = mhForBin2;
		this.expNum = expNum;
		
		this.T= T;

	}

}
