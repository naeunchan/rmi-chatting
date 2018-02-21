import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;
import java.util.*;

import java.rmi.server.*;
import java.rmi.registry.*;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
   
  protected Vector clients;	//������ client���� �����ϴ� ����
  private String file="";	//���۵� ������ ���ϸ� ���� ����
  private InetAddress sender_ipAddress = null;	//������ �����ϴ� client�� IP�ּҸ� �����ϴ� ����
  ChatClient sender;	//������ ������ client�� �����ϴ� ����


  public ChatServerImpl () throws RemoteException {		//ChatServerImple ������
    clients = new Vector ();  
 }

  public void register (ChatClient client) throws RemoteException {	//client�� ������ �� ����Ǵ� �Լ�
    clients.addElement (client);	//client���� ���� ���� ������ client�߰�
    say ("[system] " + client.getName() + " has joined.");
    sendCheck(); //���� ä�ù濡 �������ִ� �ο� üũ �Լ� ȣ��
  }

  public void deregister (ChatClient client) throws RemoteException {	//â�� ���� ��� �ش� client�� ä�ù��� �����ٴ� �޼����� ����ϴ� �Լ�
    say ("[system] " + client.getName() + " has left.");
    clients.removeElement (client);
    sendCheck();	//���� ä�ù濡 �������ִ� �ο� üũ �Լ�
  }

  public void say (String message) {	//��� client�鿡�� �޼����� ����ϱ� ���� �Լ�
    Vector clients = (Vector) this.clients.clone ();
    for (int i = 0; i < clients.size (); ++ i) {
      ChatClient client = (ChatClient) clients.elementAt (i);
      try {
         if(message.contains("[system] ")){
            message = message.substring(9);
            client.said(message);
         }
         else{
             client.said (message);
         }
      } catch (RemoteException ex) {
        this.clients.removeElement (client);
      }
    }
  }

  public ArrayList clientList() throws RemoteException {	//������ client���� �г����� �����ϱ� ���� �Լ�
     ArrayList<String> list = new ArrayList<>();
     for(int i = 0; i < this.clients.size();i++){
        ChatClient client = (ChatClient)clients.elementAt(i);
        list.add(client.getName());
     }
     return list;
    
  }
  
  public void whisper(String message) throws RemoteException{	//client�� ������ �޼����� �ӼӸ��� ��� ����Ǵ� �Լ�
	  int first, second, third;
	  first = message.indexOf("[");
	  second = message.indexOf("/");
	  third = message.indexOf("]");
	  
   	  //�ӼӸ� ���� ��ȣ�� ������ �޼������� ������ ��� �г��Ӱ� �޴� ��� �г���, �����Ϸ��� �޼��� ����
	  String sender_name = message.substring(0, first);	 
	  String receiver_name = message.substring(second+1, third);
	  String msg = message.substring(third+1);
	  
	  ChatClient sender = null, receiver = null;
	  
	  Vector clients = (Vector) this.clients.clone ();
	  for (int i = 0; i < clients.size (); ++ i) {
		  ChatClient client = (ChatClient) clients.elementAt(i);
		  if(client.getName().equals(sender_name)){	//�ӼӸ��� ������ client ����
			  sender = client;
			  break;
		  }
	  }
	  
	  for (int i = 0; i < clients.size (); ++ i) {
		  ChatClient client = (ChatClient) clients.elementAt(i);
		  if(client.getName().equals(receiver_name)){ //�ӼӸ��� �޴� client ����
			  receiver = client;
			  break;
		  }
	  }
	  
	  if(sender != null && receiver != null){
		  sender.said("[�ӼӸ�] "+sender_name+" >> "+receiver_name+" )) "+msg);
		  receiver.said("[�ӼӸ�] "+sender_name+" >> "+receiver_name+" )) "+msg);
	  }
	  else
		  sender.said("��Ȯ�� �Է����ּ���");
	  
	  
  }
  public void setIPAddress(InetAddress sender_ipAddress)throws RemoteException{	//������ �����ϴ� client�� ip�ּ� ����
     this.sender_ipAddress = sender_ipAddress;
  }
  public InetAddress getIPAddress(){	//private������ sender_ipAddress�� �����ϱ� ���� �Լ�
     return this.sender_ipAddress;
  }
  
  
 
  public void uploadSFile(ChatClient send, FileInfo upInfo) throws IOException, ClassNotFoundException{	//Ŭ���̾�Ʈ���� ������ ������ ������ �����ϴ� �Լ�
     
     sender = send;
  
     String filePath = "c:\\fileTransfer\\"+ upInfo.getFilename();
     
     FileOutputStream fos = new FileOutputStream(filePath);	//������ ������ ��ġ ����
     fos.write(upInfo.getFiledata());	//���۹��� ������ ������ ���� ���
     fos.close();
     say("[system] "+sender.getName()+"���� ����"+sender.getFile()+"�� �����̽��ϴ�.");
     Vector clients = (Vector) this.clients.clone ();
     for (int i = 0; i < clients.size (); ++ i) {
        ChatClient client = (ChatClient) clients.elementAt (i);
        client.downloadFile(filePath);	//������ ���� ������ ���� �޴� �Լ�ȣ��
     }
  }
  
  public Object downloadSFile(String filePath) throws IOException, ClassNotFoundException{	//�������� ������ ����Ʈ������ �о�帮�� �Լ�
	     
      File dFile = new File(filePath);
      int len = (int)dFile.length();	//������ ���� ���� ����
      FileInputStream fin = new FileInputStream(dFile);
      byte[] data = new byte[len];
      fin.read(data);	//read�Լ��� ���� ����Ʈ �迭�� data�о����
      FileInfo fileInfo = new FileInfo();
      //FileInfoŬ������ file�� data ����
      fileInfo.setFilename(file);
      fileInfo.setFiledata(data);
     
     return fileInfo;
   } 
   
   
   public void sendCheck(){	//���� ä�ù濡 �������ִ� �ο� üũ �Լ�	
		  Vector clients = (Vector) this.clients.clone ();
		  for (int i = 0; i < clients.size (); ++ i) {
			 ChatClient client = (ChatClient) clients.elementAt (i);
			 try {
				client.personCheck();	//������ client�� üũ�Ͽ� ä�ù� ������ ȭ�鿡 �����ִ� �Լ� ȣ��
			 } catch (RemoteException e) {
				e.printStackTrace();
			 }
		  }
	  }
   
  
  public static void main (String[] args) throws IOException, ServerNotActiveException {
    ChatServerImpl callbackServer = new ChatServerImpl ();
    Registry registry = LocateRegistry.getRegistry ();
    registry.rebind (REGISTRY_NAME, callbackServer);	//���� ��ü�� ����
    
    File dir = new File("c:\\fileTransfer\\");
    if(!dir.exists()){
       dir.mkdirs();
    }
   
  }
  
}