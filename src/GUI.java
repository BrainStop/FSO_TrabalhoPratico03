import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.Semaphore;
/**
 * @author Gonçalo Oliveira
 * @author Miguel Marçal
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tF_NomeRobo;
	private JTextField tF_MotorEsquerdo;
	private JTextField tF_MotorDireito;
	private JTextField tF_Distacia;
	private JTextField tF_Raio;
	private JTextField tF_Angulo;
	private JTextArea tA_debug;
	private JButton jB_Esquerda;
	private JButton jB_Parar;
	private JButton jB_Frente;
	private JButton jB_Retroceder;
	private JButton jB_Direita;
	private JButton jB_Gravar;
	private JButton jB_Carregar;
	private JRadioButton jRB_OnOff;
	private JCheckBox cB_Debug;
	private JCheckBox cB_Vaguear;
	private JCheckBox cB_Evitar;
	private JCheckBox cB_Gestor;
	private JCheckBox cB_SeguirParede;
	
	/* As minhas variaveis */
	/**
	 * String contem o nome do robot
	 */
	private String nomeRobot;
	
	/**
	 * Variaveis que vão ser utilizadas nos metodos do robot
	 */
	private int offsetMotorEsq, offsetMotorDir, distancia, angulo, raio;
	
	/**
	 * Variavel utilizada para definir o estado da ligação com o robot
	 */
	private boolean onOff;
	
	/**
	 * Variavel utilizada para definir a ligacao do gestor
	 */
	private boolean gestor;
	
	/**
	 * Variavel utilizada para definir a ligacao do evitar
	 */
	private boolean evitar;
	
	/**
	 * Variavel utilizada para definir a ligacao do vaguear
	 */
	private boolean vaguear;
	
	/**
	 * Variavel utilizada para definir a ligacao do seguir parede
	 */
	private boolean seguirParede;
	
	/**
	 * Variavel utilizada para definir se serão imprimidos os comandos no log 
	 */
	private boolean debug;
	
	/**
	 * Variavel que conterá uma instancia do RobotLego usada para comunicar com
	 * o robot
	 */
	private RobotPlayer robot;
	
	/**
	 * Comportamento Vaguear
	 */
	private Vaguear vag;
	
	/**
	 * Comportamento Evitar
	 */
	private Evitar evi;
	
	/**
	 * Comportametno Seguir Parede
	 */
	private SeguirParede sP;
	
	/**
	 * Tarefa que produz distancias
	 */
	private ProdutorDistancias prodDist;
	
	/**
	 * Comportamento Gestor
	 */
	private Gestor ges;
	
	/**
	 * Variavel usada para indicar o numero da linha introduzida no log 
	 */
	private int lineNumber = 0;
	
	/**
	 * Canal de comunicação para ler e escrever em ficheiros texto
	 */
	private CanalComunicacao cc;
	
	/**
	 * Metodo que iniciam as variaveis que
	 * não fazem parte da interface grafica.
	 */
	private void inicializarVariaveis() {
		Semaphore exclusao = new Semaphore(1);
		nomeRobot = new String("Guia3");
		distancia = 0;
		angulo    = 0;
		raio      = 0;
		offsetMotorEsq = 0;
		offsetMotorDir = 0;
		onOff = false;
		debug = true;
		robot = new RobotPlayer();
		vaguear = false;
		evitar  = false;
		seguirParede = false;
		gestor = false;
		prodDist = new ProdutorDistancias(robot, exclusao);
		prodDist.start();
		vag = new Vaguear(robot, exclusao);
		vag.start();
		sP = new SeguirParede(robot, prodDist, exclusao);
		sP.start();
		ges = new Gestor(prodDist, vag, sP);
		ges.start();
		evi = new Evitar(robot, exclusao);
		evi.start();
	}
	
    /**
	 * Configura os JButtons da interface para 
	 * activo ou desativo.
     *
     * @param b um booleano que define se os JButtons
     *  da interface estão activos ou não.
     */
	private void setEnable(boolean b) {
		jB_Esquerda.setEnabled(b);
		jB_Direita.setEnabled(b);
		jB_Parar.setEnabled(b);
		jB_Frente.setEnabled(b);
		jB_Retroceder.setEnabled(b);
		cB_Vaguear.setEnabled(b);
		cB_Evitar.setEnabled(b);
		cB_Gestor.setEnabled(b);
		cB_SeguirParede.setEnabled(b);
	}
	
	/**
	 * Cria a interface que controla o RobotLego.
	 */
	public GUI() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(onOff)
					robot.Parar(true);
				try{
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				robot.CloseNXT();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
		});
		
		/* Inicializa as minha variaveis */
		inicializarVariaveis();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel jL_NomeRobo = new JLabel("Nome do Robo");
		jL_NomeRobo.setHorizontalAlignment(SwingConstants.CENTER);
		jL_NomeRobo.setBounds(271, 11, 86, 14);
		contentPane.add(jL_NomeRobo);
		
		tF_NomeRobo = new JTextField();
		tF_NomeRobo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				nomeRobot = tF_NomeRobo.getText();
				debugPrint("nome do robo -> " + nomeRobot);
			}

		});
		tF_NomeRobo.setHorizontalAlignment(SwingConstants.CENTER);
		tF_NomeRobo.setBounds(208, 36, 201, 20);
		contentPane.add(tF_NomeRobo);
		tF_NomeRobo.setColumns(10);
		/* Meu codigo */
		tF_NomeRobo.setText(nomeRobot);
		
		jRB_OnOff = new JRadioButton("ON/OFF");
		jRB_OnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!onOff) {
					onOff = robot.OpenNXT(nomeRobot);
					if(onOff) {
						debugPrint("Ligação foi estabelecida.");
						robot.AjustarVMD(offsetMotorDir);
						robot.AjustarVME(offsetMotorEsq);
						prodDist.ativar();
					}
					else debugPrint("Ligação não foi estabelecida.");
				}
				else {
					robot.CloseNXT();
					onOff = false;
					debugPrint("Ligação foi terminada.");
				}
				setEnable(onOff);
				jRB_OnOff.setSelected(onOff);
			}
		});
		jRB_OnOff.setBounds(431, 19, 109, 23);
		contentPane.add(jRB_OnOff);
		/* O meu Codigo */
		jRB_OnOff.setSelected(onOff);
		
		jB_Frente = new JButton("Frente");
		jB_Frente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desativarThreads();
				debugPrint("FRENTE dis:"+distancia);
				robot.Reta(distancia);
				robot.Parar(false);
				ativarTreads();
			}
		});
		jB_Frente.setBackground(new Color(100, 149, 237));
		jB_Frente.setBounds(255, 67, 120, 70);
		contentPane.add(jB_Frente);
		
		jB_Esquerda = new JButton("Esquerda");
		jB_Esquerda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desativarThreads();
				debugPrint("Esquerda dis:"+distancia+" ang:"+angulo+" raio:"+raio);
				robot.CurvarEsquerda(raio, angulo);
				robot.Parar(false);
				ativarTreads();
			}
		});
		jB_Esquerda.setBackground(new Color(100, 149, 237));
		jB_Esquerda.setBounds(170, 148, 90, 70);
		contentPane.add(jB_Esquerda);
		
		jB_Direita = new JButton("Direita");
		jB_Direita.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					desativarThreads();
					debugPrint("DIREITA dis:"+distancia+" ang:"+angulo+" raio:"+raio);
					robot.CurvarDireita(raio, angulo);
					robot.Parar(false);
					ativarTreads();
			}
		});
		jB_Direita.setBackground(new Color(100, 149, 237));
		jB_Direita.setBounds(370, 148, 90, 70);
		contentPane.add(jB_Direita);
		
		jB_Retroceder = new JButton("Retroceder");
		jB_Retroceder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desativarThreads();
				debugPrint("RETROCEDER dis:"+distancia);
				System.out.println("RETROCEDER");
				robot.Reta(-distancia);
				robot.Parar(false);
				ativarTreads();
			}
		});
		jB_Retroceder.setBackground(new Color(100, 149, 237));
		jB_Retroceder.setBounds(255, 229, 120, 70);
		contentPane.add(jB_Retroceder);
		
		jB_Parar = new JButton("Parar");
		jB_Parar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desativarThreads();
				debugPrint("PARAR");
				robot.Parar(true);
				ativarTreads();
			}
		});
		jB_Parar.setBackground(new Color(255, 0, 0));
		jB_Parar.setBounds(270, 148, 90, 70);
		contentPane.add(jB_Parar);
		
		cB_Debug = new JCheckBox("Debug");
		cB_Debug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debug = !debug;
				cB_Debug.setSelected(debug);
			}
		});
		cB_Debug.setBounds(546, 276, 68, 23);
		contentPane.add(cB_Debug);
		/* O meu Codigo */
		cB_Debug.setSelected(debug);
		
		JLabel jL_MotorEsquerdo = new JLabel("Motor Esquerdo");
		jL_MotorEsquerdo.setHorizontalAlignment(SwingConstants.CENTER);
		jL_MotorEsquerdo.setBounds(6, 82, 105, 20);
		contentPane.add(jL_MotorEsquerdo);
		
		JLabel jL_MotorDireito = new JLabel("Motor Direito");
		jL_MotorDireito.setHorizontalAlignment(SwingConstants.CENTER);
		jL_MotorDireito.setBounds(518, 82, 96, 20);
		contentPane.add(jL_MotorDireito);
		
		tF_MotorEsquerdo = new JTextField();
		tF_MotorEsquerdo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				offsetMotorEsq = Integer.parseInt(tF_MotorEsquerdo.getText());
				debugPrint("offsetEsq -> " + offsetMotorEsq);
				robot.AjustarVME(offsetMotorEsq);	
			}
		});
		tF_MotorEsquerdo.setHorizontalAlignment(SwingConstants.CENTER);
		tF_MotorEsquerdo.setBounds(25, 102, 59, 20);
		contentPane.add(tF_MotorEsquerdo);
		tF_MotorEsquerdo.setColumns(10);
		/* O meu codigo */
		tF_MotorEsquerdo.setText(String.valueOf(offsetMotorEsq));
		
		
		tF_MotorDireito = new JTextField();
		tF_MotorDireito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				offsetMotorDir = Integer.parseInt(tF_MotorDireito.getText());
				debugPrint("offsetDir -> " + offsetMotorDir);
				robot.AjustarVMD(offsetMotorDir);
			}
		});
		tF_MotorDireito.setHorizontalAlignment(SwingConstants.CENTER);
		tF_MotorDireito.setColumns(10);
		tF_MotorDireito.setBounds(541, 102, 59, 20);
		contentPane.add(tF_MotorDireito);
		/* O meu codigo */
		tF_MotorDireito.setText(String.valueOf(offsetMotorDir));
		
		JLabel jL_Distancia = new JLabel("Distancia");
		jL_Distancia.setBounds(6, 164, 86, 14);
		contentPane.add(jL_Distancia);
		
		JLabel jL_Raio = new JLabel("Raio");
		jL_Raio.setBounds(6, 192, 86, 14);
		contentPane.add(jL_Raio);
		
		tF_Distacia = new JTextField();
		tF_Distacia.setHorizontalAlignment(SwingConstants.CENTER);
		tF_Distacia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				distancia = Integer.parseInt(tF_Distacia.getText());
				debugPrint("Distancia -> " + distancia);
			}
		});
		tF_Distacia.setBounds(78, 161, 62, 20);
		contentPane.add(tF_Distacia);
		tF_Distacia.setColumns(10);
		/* O meu codigo */
		tF_Distacia.setText(String.valueOf(distancia));
		
		tF_Raio = new JTextField();
		tF_Raio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				raio = Integer.parseInt(tF_Raio.getText());
				debugPrint("Raio -> " + raio);
			}
		});
		tF_Raio.setHorizontalAlignment(SwingConstants.CENTER);
		tF_Raio.setBounds(78, 191, 62, 20);
		contentPane.add(tF_Raio);
		tF_Raio.setColumns(10);
		/* O meu codigo */
		tF_Raio.setText(String.valueOf(raio));
		
		JLabel jL_Angulo = new JLabel("Angulo");
		jL_Angulo.setBounds(6, 224, 86, 14);
		contentPane.add(jL_Angulo);
		
		tF_Angulo = new JTextField();
		tF_Angulo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				angulo = Integer.parseInt(tF_Angulo.getText());
				debugPrint("Angulo -> " + angulo);
			}
		});
		tF_Angulo.setHorizontalAlignment(SwingConstants.CENTER);
		tF_Angulo.setBounds(78, 221, 62, 20);
		contentPane.add(tF_Angulo);
		tF_Angulo.setColumns(10);
		/* O meu codigo */
		tF_Angulo.setText(String.valueOf(angulo));
		
		JScrollPane sP_Debug = new JScrollPane();
		sP_Debug.setBounds(10, 306, 604, 125);
		contentPane.add(sP_Debug);
		
		tA_debug = new JTextArea();
		tA_debug.setEditable(false);
		sP_Debug.setViewportView(tA_debug);
		
		JLabel lblLog = new JLabel("Log:");
		lblLog.setBounds(10, 280, 46, 14);
		contentPane.add(lblLog);
		
		cB_Vaguear = new JCheckBox("Vaguear");
		cB_Vaguear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!vaguear) {
					vag.ativar();
					vaguear = true;
					cB_Vaguear.setSelected(vaguear);
				} else {
					vag.desativar();
					vaguear = false;
					cB_Vaguear.setSelected(vaguear);
				}
			}
		});
		cB_Vaguear.setBounds(499, 148, 115, 23);
		contentPane.add(cB_Vaguear);
		
		cB_Evitar = new JCheckBox("Evitar");
		cB_Evitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!evitar) {
					System.out.println("Ativa o evitar");
					evi.ativar();
					evitar = true;
					cB_Evitar.setSelected(evitar);
				} else {
					System.out.println("Desativa o evitar");
					evi.desativar();
					evitar = false;
					cB_Evitar.setSelected(evitar);
				}
			}
		});
		cB_Evitar.setBounds(499, 172, 115, 23);
		contentPane.add(cB_Evitar);
		
		cB_Gestor = new JCheckBox("Gestor");
		cB_Gestor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!gestor) {
					System.out.println("I: Ativa o Gestor");
					ges.ativar();
					gestor = true;
					cB_Gestor.setSelected(gestor);
				} else {
					System.out.println("I: Desativa o Gestor");
					ges.desativar();
					gestor = false;
					cB_Gestor.setSelected(gestor);
				}
			}
		});
		cB_Gestor.setBounds(499, 221, 115, 21);
		contentPane.add(cB_Gestor);
		
		cB_SeguirParede = new JCheckBox("Seguir Parede");
		cB_SeguirParede.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!seguirParede) {
					System.out.println("Ativa o SeguirParede");
					sP.ativar();
					seguirParede = true;
					cB_SeguirParede.setSelected(seguirParede);
				} else {
					System.out.println("Desativa o SeguirParede");
					sP.desativar();
					seguirParede = false;
					cB_SeguirParede.setSelected(seguirParede);
				}
			}
		});
		cB_SeguirParede.setBounds(499, 195, 115, 23);
		contentPane.add(cB_SeguirParede);
		
		jB_Gravar = new JButton("Gravar");
		jB_Gravar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gravar();
			}
		});
		jB_Gravar.setBounds(6, 19, 89, 23);
		contentPane.add(jB_Gravar);
		
		jB_Carregar = new JButton("Carregar");
		jB_Carregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregar();
			}
		});
		jB_Carregar.setBounds(109, 19, 89, 23);
		contentPane.add(jB_Carregar);
		
		/* Desativa os botões interface */
		setEnable(false);
		carregar();
		setVisible(true);
	}
	/**
	 * Imprimir mensagens na caixa de texte "log".
	 * 
	 * @param s String com a mensagem.
	 */
	private void debugPrint(String s) {
		if(debug) tA_debug.append(lineNumber++ +" - "+ s + "\n");
	}
	
	private void desativarThreads() {
		if(vaguear) vag.desativar();
	}
	
	private void ativarTreads() {
		if(vaguear) vag.ativar();
	}
	
	private void gravar() {
		cc = new CanalComunicacao(nomeRobot+".dat");
		String msg = distancia+";"+angulo+";"+raio+";"
				+offsetMotorEsq+";"+offsetMotorDir+";"+debug;
		cc.enviarMensagem(msg);
		debugPrint("O ficheiro gravou com sucesso.");
	}
	
	private void carregar() {
		String fp = nomeRobot+".dat";
		File f = new File(fp);
		if(f.exists() && !f.isDirectory()) { 
			cc = new CanalComunicacao(fp);
			String msg = cc.receberMensagem();
			String[] splitMsg = msg.split(";");
			distancia = Integer.parseInt(splitMsg[0]); 
			tF_Distacia.setText(splitMsg[0]);
			angulo = Integer.parseInt(splitMsg[1]); 
			tF_Angulo.setText(splitMsg[1]);
			raio = Integer.parseInt(splitMsg[2]); 
			tF_Raio.setText(splitMsg[2]);
			offsetMotorEsq = Integer.parseInt(splitMsg[3]); 
			tF_MotorEsquerdo.setText(splitMsg[3]);
			offsetMotorDir = Integer.parseInt(splitMsg[4]);
			tF_MotorDireito.setText(splitMsg[4]);
			debug = Boolean.parseBoolean(splitMsg[5]);
			cB_Debug.setEnabled(debug);
			debugPrint("O ficheiro foi carregado com sucesso.");
		}else {
			debugPrint("O ficheiro não existe.");
		}
		
		
	}
	
	/**
	 * 
	 */
	public void run() {
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		GUI frame = new GUI();
		frame.run();
	}
}
