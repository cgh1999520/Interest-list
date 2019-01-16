package cn.cz.data;

public class LiaoTianData{
	public static final int mi = 1;
	public static final int you = 2;
	public String content;
	private int touxiang;
	private int peg;

	public LiaoTianData(String content, int touxiang, int peg)
	{
		this.content = content;
		this.touxiang = touxiang;
		this.peg = peg;
	}

	public int getTouxiang()
	{
		return touxiang;
	}

	public String getContent()
	{
		return content;
	}
	
	public int getPeg(){
		return peg;
	}
	
}
