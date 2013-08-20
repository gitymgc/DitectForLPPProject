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
	public int expNum;


	/**
	 * パラメータ
	 * @param mSize
	 * @param mhForBin
	 * @param epsilon
	 * @param smthNum
	 * @param maxNum
	 * @param minNum
	 * @param expNum
	 */
	public Parameter(int mSize,int mhForBin,int epsilon, int smthNum,int maxNum,int minNum,int expNum){
		//コンクラスタで値を格納
		this.mSize = mSize;
		this.mh = mSize/2;
		this.mhForBin = mhForBin;

		this.epsilon = epsilon;
		this.smthNum = smthNum;

		this.maxNum = maxNum;
		this.minNum = minNum;
		this.expNum = expNum;

	}

}
