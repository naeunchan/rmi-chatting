import java.awt.*;
import javax.swing.*;
import java.rmi.*;
import java.util.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.*;
import java.rmi.registry.*;

public class ChatClientImpl extends UnicastRemoteObject implements ChatClient, ActionListener{

   protected String host;	//ChatClientImpl을 실행시킬 때 입력받는 arg[0]
   protected Frame frame;
   protected TextField input;	//메세지 입력 창
   protected static TextArea output;	//메세지 출력 창

   protected JPanel pane1;
   protected TextField name_input;	//닉네임 입력 창
   protected JButton btn1;	//닉네임 입력 후 입장 버튼
   protected JButton btn2;	//파일전송 버튼
   protected JLabel label;
   protected JList<Vector> list; //입장한 client를 보여주는 List
   
   protected TextField file_out;//전송할 파일의 경로 출력 창
   protected JButton file_send;//파일 선택 후 보내기 버튼
   protected JPanel pane2;
   protected JPanel pane;
   FileDialog fd;	//전송할 파일 선택 dialog
   String directory="";	//전송할 파일의 경로 저장 변수
   static String file="";	//전송할 파일의 이름 저장 변수
   
   
   ArrayList<String> c_list;	//입장한 client를 저장하는 List
   boolean msg_check = false;	//client가 입장했는지 체크하는 변수
   String name = "";	//client의 닉네임 저장 변수
   
   public ChatClientImpl(String host)throws IOException{
      this.host = host;
      
      //Swing을 이용한 GUI구현
      frame = new Frame("RMI 채팅 프로그램");
      pane1 = new JPanel();
      label = new JLabel("이름을 정하세요.(5자리 이내)");
      name_input = new TextField(5);
      btn1 = new JButton("입장");
      btn2 = new JButton("파일전송");
      output = new TextArea();
      input = new TextField(50);
      input.setText("");
      list = new JList();
      list.setFixedCellWidth(50);
      pane2 = new JPanel();
      file_out = new TextField(40);
      file_out.setEditable(false);
      file_send = new JButton("보내기");
      pane2.add(file_out);
      pane2.add(file_send);
      
      pane = new JPanel();
      pane.setLayout(new BorderLayout());
      
      pane.add(pane2, "Center");
      pane.add(input, "South");

      pane2.setVisible(false);
      
      pane1.add(label);
      pane1.add(name_input);
      pane1.add(btn1);
         
      frame.add(pane1, "North");
      
      frame.add(output, "Center");
      output.setEditable(false);      
      frame.add(pane, "South");
      
      input.setEditable(msg_check);
      
      btn1.addActionListener(new ActionListener(){	//입장 버튼 클릭
          @Override
          public void actionPerformed(ActionEvent arg0) {
         	 if(msg_check == false){	//client가 입장했는지 체크
 				msg_check = true;
 				input.setEditable(msg_check);
 				name = name_input.getText();
 				if(name.equals("")||name.length()>5){	//닉네임을 입력하지 않았거나 닉네임이 5자를 초과할 경우
 					output.append("닉네임을 다시 입력해 주세요\n");
 					name = null;
 					msg_check = false;
 				}
 				else{	//닉네임 입력 조건을 만족 시켰을 때
 					label.setText(name+" 님, 환영합니다.");
 					output.append("**************************\n"
 							+ "귓속말을 보내고 싶으시면 "
 							+ "         \"[w!/귓속말대상 이름]\"\n"
 							+ "표시를 맨 앞에 붙여서 메시지를 보내세요.\n"
 							+ "**************************\n");
 	
 					try {
 						setName();	//server의 register함수를 호출하는 함수
 					} catch (RemoteException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 					name_input.setVisible(false);
 					btn1.setVisible(false);
 					pane1.add(btn2);
 				}		
 			}
          }
      });
      
      btn2.addActionListener(new ActionListener(){	//파일 전송 버튼을 눌렀을 때
         @Override
         public void actionPerformed(ActionEvent arg0) {
            pane2.setVisible(true);
            
            fd = new FileDialog(frame, "", FileDialog.LOAD);	//파일 선택 Dialog생성
            fd.setVisible(true);
            input.setText("");
               
            directory=fd.getDirectory();	//파일 선택 Dialog에서 선택한 파일의 경로 저장
            file=fd.getFile();	//선택한 파일의 이름 저장
            file_out.setText(directory+file);  //전송할 파일의 경로 출력 창에 
         }
      });
      
      file_send.addActionListener(new ActionListener(){	//파일 선택 후 보내기 버튼
         @Override
         public void actionPerformed(ActionEvent arg0) {

            output.append("파일명 : "+file);
            output.append("\n서버에 파일을 전송중입니다.\n");
               
            try{
               File upFile = new File(directory+file);	//서버에 업로드할 파일
               int len = (int)upFile.length();	//서버에 업로드할 파일의 길이 저장 변수
               FileInputStream fin = new FileInputStream(upFile);	//파일로 부터 바이트로 입력받아, 바이트 단위로 출력할 수 있는 클래스
               byte[] data = new byte[len];
               fin.read(data);	//파일의 내용을 바이트 단위로 읽음
               
               FileInfo finfo = new FileInfo();
               //FileInfo클래스에 file과 data 저장
               finfo.setFilename(file);	
               finfo.setFiledata(data);
               
               try {
                  server.setIPAddress(InetAddress.getLocalHost());	//파일 전송하는 client의 IP주소를 서버에  저장
                  uploadFile(finfo);	//서버의 uploadSFile함수를 호출하는 함수
                  output.append("\n서버에 파일 전송을 완료했습니다.\n");
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();
               }
               
            } catch(IOException e){
               
            }     
            input.setText(" ");
            pane2.setVisible(false);       
         }    
      });

      input.addActionListener(this);
      frame.addWindowListener(new WindowAdapter(){	//창닫기(X)버튼을 눌렀을 때 반응하는 리스너
         public void windowOpened(WindowEvent ev){
            input.requestFocus();
         }
         public void windowClosing(WindowEvent ev){
            try{
               stop();	//창을 닫을 경우 stop함수 호출
            }catch(RemoteException ex){
               ex.printStackTrace();
            }
         }
      });   
      frame.pack();//프레임에 부속된 컴포넌트들의 크기에 맞게 프레임 크기를 조정
      
   }
   
   protected ChatServer server;
   public synchronized void start() throws RemoteException, NotBoundException{	//main함수에서 start함수 호출
      if(server == null){
         Registry registry = LocateRegistry.getRegistry(host);
         server = (ChatServer)registry.lookup(ChatServer.REGISTRY_NAME);	//클라이언트로부터 lookup과정을 통해 서버로부터 원격참조객체 생성
         frame.setVisible(true);
      }
   }
   
   public synchronized void stop () throws RemoteException {	//창을 닫을 경우 stop함수 호출
       frame.setVisible (false);
       ChatServer server = this.server;
       this.server = null;
       if (server != null)
         server.deregister (this);	//창을 닫을 경우 해당 client가 채팅방을 나갔다는 메세지를 출력하는 함수 호출
   }
   
   public void setName() throws RemoteException{	//server의 register함수를 호출하는 함수
         server.register(this);
   }
   
   public String getName(){	//private변수인 name에 접근하기 위한 함수
      return this.name;
   }

   public String getFile(){	//private변수인 file에 접근하기 위한 함수
      return this.file;
   }
   /*public void setFile(InetAddress sender_ipAddress) throws IOException{	//
      server.setIPAddress(sender_ipAddress);
      server.register_file(this);
      
   }*/
   public void said (String message) {	//전송할 메세지를 메세지 출력창에 출력하는 함수
     output.append (message + "\n");
     input.setText("");  
   }
	   
	public void actionPerformed (ActionEvent ev) {	//전송하려는 메세지를 체크하는 함수
		try {
			ChatServer server = this.server;
			if (server != null) {
				if(ev.getActionCommand().contains("[w!/"))	//전송하려는 메세지가 귓속말이면
					server.whisper(name + ev.getActionCommand());	//서버의 whisper함수 호출
				else
					server.say (name + " >> " + ev.getActionCommand ());	//일반 메세지이면 서버의 say함수 호출
			    input.setText("");
			}
		} catch (RemoteException ex) {
			input.setVisible (false);
			frame.validate ();
			ex.printStackTrace ();
		}
	}
		

   public void uploadFile(FileInfo finfo) throws IOException,ClassNotFoundException{	//서버의 uploadSFile함수를 호출하는 함수
      server.uploadSFile(this, finfo);
      
   }
   
   public void downloadFile(String filePath) throws RemoteException, IOException, ClassNotFoundException{	//상대방이 보낸 파일을 전송 받는 함수

      if(!(server.getIPAddress().getHostAddress().contains(InetAddress.getLocalHost().getHostAddress()))){	//파일을 전송하려는 client의 ip주소와 나의 ip주소 비교
	      int reply = JOptionPane.showConfirmDialog(null,"파일을 전송받으시겠습니까?","시스템 알림", JOptionPane.OK_CANCEL_OPTION);	//시스템 알림 dialog생성
	         
	         if(reply == JOptionPane.YES_OPTION){	//확인버튼을 누르면
	            FileInfo dInfo = (FileInfo) server.downloadSFile(filePath);	//server의 downloadSFile호출
	            
	            int i = filePath.lastIndexOf("\\");
	           
	            String fileName = filePath.substring(i+1);	//filePath에서 파일명 추출
	            output.append(fileName+"이 저장되었습니다.\n");
	            File dir = new File("c:\\fileTransfer_client\\");	//전송받는 파일의 경로 지정
	            if(!dir.exists()){	//해당 폴더가 존재하지 않을 시 폴더 생성
	               dir.mkdirs();
	            }
	            FileOutputStream fos = new FileOutputStream(dir+"\\"+fileName);	//복사된 파일의 위치 지정
	            fos.write(dInfo.getFiledata());	//파일 출력
	            fos.close();
	            
	         }
	         else{	//취소버튼을 누르면
	        	 file_out.setText("");
	         }
      }
      
   }
   
	public void personCheck(){	//접속한 client를 체크하여 채팅방 오른쪽 화면에 보여주는 함수
		Vector vec = new Vector();
		try {
			c_list = server.clientList();
			vec.clear();
			for(int i = 0; i < c_list.size(); i++){
				vec.addElement(c_list.get(i));
			}
			list.setListData(vec);
			frame.add(list, "East");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
	}
	
    public static void main (String[] args) throws NotBoundException, UnknownHostException, IOException {
    	if (args.length != 1)throw new IllegalArgumentException	//서버 주소를 잘못 입력할 경우
        	("Syntax: ChatClientImpl <host>");
    	ChatClientImpl chatClient = new ChatClientImpl (args[0]);
    	chatClient.start ();
   }
}