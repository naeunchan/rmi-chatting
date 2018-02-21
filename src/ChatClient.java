/*3�г� 1�б� ��Ʈ��ũ ���α׷��� �⸻ ������Ʈ
 * RMI�� �̿��� ä�ð� �������� ���α׷�
 * ����Ʈ�����к� 20150260 �̼ҿ�, 20150262 �̽���
 * Client Interface */

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;

public interface ChatClient extends Remote{		//Client Interface
   public abstract void setName() throws RemoteException;	//server�� register�Լ��� ȣ���ϴ� �Լ�
   public abstract String getName() throws RemoteException;	//private������ name�� �����ϱ� ���� �Լ�
   //public abstract void setFile(InetAddress sender_ipAddress) throws IOException;
   public abstract void said (String message) throws RemoteException;	//������ �޼����� �޼��� ���â�� ����ϴ� �Լ�
   public abstract String getFile() throws RemoteException;	//private������ file�� �����ϱ� ���� �Լ�
   public abstract void personCheck() throws RemoteException;	//������ client�� üũ�Ͽ� ä�ù� ������ ȭ�鿡 �����ִ� �Լ�
   public abstract void downloadFile(String filePath) throws RemoteException, IOException, ClassNotFoundException;	//������ ���� ������ ���� �޴� �Լ�
   
}