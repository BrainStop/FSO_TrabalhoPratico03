import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class RobotPlayer extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblRobotplayer;
	private JButton btnGravarTrajetoria;
	private JButton btnReproduzirTrajetoria;
	private JButton btnReproduzirTrajetoriaInversa;
	private JButton btnPararGravaoOu;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	/**
	 * as minhas variáveis
	 */
//	private RobotLego robot;
	private RobotLego robot;
	public boolean gravar_traj;
	public boolean reproduzir_traj;
	public boolean reproduzir_trajInversa;
	public boolean pararReproduzir_gravacao;
	private List<String> arrayTrajetoria;
	public java.io.File ficheiro;
	public FileChannel canal;
	MappedByteBuffer buffer;
	final int BUFFER_MAX = 5000;
	private double VEL;	

 	
	private void inicializarVariaveis() {
		VEL = 0.02;
		robot = new RobotLego();
		gravar_traj = false;
		reproduzir_traj = false;
		reproduzir_trajInversa = false;
		pararReproduzir_gravacao = false;
		arrayTrajetoria = new ArrayList<String>();
	}	
	
	/**
	 * Create the frame.
	 */

	public RobotPlayer() {
		// cria um ficheiro com o nome trajetoria.dat
		ficheiro = new File("trajetoria.dat");
		//cria um canal de comunicação de leitura e escrita
		try {
			canal = new RandomAccessFile(ficheiro, "rw").getChannel();
		} catch (FileNotFoundException e) {e.printStackTrace(); }
		// mapeia para memória o conteúdo do ficheiro
		try {
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_MAX);
		} catch (IOException e) { e.printStackTrace(); }

		inicializarVariaveis();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 518, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblRobotplayer = new JLabel("RobotPlayer");
		lblRobotplayer.setFont(new Font("Microsoft YaHei Light", Font.BOLD | Font.ITALIC, 18));
		lblRobotplayer.setBounds(123, 13, 124, 33);
		contentPane.add(lblRobotplayer);
		
		btnGravarTrajetoria = new JButton("Gravar Trajetoria");
		btnGravarTrajetoria.setBackground(SystemColor.inactiveCaption);
		btnGravarTrajetoria.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gravar_traj = !gravar_traj;
				debugPrint("gravar_traj -> " + gravar_traj);
				if(gravar_traj == false){
					escreverFicheiro(arrayTrajetoria);
					arrayTrajetoria.clear();
				}
			}
			
		});
		btnGravarTrajetoria.setBounds(12, 67, 138, 33);
		contentPane.add(btnGravarTrajetoria);
		
		btnReproduzirTrajetoria = new JButton("Reproduzir Trajetoria");
		btnReproduzirTrajetoria.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reproduzir_traj = !reproduzir_traj;
				debugPrint("reproduzir_traj -> " + reproduzir_traj);
				reproduzirTrajetoria(filtrarInformacao(lerFicheiro()));
				
			}
		});
		btnReproduzirTrajetoria.setBackground(SystemColor.inactiveCaption);
		btnReproduzirTrajetoria.setBounds(12, 113, 162, 33);
		contentPane.add(btnReproduzirTrajetoria);
		
		btnReproduzirTrajetoriaInversa = new JButton("Reproduzir Trajetoria Inversa");
		btnReproduzirTrajetoriaInversa.setBackground(SystemColor.inactiveCaption);
		btnReproduzirTrajetoriaInversa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reproduzir_trajInversa = !reproduzir_trajInversa;
				debugPrint("reproduzir_trajInversa -> " + reproduzir_trajInversa);
				reproduzirTrajetoriaInversa(filtrarInformacaoInversa(lerFicheiro()));
			}
		});
		btnReproduzirTrajetoriaInversa.setBounds(12, 159, 225, 33);
		contentPane.add(btnReproduzirTrajetoriaInversa);
		
		btnPararGravaoOu = new JButton("Parar grava\u00E7\u00E3o ou reprodu\u00E7\u00E3o");
		btnPararGravaoOu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pararReproduzir_gravacao = !pararReproduzir_gravacao;
				debugPrint("pararReproduzir_gravaçao -> " + pararReproduzir_gravacao);
				pararReproduzir();
			}
		});
		btnPararGravaoOu.setBackground(SystemColor.inactiveCaption);
		btnPararGravaoOu.setBounds(12, 203, 233, 33);
		contentPane.add(btnPararGravaoOu);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(268, 49, 220, 219);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
				
		setVisible(true);
	}
	
	public void AjustarVMD(int d) {
		robot.AjustarVMD(d);
	}
	
	public void AjustarVME(int d) {
		robot.AjustarVME(d);

	}

	public boolean OpenNXT(String nomeRobot) {
		return robot.OpenNXT(nomeRobot);
	}

	public void SetSensorTouch(int s2) {
		robot.SetSensorTouch(s2);
	}

	public void CurvarDireita(int raio, int angulo) {
		robot.CurvarDireita(raio, angulo);
		robot.Parar(false);
		//System.out.println("entrou e reproduzir o curvarDireita");
		if(gravar_traj) arrayTrajetoria.add("CurvarDireita;"+raio+";"+angulo+"");	
	}

	public void CurvarEsquerda(int raio, int angulo) {
		robot.CurvarEsquerda(raio, angulo);
		robot.Parar(false);
		////System.out.println("entrou e reproduzir o curvarEsquerda");
		if(gravar_traj) arrayTrajetoria.add("CurvarEsquerda;"+raio+";"+angulo+"");	
	}

	public void Parar(boolean b) {
		robot.Parar(b);
		//System.out.println("entrou e reproduziu o parar");
		if(gravar_traj) arrayTrajetoria.add("Parar;"+b+"");	
	}

	public void SetSensorLowSpeed(int s1) {
		robot.SetSensorLowspeed(s1);
	}

	public void Reta(int dist) {
		//System.out.println("entrou e reproduziu o reta");
		robot.Reta(dist);
		robot.Parar(false);
	    if(gravar_traj) arrayTrajetoria.add("Reta;"+dist+"");
	}

	private String lerFicheiro(){
		String msg = new String();
		char c;
		buffer.position(0);
		while ((c = buffer.getChar()) != '\0'){	
			msg +=c;
		}
			
		return msg;
	}
	
	private void escreverFicheiro(List<String> traj){
		char c;
		buffer.position(0);
		for(int i = 0; i < traj.size(); i++){
			String aux = traj.get(i);
			if(i >= 1){
				buffer.putChar(',');
			}
			for (int b = 0 ; b < aux.length() ; ++b){
				c = aux.charAt(b);
				buffer.putChar(c);
			}
		}
		buffer.putChar('\0');
		
	}
	
	// fecha o canal entre o buffer e o ficheiro
	private void fecharCanal() {
		try {
			canal.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> filtrarInformacao(String msg){
		//System.out.println("msg: " + msg);
		List<String> listaTraj = new ArrayList<String>(Arrays.asList(msg.split("[,;]")));
		//System.out.println("listaTraj: "+listaTraj);
		//System.out.println(listaTraj.size());
		return listaTraj;
	}
	
	private List<String> filtrarInformacaoInversa(String msg){
		List<String> listaTraj = new ArrayList<String>(Arrays.asList(msg.split("[,;]")));
		Collections.reverse(listaTraj);
		//System.out.println("listaTrajInversa: " + listaTraj);
		return listaTraj;
	}
	
	private void reproduzirTrajetoria(List<String> listaTraj){
		double td = 0;
		double dist = 0;
		for(int i = 0; i < listaTraj.size(); i++){
			System.out.println(listaTraj.get(i));
			switch(listaTraj.get(i)){
			case "CurvarDireita":
				CurvarDireita(Integer.parseInt(listaTraj.get(i+1)), Integer.parseInt(listaTraj.get(i+2)));
				System.out.println("raio: "+ listaTraj.get(i+1) + "   angulo: " + listaTraj.get(i+2));
				dist   = Integer.parseInt(listaTraj.get(i+2)) * Math.PI / 180 * Integer.parseInt(listaTraj.get(i+1));
				td = dist / VEL;
				break;
			case "CurvarEsquerda":
				CurvarEsquerda(Integer.parseInt(listaTraj.get(i+1)), Integer.parseInt(listaTraj.get(i+2)));
				System.out.println("raio: "+ listaTraj.get(i+1) + "   angulo: " + listaTraj.get(i+2));
				dist  = Integer.parseInt(listaTraj.get(i+2)) * Math.PI / 180 * Integer.parseInt(listaTraj.get(i+1));
				td = dist / VEL;
				break;
			case "Parar":
				Parar(Boolean.getBoolean(listaTraj.get(i+1)));
				System.out.println("parar: "+ Boolean.getBoolean(listaTraj.get(i+1)));
				td = 100;
				break;
			case "Reta":
				Reta(Integer.valueOf(listaTraj.get(i+1)));
				System.out.println("Reta: " + Integer.valueOf(listaTraj.get(i+1)));
				td = Integer.valueOf(listaTraj.get(i+1)) / VEL;
				break;
			default:
				break;
			}
			System.out.println("TD:" + td);
			try {
				Thread.sleep((long)td);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void reproduzirTrajetoriaInversa(List<String> listaTraj){
		double td = 0;
		double dist = 0;
		for(int i = 0; i < listaTraj.size(); i++){
			switch(listaTraj.get(i)){
			case "CurvarDireita":
				CurvarEsquerda(Integer.parseInt(listaTraj.get(i-1)), Integer.parseInt(listaTraj.get(i-2)));
				dist   = Integer.parseInt(listaTraj.get(i-2)) * Math.PI / 180 * Integer.parseInt(listaTraj.get(i-1));
				td = dist / VEL;
				break;
			case "CurvarEsquerda":
				CurvarDireita(Integer.parseInt(listaTraj.get(i-1)), Integer.parseInt(listaTraj.get(i-2)));
				dist   = Integer.parseInt(listaTraj.get(i-2)) * Math.PI / 180 * Integer.parseInt(listaTraj.get(i-1));
				td = dist / VEL;
				break;
			case "Parar":
				Parar(Boolean.getBoolean(listaTraj.get(i-1)));
				td = 100;
				break;
			case "Reta":
				Reta(Integer.valueOf(listaTraj.get(i-1)));
				td = Integer.valueOf(listaTraj.get(i-1)) / VEL;
				break;
			default:
				break;
			}
			System.out.println("TD:" + td);
			try {
				Thread.sleep((long)td);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Parar(false);
	}
	
	private void pararReproduzir(){
		robot.Parar(true);
		fecharCanal();
	}
	/**
	 * Imprimir mensagens na caixa de texte "log".
	 * 
	 * @param s String com a mensagem.
	 */
	private void debugPrint(String s) {
		textArea.append(s + "\n");
	}

	private void run() {
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		RobotPlayer frame = new RobotPlayer();
		frame.run();
	}

	public void CloseNXT() {
		robot.CloseNXT();		
	}

	public int SensorUS(int port) {
		return robot.SensorUS(port);
	}

	public void SetSensorLowspeed(int port) {
		robot.SetSensorLowspeed(port);
	}

	public int Sensor(int port) {
		return robot.Sensor(port);
	}
}
