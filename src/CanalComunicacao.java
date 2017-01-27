import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class CanalComunicacao {
	
	private FileChannel canal;
	private final long BUFFER_MAX = 2000;
	private MappedByteBuffer buffer;
	private FileLock lock;

	CanalComunicacao(String filePath) {
		File ficheiro = new File(filePath);
		
		try {
			canal = new RandomAccessFile(ficheiro, "rw").getChannel();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_MAX);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void lock() {
		try {
			lock = canal.lock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void unlock() {
		try {
			lock.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarMensagem(String msg) {
		lock();
		char c;
		buffer.position(0);
		for(int i = 0; i < msg.length(); ++i) {
			c = msg.charAt(i);
			buffer.putChar(c);
		}
		buffer.putChar('\0');
		unlock();
	}
	
	public String receberMensagem() {
		lock();
		String msg = new String();
		char c;
		buffer.position(0);
		while((c = buffer.getChar()) != '\0') {
			msg += c;
		}
		unlock();
		return msg;
	}
	
	public void getAndSet(String get, String set) {
		String msg = new String();
		char c;
		buffer.position(0);
		
		lock();
		while((c = buffer.getChar()) != '\0') {
			msg += c;
		}
		System.out.println("MENGAM: "+ msg);
		System.out.println("SET: "+ set);
		if(msg.equals(get)) {
			buffer.position(0);
			for(int i = 0; i < set.length(); ++i) {
				c = set.charAt(i);
				buffer.putChar(c);
			}
			buffer.putChar('\0');
		}
		unlock();
	}
	
	public void fecharCanal() {
		try {
			canal.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
