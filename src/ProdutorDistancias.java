import java.util.concurrent.Semaphore;

public class ProdutorDistancias extends Thread {
	
	private static final int SLEEP = 250;
	
	private static final int PORT = Robot.S_1;
	
	private RobotPlayer robot;
	
	private int distancia;

	private Semaphore desativar = new Semaphore(0);
	
	private Semaphore exclusao;
	
	private enum estados { esperarTrabalho, getSensorDistancia}
	
	private estados estPD;
	
	public ProdutorDistancias(RobotPlayer robot, Semaphore exclusao) {
		this.exclusao  = exclusao;
		this.robot     = robot;
		this.estPD = estados.esperarTrabalho;
	}
	

	

	
	/**
	 * 
	 */
	private void getSensorDistance() {
		try {
			exclusao.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		distancia = robot.SensorUS(PORT);
		exclusao.release();
		
		synchronized (this) {
			this.notifyAll();
		}

//		System.out.println("P: Get Distancia: "+distancia);
		try {
			Thread.sleep(SLEEP);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void esperarTrabalho() {
		try {
			desativar.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	public void run () {
		while(true) {
			switch (estPD) {
				case esperarTrabalho:
					esperarTrabalho();
					if(estPD == estados.esperarTrabalho)
						estPD = estados.getSensorDistancia;
					break;
					
				case getSensorDistancia:
					getSensorDistance();
					if(estPD == estados.getSensorDistancia)
						estPD = estados.getSensorDistancia;
					break;
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDistancia() {
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return distancia;
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
		System.out.println("setSensor Distancia");
		robot.SetSensorLowspeed(PORT);
		exclusao.release();
		desativar.release();
	}
	
	/**
	 * 
	 */
	public void desativar() {
		estPD = estados.esperarTrabalho;
		desativar.drainPermits();
	}
}