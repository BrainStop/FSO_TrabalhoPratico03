import java.util.concurrent.Semaphore;


/**
 * O comportamento SEGUIR PAREDE é uma tarefa JAVA que quando ativa, faz com que
 * o robot ande paralelo á parede, sobre uma linha imaginária a uma distância
 * pré-definida, que se encontra do lado direito do robot.
 * 
 * @author Goncalo Oliveira
 * @author Miguel Marcal
 */
public class SeguirParede extends Thread {
	
	/**
	 *  Distância que o robot se deslocará em cm
	 */
	private static final int DIST = 15;
	
	/**
	 *  Velocidae a que o robot se desloca cm/millis
	 */
	private static final double VEL = 0.018;
	
	/**
	 * Distancia do centro a roda em cm
	 */
	private static final double raio = 3.5;
	
	/**
	 * Canal de comunicacao com o robot
	 */
	private final RobotPlayer robot;
	
	/**
	 * Tarefa que produz distancias com o sensor do robot
	 */
	private final ProdutorDistancias prodDist;

	/**
	 * Semaforo utilizado para ativar ou desactivar a tarefa
	 */
	private Semaphore desativar = new Semaphore(0);
	
	/**
	 *  Semaforo utilizado para bloquear ou desbloquear a tarefa
	 */
	private Semaphore bloquear = new Semaphore(0);

	/**
	 * Semaforo utilizado para a exclusao mutua do acesso ao robot
	 */
	private Semaphore exclusao;
	
	/**
	 * Semaforo utilizado para fazer os processos que chamem o metodo esperar
	 * fiquem á espera que este os liberte
	 */
	private Semaphore espera = new Semaphore(0);
	
	enum estados {esperarTrabalho, seguirParede}
	
	private estados estSParede;

	/**
	 * 
	 * 
	 * @param robot
	 * @param pd
	 * @param exclusao
	 */
	public SeguirParede(RobotPlayer robot, ProdutorDistancias pd,
			Semaphore exclusao){
		this.exclusao      = exclusao;
		this.robot         = robot;
		this.prodDist      = pd; 
		this.estSParede = estados.esperarTrabalho;
	}

	/**
	 * Verifica se o processo é necessario ser bloqueado ou desativado
	 */
	private void esperarTrabalho(){
		System.out.println("SP: Esperar Trabalho");
		espera.release();
		try {
//			if(bloquearFlag) {
//				System.out.println("SP: Bloqueou");
//				bloquear.acquire();
//				System.out.println("SP: Desbloqueou");
//				bloquearFlag = false;
//			}
			System.out.println("SP: Desativou");
			desativar.acquire();
			System.out.println("SP: Ativou");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * sub-comportamento faz com que o robo ande paralelo á parede que esteja a 
	 * usa direita 
	 */
	private void seguirParede(){
		System.out.println("SP: Seguir Parede");
		try {
			int distI = prodDist.getDistancia(); //Distancia inicial
			exclusao.acquire();
			long sleepSPreta = (long) (DIST / VEL) * 2;
			robot.Reta(DIST);
			exclusao.release();

			Thread.sleep(Math.abs(sleepSPreta));

			float distF        = prodDist.getDistancia(); //Distancia final
			exclusao.acquire();
			float catOposto    = distF - distI;
			float catAdjacente = DIST;
			int angulo         = (int)(Math.atan(Math.abs(catOposto) /
					catAdjacente) * 57.3);
			//Distancia de rotacao
			double distRot     = (angulo * Math.PI * raio)/180;
			long SleepSPcurva = (long) (distRot / VEL);

			if(catOposto < 0) 
				robot.CurvarEsquerda(0, angulo);
			else
				robot.CurvarDireita(0, angulo);

			robot.Parar(false);
			exclusao.release();

			System.out.println("Detalhes SP://///////////////////// \n"
					+ "dist_F: " + distF + "\n"
					+ "disf_I: " + distI + "\n"
					+ "catOposto: " + catOposto +  "\n"
					+ "catAdjacente: " + catAdjacente +  "\n"
					+ "angulo: " + angulo + "\n"
					+ "distRot " + distRot + "\n"
					+ "/////////////////////////////////////");

			Thread.sleep(Math.abs(SleepSPcurva));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sub-comportamento que é executado depois do sub-comportamento seguir 
	 * parede e ajusta a distancia entre o robot e a parede
	 */
	public void ajustarDistancia() {
		System.out.println("SP: Ajustar Distancia");
		try {
			
			int distParede   = prodDist.getDistancia();
			exclusao.acquire();
			double catOposto = distParede - 50;
			double angulo    = 45;
			double distDesl  = catOposto / Math.sin(Math.abs(angulo));
			double distRot = (45*Math.PI * raio)/180; //Distancia de rotacao
			long sleepAD = (long) ((distDesl + distRot) / VEL) * 2;
			
			
			if(catOposto > 0) {
				robot.CurvarDireita(0, (int)angulo);
				robot.Reta(Math.abs((int)distDesl));
				robot.CurvarEsquerda(0, (int)angulo);
			} else {
				robot.CurvarEsquerda(0, (int)angulo);
				robot.Reta(Math.abs((int)distDesl));
				robot.CurvarDireita(0, (int)angulo);
			}
			
			robot.Parar(false);
			exclusao.release();

			System.out.println("Detalhes AD://////////////////\n"
					+ "distParede" + distParede + "\n"
					+ "catOposto: " + catOposto +  "\n"
					+ "angulo: " + angulo +  "\n"
					+ "distDesl: " + distDesl + "\n"
					+ "ditRot" + distRot + "\n"
					+ "sleepAD" + sleepAD + "\n"
					+ "/////////////////////////////////////");

			Thread.sleep(Math.abs(sleepAD));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void run(){
		while(true) {
			switch (estSParede){
			case esperarTrabalho:
				esperarTrabalho();
				if(estSParede == estados.esperarTrabalho)
					estSParede = estados.seguirParede;
				break; 
				
			case seguirParede:
				seguirParede();
				if(estSParede == estados.seguirParede)
					estSParede = estados.seguirParede;
				break; 
				
//			case ajustarDistancia:
//				ajustarDistancia();
//				if(estSParede == estados.ajustarDistancia)
//					estSParede = estados.seguirParede;
//				break;
			}
		}
	}

	/**
	 * Ativa a tarefa
	 * Metodo utilizado pelos processo pai
	 */
	public void ativar() {
		System.out.println("SP: Ativar");
		desativar.release();
	}

	/**
	 * Desativa a tarefa
	 * Metodo utilizado pelo processo pai
	 */
	public void desativar() {
		System.out.println("SP: Desativar");
		estSParede = estados.esperarTrabalho;
		desativar.drainPermits();
	}

	/**
	 * Desbloqueia a tarefa
	 * Metodo utilizado pelos processos irmao que tenham maior prioridade
	 */
	public void desbloquear() {
		System.out.println("SP: Desbloquear");
		bloquear.release();
	}

	/**
	 * Bloqueia a tarefa
	 * Metodo utilizado pelos processos irmao que tenham maior prioridade
	 */
	public void bloquear() {
		System.out.println("SP: Bloquear");
	}
}
