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
   
  protected Vector clients;	//접속한 client들을 저장하는 변수
  private String file="";	//전송된 파일의 파일명 저장 변수
  private InetAddress sender_ipAddress = null;	//파일을 전송하는 client의 IP주소를 저장하는 변수
  ChatClient sender;	//파일을 전송한 client를 저장하는 변수


  public ChatServerImpl () throws RemoteException {		//ChatServerImple 생성자
    clients = new Vector ();  
 }

  public void register (ChatClient client) throws RemoteException {	//client가 입장할 때 실행되는 함수
    clients.addElement (client);	//client정보 저장 벡터 변수에 client추가
    say ("[system] " + client.getName() + " has joined.");
    sendCheck(); //현재 채팅방에 접속해있는 인원 체크 함수 호출
  }

  public void deregister (ChatClient client) throws RemoteException {	//창을 닫을 경우 해당 client가 채팅방을 나갔다는 메세지를 출력하는 함수
    say ("[system] " + client.getName() + " has left.");
    clients.removeElement (client);
    sendCheck();	//현재 채팅방에 접속해있는 인원 체크 함수
  }

  public void say (String message) {	//모든 client들에게 메세지를 출력하기 위한 함수
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

  public ArrayList clientList() throws RemoteException {	//접속한 client들의 닉네임을 저장하기 위한 함수
     ArrayList<String> list = new ArrayList<>();
     for(int i = 0; i < this.clients.size();i++){
        ChatClient client = (ChatClient)clients.elementAt(i);
        list.add(client.getName());
     }
     return list;
    
  }
  
  public void whisper(String message) throws RemoteException{	//client가 전송한 메세지가 귓속말일 경우 실행되는 함수
	  int first, second, third;
	  first = message.indexOf("[");
	  second = message.indexOf("/");
	  third = message.indexOf("]");
	  
   	  //귓속말 지정 기호를 포함한 메세지에서 보내는 사람 닉네임과 받는 사람 닉네임, 전송하려는 메세지 추출
	  String sender_name = message.substring(0, first);	 
	  String receiver_name = message.substring(second+1, third);
	  String msg = message.substring(third+1);
	  
	  ChatClient sender = null, receiver = null;
	  
	  Vector clients = (Vector) this.clients.clone ();
	  for (int i = 0; i < clients.size (); ++ i) {
		  ChatClient client = (ChatClient) clients.elementAt(i);
		  if(client.getName().equals(sender_name)){	//귓속말을 보내는 client 구분
			  sender = client;
			  break;
		  }
	  }
	  
	  for (int i = 0; i < clients.size (); ++ i) {
		  ChatClient client = (ChatClient) clients.elementAt(i);
		  if(client.getName().equals(receiver_name)){ //귓속말을 받는 client 구분
			  receiver = client;
			  break;
		  }
	  }
	  
	  if(sender != null && receiver != null){
		  sender.said("[귓속말] "+sender_name+" >> "+receiver_name+" )) "+msg);
		  receiver.said("[귓속말] "+sender_name+" >> "+receiver_name+" )) "+msg);
	  }
	  else
		  sender.said("정확히 입력해주세요");
	  
	  
  }
  public void setIPAddress(InetAddress sender_ipAddress)throws RemoteException{	//파일을 전송하는 client의 ip주소 저장
     this.sender_ipAddress = sender_ipAddress;
  }
  public InetAddress getIPAddress(){	//private변수인 sender_ipAddress에 접근하기 위한 함수
     return this.sender_ipAddress;
  }
  
  
 
  public void uploadSFile(ChatClient send, FileInfo upInfo) throws IOException, ClassNotFoundException{	//클라이언트에서 전송한 파일을 서버에 저장하는 함수
     
     sender = send;
  
     String filePath = "c:\\fileTransfer\\"+ upInfo.getFilename();
     
     FileOutputStream fos = new FileOutputStream(filePath);	//복사할 파일의 위치 지정
     fos.write(upInfo.getFiledata());	//전송받은 파일을 서버에 파일 출력
     fos.close();
     say("[system] "+sender.getName()+"님이 파일"+sender.getFile()+"을 보내셨습니다.");
     Vector clients = (Vector) this.clients.clone ();
     for (int i = 0; i < clients.size (); ++ i) {
        ChatClient client = (ChatClient) clients.elementAt (i);
        client.downloadFile(filePath);	//상대방이 보낸 파일을 전송 받는 함수호출
     }
  }
  
  public Object downloadSFile(String filePath) throws IOException, ClassNotFoundException{	//서버에서 파일을 바이트단위로 읽어드리는 함수
	     
      File dFile = new File(filePath);
      int len = (int)dFile.length();	//파일의 길이 저장 변수
      FileInputStream fin = new FileInputStream(dFile);
      byte[] data = new byte[len];
      fin.read(data);	//read함수를 통해 바이트 배열인 data읽어들임
      FileInfo fileInfo = new FileInfo();
      //FileInfo클래스에 file과 data 저장
      fileInfo.setFilename(file);
      fileInfo.setFiledata(data);
     
     return fileInfo;
   } 
   
   
   public void sendCheck(){	//현재 채팅방에 접속해있는 인원 체크 함수	
		  Vector clients = (Vector) this.clients.clone ();
		  for (int i = 0; i < clients.size (); ++ i) {
			 ChatClient client = (ChatClient) clients.elementAt (i);
			 try {
				client.personCheck();	//접속한 client를 체크하여 채팅방 오른쪽 화면에 보여주는 함수 호출
			 } catch (RemoteException e) {
				e.printStackTrace();
			 }
		  }
	  }
   
  
  public static void main (String[] args) throws IOException, ServerNotActiveException {
    ChatServerImpl callbackServer = new ChatServerImpl ();
    Registry registry = LocateRegistry.getRegistry ();
    registry.rebind (REGISTRY_NAME, callbackServer);	//원격 객체에 연결
    
    File dir = new File("c:\\fileTransfer\\");
    if(!dir.exists()){
       dir.mkdirs();
    }
   
  }
  
}