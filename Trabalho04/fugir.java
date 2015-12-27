package Trabalho04;

import java.util.Random;
import java.util.concurrent.Semaphore;

import RobotLego.RobotLego;

public class fugir extends Thread{
	Semaphore lock = new Semaphore(0);
	private int estado = 1;
	int distSensor;
	int distMax = 100;
	int distMin = 0;
	boolean suspend, stop = false;
	RobotLego robo;
	private static final int S_1 = RobotLego.S_1;
	vaguear v;

	fugir(RobotLego robo, vaguear v) {
		this.robo = robo;
		this.v = v;
	}

	public void run() {
		robo.SetSensorLowspeed(S_1);
		int distAux;
		for (;;) {
			if(stop){
				stop = false;
				break;
			}
			if(suspend){
				try {
					lock.acquire();
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			distAux = 0;
			for(int i = 0; i <5;++i){
				distAux += robo.SensorUS(S_1);
			}
			distSensor = distAux/5;
			if (distSensor < distMax && distSensor >= distMin) {
				if(v.isAlive()){
					v.mySuspend();
				}
				System.out.println("Fugir: " + distSensor);
				robo.SetSpeed(50 - (distSensor - 2));
				robo.Reta(50);
				robo.SetSpeed(50);
				robo.Parar(false);
				distAux = 0;
				if(v.isAlive()){
					v.myResume();
				}
			}
			
			 try {
				Thread.sleep(distSensor * 10);
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
