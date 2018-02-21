/*3�г� 1�б� ��Ʈ��ũ ���α׷��� �⸻ ������Ʈ
 * RMI�� �̿��� ä�ð� �������� ���α׷�
 * ����Ʈ�����к� 20150260 �̼ҿ�, 20150262 �̽���
 * Server Interface */
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;
import java.util.*;

public interface ChatServer extends Remote {
  public static final String REGISTRY_NAME = "rmi://localhost:3333/Callback Server";
  public abstract void register (ChatClient client) throws RemoteException;	//client�� ������ �� ����Ǵ� �Լ�
  public abstract void deregister (ChatClient client) throws RemoteException;	//â�� ���� ��� �ش� client�� ä�ù��� �����ٴ� �޼����� ����ϴ� �Լ�
  //public abstract void register_file(ChatClient client) throws IOException;
  public abstract void say (String message) throws RemoteException;	//��� client�鿡�� �޼����� ����ϱ� ���� �Լ�
  public abstract void setIPAddress(InetAddress sender_ipAddress) throws RemoteException;//������ �����ϴ� client�� ip�ּ� ����
  public abstract InetAddress getIPAddress()throws RemoteException;	//private������ sender_ipAddress�� �����ϱ� ���� �Լ�
  public abstract ArrayList clientList() throws RemoteException;	//������ client���� �г����� �����ϱ� ���� �Լ�
  public abstract void whisper(String message) throws RemoteException;	//client�� ������ �޼����� �ӼӸ��� ��� ����Ǵ� �Լ�
  public void uploadSFile(ChatClient sender, FileInfo upInfo) throws IOException, ClassNotFoundException;	//Ŭ���̾�Ʈ���� ������ ������ ������ �����ϴ� �Լ�
  public Object downloadSFile(String filePath) throws IOException, ClassNotFoundException;	//�������� ������ ����Ʈ������ �о�帮�� �Լ�
}