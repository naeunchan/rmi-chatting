/*3학년 1학기 네트워크 프로그래밍 기말 프로젝트
 * RMI를 이용한 채팅과 파일전송 프로그램
 * 소프트웨어학부 20150260 이소영, 20150262 이시현
 * 전송하는 파일의 정보를 저장하는 class*/

import java.io.Serializable;

public class FileInfo implements Serializable{
	private static final long serialVersionUID = 2L;
	private String filename;	//file의 이름을 저장하는 변수
	private byte[] filedata;	//file의 data를 저장하는 바이트 배열 
	
	public String getFilename() {	//private변수인 filename에 접근하기 위한 함수
		return filename;	
	}
	
	public void setFilename(String filename) {	//filename을 저장하는 함수
		this.filename = filename;
	}
	
	public byte[] getFiledata() {	//private변수인 filename에 접근하기 위한 함수
		return filedata;
	}
	
	public void setFiledata(byte[] filedata) {	//file의 data를 저장하는 함수	
		this.filedata = filedata;
	}
 }
