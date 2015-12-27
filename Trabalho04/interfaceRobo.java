package Trabalho04;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//User Imports
import RobotLego.RobotLego;

public class interfaceRobo extends JFrame implements Runnable {

	// Asminhas variaveis
	private boolean isDebugAtivo;
	private boolean onoff;
	private int offsetEsquerdo = 0;
	private int offsetDireiro = 0;
	private int raio = 0;
	private int angulo = 0;
	private int distancia = 0;
	private String nome = "LOST";
	private String s;
	private RobotLego robo = new RobotLego();
	// private RobotLego robo = null;

	private vaguear vg = new vaguear(robo);
	private fugir fug = new fugir(robo, vg);
	private evitar evi = new evitar(robo, vg, fug);;
	

	// Funções auxiliares
	private void mostrarMensagem(String mensagem) {
		if (this.isDebugAtivo == true)
			this.log.append(mensagem + "\n");

	}

	private void interruptall() {
		vg.mySuspend();
		fug.mySuspend();
		evi.mySuspend();
	}
	
	private void resumeall() {
		vg.myResume();
		fug.myResume();
		evi.myResume();
	}

	private void stop() {
		interruptall();
		robo.Parar(true);
		resumeall();
	}

	private void forward() {
		interruptall();
		robo.Parar(true);
		robo.Reta(distancia);
		robo.Parar(false);
		resumeall();
	}

	private void backward() {
		interruptall();
		robo.Parar(true);
		robo.Reta(-distancia);
		robo.Parar(false);
		resumeall();
	}

	private void left() {
		interruptall();
		robo.Parar(true);
		robo.CurvarEsquerda(raio, angulo);
		robo.Parar(false);
		resumeall();
	}

	private void rigth() {
		interruptall();
		robo.Parar(true);
		robo.CurvarDireita(raio, angulo);
		robo.Parar(false);
		resumeall();
	}

	private void leftOffset() {
		robo.AjustarVME(offsetEsquerdo);
	}

	private void rigthOffset() {
		robo.AjustarVMD(offsetDireiro);
	}

	private void myInit() {
		this.isDebugAtivo = true;

		// robo.OpenNXT(s);

		this.checkboxDebugAction.setSelected(this.isDebugAtivo);
		this.textFieldOffsetEsquerda.setText(Integer.toString(this.offsetEsquerdo));
	}

	// Atribulos "automáticos"

	private JPanel contentPane;
	private JTextField textFieldOffsetEsquerda;
	private JTextField textFieldOffsetDireita;
	private JCheckBox checkboxDebugAction;
	private JLabel lblNomeDoRobot;
	private JTextField textFieldNome;
	private JTextField textFieldRaio;
	private JTextField textFieldAngulo;
	private JTextField textFieldDistancia;
	private JButton btnNewButton_1;
	private JButton btnNewButton_3;
	private JButton btnNewButton;
	private JButton btnNewButton_4;
	private JButton btnNewButton_2;
	private JTextArea log;
	private JRadioButton radioVaguear;
	private JRadioButton radioEvitar;
	private JRadioButton radioFugir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			interfaceRobo frame = new interfaceRobo();
			frame.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		for (;;) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public interfaceRobo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 487);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textFieldOffsetEsquerda = new JTextField();
		textFieldOffsetEsquerda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					offsetEsquerdo = Integer.parseInt(textFieldOffsetEsquerda.getText());
					mostrarMensagem("Offset esquerdo -> " + offsetEsquerdo);
					leftOffset();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldOffsetEsquerda.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldOffsetEsquerda.setText("0");
		textFieldOffsetEsquerda.setBounds(10, 40, 72, 20);
		contentPane.add(textFieldOffsetEsquerda);
		textFieldOffsetEsquerda.setColumns(10);

		textFieldOffsetDireita = new JTextField();
		textFieldOffsetDireita.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					offsetDireiro = Integer.parseInt(textFieldOffsetDireita.getText());
					mostrarMensagem("Offset direita -> " + offsetDireiro);
					rigthOffset();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldOffsetDireita.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldOffsetDireita.setText("0");
		textFieldOffsetDireita.setBounds(306, 40, 67, 20);
		contentPane.add(textFieldOffsetDireita);
		textFieldOffsetDireita.setColumns(10);

		JLabel lblOffsetEsquerda = new JLabel("Offset Esquerda");
		lblOffsetEsquerda.setBounds(10, 13, 97, 14);
		contentPane.add(lblOffsetEsquerda);

		JLabel lblOffsetDireita = new JLabel("Offset Direita");
		lblOffsetDireita.setBounds(306, 12, 97, 17);
		contentPane.add(lblOffsetDireita);

		checkboxDebugAction = new JCheckBox("Debug Action");
		checkboxDebugAction.setBackground(Color.LIGHT_GRAY);
		checkboxDebugAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isDebugAtivo = checkboxDebugAction.isSelected();
				mostrarMensagem("Debug state ->" + isDebugAtivo);
			}
		});
		checkboxDebugAction.setBounds(272, 307, 101, 23);
		contentPane.add(checkboxDebugAction);

		lblNomeDoRobot = new JLabel("Nome do Robot");
		lblNomeDoRobot.setBounds(148, 13, 88, 14);
		contentPane.add(lblNomeDoRobot);

		textFieldNome = new JTextField();
		textFieldNome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					nome = textFieldNome.getText();

					mostrarMensagem("Nome -> " + nome);
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldNome.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldNome.setText("none");
		textFieldNome.setBounds(144, 40, 86, 20);
		contentPane.add(textFieldNome);
		textFieldNome.setColumns(10);

		JRadioButton bOnOff = new JRadioButton("On/Off");
		bOnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					onoff = bOnOff.isSelected();
					if (onoff == true) {
						onoff = robo.OpenNXT(nome);
						mostrarMensagem("Robo ligado");
					} else {
						robo.CloseNXT();
						mostrarMensagem("Robo desligado");
					}
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		bOnOff.setBackground(Color.LIGHT_GRAY);
		bOnOff.setForeground(Color.BLACK);
		bOnOff.setBounds(154, 67, 67, 23);
		contentPane.add(bOnOff);

		JLabel lblRaio = new JLabel("Raio");
		lblRaio.setBounds(10, 112, 30, 14);
		contentPane.add(lblRaio);

		textFieldRaio = new JTextField();
		textFieldRaio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					raio = Integer.parseInt(textFieldRaio.getText());

					mostrarMensagem("Raio -> " + raio);
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldRaio.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRaio.setText("0");
		textFieldRaio.setBounds(49, 108, 30, 20);
		contentPane.add(textFieldRaio);
		textFieldRaio.setColumns(10);

		JLabel lblCm = new JLabel("cm");
		lblCm.setBounds(84, 112, 23, 14);
		contentPane.add(lblCm);

		JLabel lblAngulo = new JLabel("Angulo");
		lblAngulo.setBounds(120, 112, 46, 14);
		contentPane.add(lblAngulo);

		textFieldAngulo = new JTextField();
		textFieldAngulo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					angulo = Integer.parseInt(textFieldAngulo.getText());

					mostrarMensagem("Angulo -> " + angulo);
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldAngulo.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldAngulo.setText("0");
		textFieldAngulo.setBounds(172, 110, 30, 20);
		contentPane.add(textFieldAngulo);
		textFieldAngulo.setColumns(10);

		JLabel lblGraus = new JLabel("graus");
		lblGraus.setBounds(205, 112, 46, 14);
		contentPane.add(lblGraus);

		JLabel lblDistancia = new JLabel("Dist\u00E2ncia");
		lblDistancia.setBounds(261, 112, 53, 14);
		contentPane.add(lblDistancia);

		textFieldDistancia = new JTextField();
		textFieldDistancia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					distancia = Integer.parseInt(textFieldDistancia.getText());

					mostrarMensagem("Distância -> " + distancia);
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		textFieldDistancia.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDistancia.setText("0");
		textFieldDistancia.setBounds(322, 109, 30, 20);
		contentPane.add(textFieldDistancia);
		textFieldDistancia.setColumns(10);

		JLabel lblCm_1 = new JLabel("cm");
		lblCm_1.setBounds(357, 112, 23, 14);
		contentPane.add(lblCm_1);

		JLabel lblLog = new JLabel("Log");
		lblLog.setBounds(12, 312, 53, 14);
		contentPane.add(lblLog);

		btnNewButton = new JButton("Frente");
		btnNewButton.setBackground(Color.GREEN);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Andou para a frente -> " + distancia + "cm.");
					forward();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		btnNewButton.setBounds(138, 158, 109, 46);
		contentPane.add(btnNewButton);

		btnNewButton_1 = new JButton("Parar");
		btnNewButton_1.setBackground(Color.RED);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Parou.");
					stop();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		btnNewButton_1.setBounds(138, 215, 109, 46);
		contentPane.add(btnNewButton_1);

		btnNewButton_2 = new JButton("Retaguarda");
		btnNewButton_2.setBackground(Color.BLUE);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Andou para trás -> " + distancia + "cm.");
					backward();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		btnNewButton_2.setBounds(138, 272, 109, 46);
		contentPane.add(btnNewButton_2);

		btnNewButton_3 = new JButton("Esquerda");
		btnNewButton_3.setBackground(Color.YELLOW);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Virou para a esquerda -> " + angulo + "º");
					left();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		btnNewButton_3.setBounds(21, 215, 109, 46);
		contentPane.add(btnNewButton_3);

		btnNewButton_4 = new JButton("Direita");
		btnNewButton_4.setBackground(Color.PINK);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Virou para a direita -> " + angulo + "º");
					rigth();
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		btnNewButton_4.setBounds(257, 215, 109, 46);
		contentPane.add(btnNewButton_4);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 331, 364, 107);
		contentPane.add(scrollPane);

		log = new JTextArea();
		scrollPane.setViewportView(log);

		radioVaguear = new JRadioButton("Vaguear");
		radioVaguear.setBounds(20, 133, 109, 23);
		radioVaguear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Vaguiando");
					if(!vg.isAlive()){
						vg.start();
					}else{
						vg.myStop();
					}
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		contentPane.add(radioVaguear);

		radioEvitar = new JRadioButton("Evitar");
		radioEvitar.setBounds(20, 159, 109, 23);
		radioEvitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Evitando");
					if(!evi.isAlive()){
						evi.start();
					}else{
						evi.myStop();
					}
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		contentPane.add(radioEvitar);

		radioFugir = new JRadioButton("Fugir");
		radioFugir.setBounds(20, 186, 109, 23);
		radioFugir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarMensagem("Fugindo");
					if(!fug.isAlive()){
						fug.start();
					}else{
						fug.myStop();
					}
				} catch (Exception e1) {
					// JOptionPane.showMessageDialog(...);
					e1.printStackTrace();

				}
			}
		});
		contentPane.add(radioFugir);

		setVisible(true);

		myInit();
	}
}
