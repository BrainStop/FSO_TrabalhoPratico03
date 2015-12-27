package Trabalho04;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class ComJAVA {
	File ficheiro;
	FileChannel canal;
	MappedByteBuffer buffer;
	FileLock lock;
	final int buffer_max = 200;
	private RandomAccessFile randomAccessFile;

	public ComJAVA() {
		ficheiro = new File("comunicacao.dat");

		try {
			randomAccessFile = new RandomAccessFile(ficheiro, "rw");
			canal = randomAccessFile.getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, buffer_max);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void lock() {
		try {
			lock = canal.lock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unlock() {
		try {
			lock.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String recebeMensagem() {
		String mensagem = new String("");
		char c;
		buffer.position(0);
		while ((c = buffer.getChar()) != '\0') {
			mensagem += c;
		}
		return mensagem;
	}

	// envia uma String como mensagem
	void enviarMensagem(String msg) {
		char c;
		buffer.position(0);
		for (int i = 0; i < msg.length(); ++i) {
			c = msg.charAt(i);
			buffer.putChar(c);
		}
		buffer.putChar('\0');
	}

	void fecharCanal() {
		try {
			canal.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		enviarMensagem("robotlivre");
	}

	public static void main(String[] args) {
		ComJAVA escreve = new ComJAVA();
		escreve.run();
	}
}
