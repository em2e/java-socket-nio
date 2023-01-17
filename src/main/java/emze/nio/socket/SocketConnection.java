package emze.nio.socket;

import java.io.Closeable;
import java.io.IOException;

public class SocketConnection implements Closeable {

	private final ServerSocket serverSocket;
	private final Socket peerSocket;
	private final boolean isServer;

	protected SocketConnection(ServerSocket serverSocket, Socket peerSocket, boolean isServer) {
		this.serverSocket = serverSocket;
		this.peerSocket = peerSocket;
		this.isServer = isServer;
	}

	public boolean isOpen() {
		return serverSocket.isOpen();
	}
	
	public boolean isClosed() {
		return serverSocket.isClosed();
	}
	
	public boolean isServer() {
		return isServer;
	}

	@Override
	public void close() throws IOException {
		peerSocket.close();
		if (isServer) {
			serverSocket.close();
		}
	}

	public static SocketConnection fromServerSide(ServerSocket serverSocket, Socket peer) {
		return new SocketConnection(serverSocket, peer, true);
	}

	public static SocketConnection fromClient(Socket socket, String address) {
		return new SocketConnection(new ServerSocket(socket, address), socket, false);
	}

	public void send(byte[] bytes) throws IOException {
		SocketUtil.send(peerSocket, bytes);
	}

	public byte[] recv() throws IOException {
		return SocketUtil.recv(peerSocket);
	}
}
