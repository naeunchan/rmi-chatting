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

   protected String host;	//ChatClientImpl�� �����ų �� �Է¹޴� arg[0]
   protected Frame frame;
   protected TextField input;	//�޼��� �Է� â
   protected static TextArea output;	//�޼��� ��� â

   protected JPanel pane1;
   protected TextField name_input;	//�г��� �Է� â
   protected JButton btn1;	//�г��� �Է� �� ���� ��ư
   protected JButton btn2;	//�������� ��ư
   protected JLabel label;
   protected JList<Vector> list; //������ client�� �����ִ� List
   
   protected TextField file_out;//������ ������ ��� ��� â
   protected JButton file_send;//���� ���� �� ������ ��ư
   protected JPanel pane2;
   protected JPanel pane;
   FileDialog fd;	//������ ���� ���� dialog
   String directory="";	//������ ������ ��� ���� ����
   static String file="";	//������ ������ �̸� ���� ����
   
   
   ArrayList<String> c_list;	//������ client�� �����ϴ� List
   boolean msg_check = false;	//client�� �����ߴ��� üũ�ϴ� ����
   String name = "";	//client�� �г��� ���� ����
   
   public ChatClientImpl(String host)throws IOException{
      this.host = host;
      
      //Swing�� �̿��� GUI����
      frame = new Frame("RMI ä�� ���α׷�");
      pane1 = new JPanel();
      label = new JLabel("�̸��� ���ϼ���.(5�ڸ� �̳�)");
      name_input = new TextField(5);
      btn1 = new JButton("����");
      btn2 = new JButton("��������");
      output = new TextArea();
      input = new TextField(50);
      input.setText("");
      list = new JList();
      list.setFixedCellWidth(50);
      pane2 = new JPanel();
      file_out = new TextField(40);
      file_out.setEditable(false);
      file_send = new JButton("������");
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
      
      btn1.addActionListener(new ActionListener(){	//���� ��ư Ŭ��
          @Override
          public void actionPerformed(ActionEvent arg0) {
         	 if(msg_check == false){	//client�� �����ߴ��� üũ
 				msg_check = true;
 				input.setEditable(msg_check);
 				name = name_input.getText();
 				if(name.equals("")||name.length()>5){	//�г����� �Է����� �ʾҰų� �г����� 5�ڸ� �ʰ��� ���
 					output.append("�г����� �ٽ� �Է��� �ּ���\n");
 					name = null;
 					msg_check = false;
 				}
 				else{	//�г��� �Է� ������ ���� ������ ��
 					label.setText(name+" ��, ȯ���մϴ�.");
 					output.append("**************************\n"
 							+ "�ӼӸ��� ������ �����ø� "
 							+ "         \"[w!/�ӼӸ���� �̸�]\"\n"
 							+ "ǥ�ø� �� �տ� �ٿ��� �޽����� ��������.\n"
 							+ "**************************\n");
 	
 					try {
 						setName();	//server�� register�Լ��� ȣ���ϴ� �Լ�
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
      
      btn2.addActionListener(new ActionListener(){	//���� ���� ��ư�� ������ ��
         @Override
         public void actionPerformed(ActionEvent arg0) {
            pane2.setVisible(true);
            
            fd = new FileDialog(frame, "", FileDialog.LOAD);	//���� ���� Dialog����
            fd.setVisible(true);
            input.setText("");
               
            directory=fd.getDirectory();	//���� ���� Dialog���� ������ ������ ��� ����
            file=fd.getFile();	//������ ������ �̸� ����
            file_out.setText(directory+file);  //������ ������ ��� ��� â�� 
         }
      });
      
      file_send.addActionListener(new ActionListener(){	//���� ���� �� ������ ��ư
         @Override
         public void actionPerformed(ActionEvent arg0) {

            output.append("���ϸ� : "+file);
            output.append("\n������ ������ �������Դϴ�.\n");
               
            try{
               File upFile = new File(directory+file);	//������ ���ε��� ����
               int len = (int)upFile.length();	//������ ���ε��� ������ ���� ���� ����
               FileInputStream fin = new FileInputStream(upFile);	//���Ϸ� ���� ����Ʈ�� �Է¹޾�, ����Ʈ ������ ����� �� �ִ� Ŭ����
               byte[] data = new byte[len];
               fin.read(data);	//������ ������ ����Ʈ ������ ����
               
               FileInfo finfo = new FileInfo();
               //FileInfoŬ������ file�� data ����
               finfo.setFilename(file);	
               finfo.setFiledata(data);
               
               try {
                  server.setIPAddress(InetAddress.getLocalHost());	//���� �����ϴ� client�� IP�ּҸ� ������  ����
                  uploadFile(finfo);	//������ uploadSFile�Լ��� ȣ���ϴ� �Լ�
                  output.append("\n������ ���� ������ �Ϸ��߽��ϴ�.\n");
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
      frame.addWindowListener(new WindowAdapter(){	//â�ݱ�(X)��ư�� ������ �� �����ϴ� ������
         public void windowOpened(WindowEvent ev){
            input.requestFocus();
         }
         public void windowClosing(WindowEvent ev){
            try{
               stop();	//â�� ���� ��� stop�Լ� ȣ��
            }catch(RemoteException ex){
               ex.printStackTrace();
            }
         }
      });   
      frame.pack();//�����ӿ� �μӵ� ������Ʈ���� ũ�⿡ �°� ������ ũ�⸦ ����
      
   }
   
   protected ChatServer server;
   public synchronized void start() throws RemoteException, NotBoundException{	//main�Լ����� start�Լ� ȣ��
      if(server == null){
         Registry registry = LocateRegistry.getRegistry(host);
         server = (ChatServer)registry.lookup(ChatServer.REGISTRY_NAME);	//Ŭ���̾�Ʈ�κ��� lookup������ ���� �����κ��� ����������ü ����
         frame.setVisible(true);
      }
   }
   
   public synchronized void stop () throws RemoteException {	//â�� ���� ��� stop�Լ� ȣ��
       frame.setVisible (false);
       ChatServer server = this.server;
       this.server = null;
       if (server != null)
         server.deregister (this);	//â�� ���� ��� �ش� client�� ä�ù��� �����ٴ� �޼����� ����ϴ� �Լ� ȣ��
   }
   
   public void setName() throws RemoteException{	//server�� register�Լ��� ȣ���ϴ� �Լ�
         server.register(this);
   }
   
   public String getName(){	//private������ name�� �����ϱ� ���� �Լ�
      return this.name;
   }

   public String getFile(){	//private������ file�� �����ϱ� ���� �Լ�
      return this.file;
   }
   /*public void setFile(InetAddress sender_ipAddress) throws IOException{	//
      server.setIPAddress(sender_ipAddress);
      server.register_file(this);
      
   }*/
   public void said (String message) {	//������ �޼����� �޼��� ���â�� ����ϴ� �Լ�
     output.append (message + "\n");
     input.setText("");  
   }
	   
	public void actionPerformed (ActionEvent ev) {	//�����Ϸ��� �޼����� üũ�ϴ� �Լ�
		try {
			ChatServer server = this.server;
			if (server != null) {
				if(ev.getActionCommand().contains("[w!/"))	//�����Ϸ��� �޼����� �ӼӸ��̸�
					server.whisper(name + ev.getActionCommand());	//������ whisper�Լ� ȣ��
				else
					server.say (name + " >> " + ev.getActionCommand ());	//�Ϲ� �޼����̸� ������ say�Լ� ȣ��
			    input.setText("");
			}
		} catch (RemoteException ex) {
			input.setVisible (false);
			frame.validate ();
			ex.printStackTrace ();
		}
	}
		

   public void uploadFile(FileInfo finfo) throws IOException,ClassNotFoundException{	//������ uploadSFile�Լ��� ȣ���ϴ� �Լ�
      server.uploadSFile(this, finfo);
      
   }
   
   public void downloadFile(String filePath) throws RemoteException, IOException, ClassNotFoundException{	//������ ���� ������ ���� �޴� �Լ�

      if(!(server.getIPAddress().getHostAddress().contains(InetAddress.getLocalHost().getHostAddress()))){	//������ �����Ϸ��� client�� ip�ּҿ� ���� ip�ּ� ��
	      int reply = JOptionPane.showConfirmDialog(null,"������ ���۹����ðڽ��ϱ�?","�ý��� �˸�", JOptionPane.OK_CANCEL_OPTION);	//�ý��� �˸� dialog����
	         
	         if(reply == JOptionPane.YES_OPTION){	//Ȯ�ι�ư�� ������
	            FileInfo dInfo = (FileInfo) server.downloadSFile(filePath);	//server�� downloadSFileȣ��
	            
	            int i = filePath.lastIndexOf("\\");
	           
	            String fileName = filePath.substring(i+1);	//filePath���� ���ϸ� ����
	            output.append(fileName+"�� ����Ǿ����ϴ�.\n");
	            File dir = new File("c:\\fileTransfer_client\\");	//���۹޴� ������ ��� ����
	            if(!dir.exists()){	//�ش� ������ �������� ���� �� ���� ����
	               dir.mkdirs();
	            }
	            FileOutputStream fos = new FileOutputStream(dir+"\\"+fileName);	//����� ������ ��ġ ����
	            fos.write(dInfo.getFiledata());	//���� ���
	            fos.close();
	            
	         }
	         else{	//��ҹ�ư�� ������
	        	 file_out.setText("");
	         }
      }
      
   }
   
	public void personCheck(){	//������ client�� üũ�Ͽ� ä�ù� ������ ȭ�鿡 �����ִ� �Լ�
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
    	if (args.length != 1)throw new IllegalArgumentException	//���� �ּҸ� �߸� �Է��� ���
        	("Syntax: ChatClientImpl <host>");
    	ChatClientImpl chatClient = new ChatClientImpl (args[0]);
    	chatClient.start ();
   }
}