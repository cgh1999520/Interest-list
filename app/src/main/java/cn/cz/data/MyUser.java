package cn.cz.data;

import cn.bmob.v3.BmobObject;

public class MyUser extends BmobObject {

	private String phone;
	private String password;
	private String huanxinPwd;
	// 昵称
	private String nickName;
	// 等级
	private Integer rank;
	//等级积分
	private Integer rankfen;
	// 积分
	private Integer integral;
	//环信好友帐号
	private String friendID;
	//个性签名
	private String gexingqianming;
	
	public synchronized String getGexingqianming() {
		return gexingqianming;
	}
	public synchronized void setGexingqianming(String gexingqianming) {
		this.gexingqianming = gexingqianming;
	}
	public synchronized String getFriendID() {
		return friendID;
	}
	public synchronized void setFriendID(String friendID) {
		this.friendID = friendID;
	}
	public synchronized String getPhone() {
		return phone;
	}
	public synchronized void setPhone(String phone) {
		this.phone = phone;
	}
	public synchronized String getPassword() {
		return password;
	}
	public synchronized void setPassword(String password) {
		this.password = password;
	}
	public synchronized String getHuanxinPwd() {
		return huanxinPwd;
	}
	public synchronized void setHuanxinPwd(String huanxinPwd) {
		this.huanxinPwd = huanxinPwd;
	}
	public synchronized String getNickName() {
		return nickName;
	}
	public synchronized void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public synchronized Integer getRank() {
		return rank;
	}
	public synchronized void setRank(Integer rank) {
		this.rank = rank;
	}
	public synchronized Integer getRankfen() {
		return rankfen;
	}
	public synchronized void setRankfen(Integer rankfen) {
		this.rankfen = rankfen;
	}
	public synchronized Integer getIntegral() {
		return integral;
	}
	public synchronized void setIntegral(Integer integral) {
		this.integral = integral;
	}
	
	
}
