package com.liangxiao.lovemusic.model;

public class Song {
	private String mSongName;// 歌曲名字，如童话
	private String mSongFileName;// 歌曲文件名字，如童话.mp3
	private int mNameLength;// 歌曲名字的长度，如2

	public char[] getNameCharacters() {
		return mSongName.toCharArray();// 把当前的歌曲名字转化成字符数组存入部分
	}

	public String getSongName() {
		return mSongName;
	}

	public void setSongName(String songName) {
		this.mSongName = songName;
		this.mNameLength = songName.length();// 歌曲名字的长度，如2
	}

	public String getSongFileName() {
		return mSongFileName;
	}

	public void setSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}

	public int getNameLength() {
		return mNameLength;
	}

	// public void setNameLength(int mNameLength) {
	// this.mNameLength = mNameLength;
	// }

}
