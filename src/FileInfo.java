/*3�г� 1�б� ��Ʈ��ũ ���α׷��� �⸻ ������Ʈ
 * RMI�� �̿��� ä�ð� �������� ���α׷�
 * ����Ʈ�����к� 20150260 �̼ҿ�, 20150262 �̽���
 * �����ϴ� ������ ������ �����ϴ� class*/

import java.io.Serializable;

public class FileInfo implements Serializable{
	private static final long serialVersionUID = 2L;
	private String filename;	//file�� �̸��� �����ϴ� ����
	private byte[] filedata;	//file�� data�� �����ϴ� ����Ʈ �迭 
	
	public String getFilename() {	//private������ filename�� �����ϱ� ���� �Լ�
		return filename;	
	}
	
	public void setFilename(String filename) {	//filename�� �����ϴ� �Լ�
		this.filename = filename;
	}
	
	public byte[] getFiledata() {	//private������ filename�� �����ϱ� ���� �Լ�
		return filedata;
	}
	
	public void setFiledata(byte[] filedata) {	//file�� data�� �����ϴ� �Լ�	
		this.filedata = filedata;
	}
 }
