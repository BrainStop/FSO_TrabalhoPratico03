import java.util.concurrent.Semaphore;

public class teste {

	static Semaphore s = new Semaphore(0);
	
	public static void main(String[] args) throws InterruptedException {
		while(true) {
			s.release(0);
			System.out.println("sadas");
		}
	}
}
