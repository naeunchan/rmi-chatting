/* Server Interface */
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;
import java.util.*;

public interface ChatServer extends Remote {
  public static final String REGISTRY_NAME = "rmi://localhost:3333/Callback Server";
  public abstract void register (ChatClient client) throws RemoteException;	//client가 입장할 때 실행되는 함수
  public abstract void deregister (ChatClient client) throws RemoteException;	//창을 닫을 경우 해당 client가 채팅방을 나갔다는 메세지를 출력하는 함수
  //public abstract void register_file(ChatClient client) throws IOException;
  public abstract void say (String message) throws RemoteException;	//모든 client들에게 메세지를 출력하기 위한 함수
  public abstract void setIPAddress(InetAddress sender_ipAddress) throws RemoteException;//파일을 전송하는 client의 ip주소 저장
  public abstract InetAddress getIPAddress()throws RemoteException;	//private변수인 sender_ipAddress에 접근하기 위한 함수
  public abstract ArrayList clientList() throws RemoteException;	//접속한 client들의 닉네임을 저장하기 위한 함수
  public abstract void whisper(String message) throws RemoteException;	//client가 전송한 메세지가 귓속말일 경우 실행되는 함수
  public void uploadSFile(ChatClient sender, FileInfo upInfo) throws IOException, ClassNotFoundException;	//클라이언트에서 전송한 파일을 서버에 저장하는 함수
  public Object downloadSFile(String filePath) throws IOException, ClassNotFoundException;	//서버에서 파일을 바이트단위로 읽어드리는 함수
}
