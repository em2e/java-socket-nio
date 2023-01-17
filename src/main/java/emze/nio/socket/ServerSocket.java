package emze.nio.socket;

import java.io.Closeable;
import java.io.IOException;

public class ServerSocket implements Closeable { 
	
	private final Socket socket;
	private final String address;
	
	public ServerSocket(Socket socket, String address) {
		this.socket = socket;
		this.address = address;
	}
	
	public boolean isOpen() {
		return socket.isOpen();
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	public void listen() throws IOException {
		SocketUtil.listen(socket);
	}

	public SocketConnection accept() throws IOException {
		Socket peer = SocketUtil.accept(socket);
		return SocketConnection.fromServerSide(this, peer);
	}

	public String getAddress() {
		return address;
	}
}
