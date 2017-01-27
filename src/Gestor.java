import java.util.concurrent.Semaphore;

public class Gestor extends Thread {
	
	/**
	 * 
	 */
	private SeguirParede seguirParede;
	
	/**
	 * 
	 */
	private Vaguear vaguear;
	
	/**
	 * 
	 */
	private ProdutorDistancias prodDist;
	
	/**
	 * 
	 */
	private Semaphore desativar = new Semaphore(0);
	
	/**
	 * 
	 */
	
	enum estadoGestor { esperarTrabalho, medirDistancia, ligarVaguear,
		ligarSParede, medirDistVaguear, medirDistSParede}
	
	private estadoGestor estGestor;
	
	/**
	 * 
	 * @param pd
	 * @param vag
	 * @param sp
	 */
	public Gestor(ProdutorDistancias pd, Vaguear vag, SeguirParede sP) {
		this.prodDist = pd;
		this.vaguear = vag;
		this.seguirParede = sP;
		estGestor = estadoGestor.esperarTrabalho;
	}
	
	/**
	 * 
	 */
	private void esperarTrabalho() {
		System.out.println("G: esperarTrabalho");
			vaguear.desativar();
			seguirParede.desativar();
			try {
				desativar.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	
	public estadoGestor medirDistancia() {
		System.out.println("G: medirDistancia");
		if(prodDist.getDistancia() < 100)
			return estadoGestor.ligarSParede;
		else
			return estadoGestor.ligarVaguear;
	}
	

	private estadoGestor medirDistSParede() {
		System.out.println("G: medirDistSParede");
		if(prodDist.getDistancia() > 110)
			return estadoGestor.ligarVaguear;
		
		return estadoGestor.medirDistSParede;
	}

	private estadoGestor medirDistVaguear() {
		System.out.println("G: medirDistVaguear");
		if(prodDist.getDistancia() < 90)
			return estadoGestor.ligarSParede;
		
		return estadoGestor.medirDistVaguear;
	}

	private void ligarVaguear() {
		System.out.println("G: LigarVaguear");
		seguirParede.desativar();
		vaguear.ativar();
	}

	private void ligarSParede() {
		System.out.println("G: ligarSParede");
		vaguear.desativar();
		seguirParede.ativar();
	}
	
	public void run() {
		estadoGestor estNovo;
		while(true) {
			switch (estGestor) {
			case esperarTrabalho:
				esperarTrabalho();
				if(estGestor == estadoGestor.esperarTrabalho)
					estGestor = estadoGestor.medirDistancia;
				break;
			
			case medirDistancia:
				estNovo = medirDistancia();
				if(estGestor == estadoGestor.medirDistancia)
					estGestor = estNovo;
				break;
				
			case ligarVaguear:
				ligarVaguear();
				if(estGestor == estadoGestor.ligarVaguear)
					estGestor = estadoGestor.medirDistVaguear;
				break;
			
			case medirDistVaguear:
				estNovo = medirDistVaguear();
				if(estGestor == estadoGestor.medirDistVaguear)
					estGestor = estNovo;
				break;
				
			case ligarSParede:
				ligarSParede();
				if(estGestor == estadoGestor.ligarSParede)
					estGestor = estadoGestor.medirDistSParede;
				break;
				
			case medirDistSParede:
				estNovo = medirDistSParede();
				if(estGestor == estadoGestor.medirDistSParede)
					estGestor = estNovo;
				break;
			}
		}
	}

	public void  ativar() {
		System.out.println("G: Ativar");
		desativar.release();
	}
	
	public void desativar() {
		System.out.println("G: Desativar");
		estGestor = estadoGestor.esperarTrabalho;
		desativar.drainPermits();
	}

}
