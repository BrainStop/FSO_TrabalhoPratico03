import java.util.concurrent.Semaphore;

/**
 * @author Gonçalo Oliveira
 * @author Miguel Marçal
 */
public class Evitar extends Thread{
		
	/**
	 *  Distância que o robot se deslocará
	 */
	private static final int DIST = -15;
	
	/**
	 * Velocidade estimada que o robot se desloca em milisegundos.
	 */
	private static final double VEL = 0.01;
	
	/**
	 * Raio que o robot utiliza ao curvar
	 */
	private static final double RAIO = 3.5;
	
	/**
	 * Angulo que o robot utiliza ao curvar
	 */
	private static final int ANGULO = 90;

	/**
	 * Port do sensor de embate
	 */
	private static final int PORT = Robot.S_2;

	/**
	 * Objeto utilizado para comunicar e controlar o robot
	 */
	private final RobotPlayer robot;
	
	/**
	 * Semaforo utilizado para bloquear a class quando é invocado o metodo
	 *  desativar()
	 */
	private Semaphore desativar = new Semaphore(0);
	
	enum estados {esperarTrabalho, lerSensorEmbate, evitar}
	
	private estados estEvitar;
	
	/**
	 * Semaforo utilizado para a exclusao mutua do acesso ao robot
	 */
	private Semaphore exclusao;

	/**
	 * 
	 * @param robot
	 * @param vag
	 * @param sP
	 * @param exclusao
	 */
	public Evitar(RobotPlayer robot, Semaphore exclusao) {
		this.robot = robot;
		this.exclusao = exclusao;
		this.estEvitar = estados.esperarTrabalho;
	}

	/**
	 * 
	 */
	public void run() {
		estados estNovo;
		while(true){
			switch(estEvitar){
			case esperarTrabalho:
				esperarTrabalho();
				if(estEvitar == estados.esperarTrabalho)
					estEvitar = estados.lerSensorEmbate;
				break;
			
			case lerSensorEmbate:
				estNovo = lerSensorEmbate();
				if(estEvitar == estados.lerSensorEmbate)
					estEvitar = estNovo;
				break;
			
			case evitar:
				evitar();
				if(estEvitar == estados.evitar)
					estEvitar = estados.lerSensorEmbate;
				break;
			}
		}
	}
	
	/**
	 * 
	 */
	private void esperarTrabalho() {
		System.out.println("E: Esperar Trabalho foi este");
		try {
			desativar.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	private estados lerSensorEmbate() {
		try {
			exclusao.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int sensorE = robot.Sensor(PORT);
		exclusao.release();
		System.out.println("E: Ler Sensor Embate:" + sensorE);
		if (sensorE == 0) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return estados.lerSensorEmbate;
		} else {
			return estados.evitar;
		}
	}
	
	/**
	 * 
	 */
	private void evitar() {
		System.out.println("E: Evitar");
		try {
			exclusao.acquire();

			robot.Parar(true);
			robot.Reta(DIST);
			robot.CurvarEsquerda(1, ANGULO);
			robot.Parar(false);
			
			exclusao.release();
			
			double dist = (ANGULO*Math.PI)/180 * RAIO;
			long td = (long)((dist + Math.abs(DIST))/VEL);
			Thread.sleep(td);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void ativar() {
		try {
			exclusao.acquire();			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		robot.SetSensorTouch(PORT);
		exclusao.release();
		desativar.release();
	}
	
	/**
	 * 
	 */
	public void desativar() {
		estEvitar = estados.esperarTrabalho;
		desativar.drainPermits();
	}
}
