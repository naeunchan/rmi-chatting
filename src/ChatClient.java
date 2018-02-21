/*3학년 1학기 네트워크 프로그래밍 기말 프로젝트
 * RMI를 이용한 채팅과 파일전송 프로그램
 * 소프트웨어학부 20150260 이소영, 20150262 이시현
 * Client Interface */

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;

public interface ChatClient extends Remote{		//Client Interface
   public abstract void setName() throws RemoteException;	//server의 register함수를 호출하는 함수
   public abstract String getName() throws RemoteException;	//private변수인 name에 접근하기 위한 함수
   //public abstract void setFile(InetAddress sender_ipAddress) throws IOException;
   public abstract void said (String message) throws RemoteException;	//전송할 메세지를 메세지 출력창에 출력하는 함수
   public abstract String getFile() throws RemoteException;	//private변수인 file에 접근하기 위한 함수
   public abstract void personCheck() throws RemoteException;	//접속한 client를 체크하여 채팅방 오른쪽 화면에 보여주는 함수
   public abstract void downloadFile(String filePath) throws RemoteException, IOException, ClassNotFoundException;	//상대방이 보낸 파일을 전송 받는 함수
   
}