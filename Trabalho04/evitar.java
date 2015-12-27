package Trabalho04;

import java.util.concurrent.Semaphore;
import RobotLego.RobotLego;

public class evitar extends Thread {
	Semaphore lock = new Semaphore(0);
	private final int S_2 = RobotLego.S_2;
	RobotLego robo;
	vaguear vag;
	fugir f;
	boolean suspend, stop = false;

	evitar(RobotLego robo, vaguear v, fugir f) {
		this.robo = robo;
		this.vag = v;
		this.f = f;
	}

	public void run() {
		robo.SetSensorTouch( S_2 );
		int s,num = 0;
		for(;;) {
			if(stop){
				stop = false;
				break;
			}
			if(suspend){
				try {
					lock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			s = robo.Sensor(S_2);
				
			if (s == 1) {
				System.out.println("Bateu " + num);
				++num;
				if(vag.isAlive()){
					vag.mySuspend();
				}
				if(f.isAlive()){
					f.mySuspend();
				}
				robo.Parar(true);
				System.out.println("PAROU!");
				robo.Reta(-20);
				robo.Parar(false);
				robo.CurvarEsquerda(0, 90);
				robo.Parar(false);
				vag.myResume();
				if(vag.isAlive()){
					vag.myResume();
				}
				if(f.isAlive()){
					f.myResume();
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void mySuspend() {
		suspend = true;
	}
	public void myResume() {
		suspend = false;
		lock.release();
	}
	public void myStop() {
		stop = true;
	}
}
