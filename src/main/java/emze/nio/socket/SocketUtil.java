package emze.nio.socket;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.util.Arrays;

public class SocketUtil {

	static {
		NarSystem.loadLibrary();	
	}
	
	public static Socket socket(final StandardProtocolFamily family, final SocketType type) throws IOException {
		int familyId = nativeGetFamilyId(family.name());
		int typeId = nativeGetTypeId(type.name());
		int fd = nativeSocket(familyId, typeId);
		if (fd < 0) {
			throw new IOException("Socket creation failed: " + nativeError());
		}
		Socket s = new Socket(fd, family, familyId, type, typeId);
		return s;
	}

	public static void close(Socket Socket) throws IOException {
		int ret = SocketUtil.nativeClose(Socket.getFd());
		if (ret != 0) {
			throw new IOException("Cannot close socket: " + SocketUtil.nativeError());
		}
	}

	public static void bind(Socket socket, String address) throws IOException {
		int ret = nativeBind(socket.getFd(), socket.getNativeFamily(), address);
		if (ret != 0) {
			throw new IOException("Socket creation failed: " + nativeError());
		}
	}

	public static void listen(Socket socket) throws IOException {
		int ret = nativeListen(socket.getFd(), 1);
		if (ret != 0) {
			throw new IOException("Socket listen failed: " + nativeError());
		}
	}

	public static Socket accept(Socket socket) throws IOException {
		int fd = nativeAccept(socket.getFd());
		if (fd < 0) {
			throw new IOException("Socket accept failed: " + nativeError());
		}
		return new Socket(fd, socket.getFamily(), socket.getNativeFamily(), socket.getType(), socket.getNativeType());
	}

	public static void connect(Socket socket, String address) throws IOException {
		int ret = nativeConnect(socket.getFd(), socket.getNativeFamily(), address);
		if (ret != 0) {
			throw new IOException("Socket connect failed: " + nativeError());
		}
	}

	public static void send(Socket socket, byte[] bytes) throws IOException {
		int ret = nativeSend(socket.getFd(), bytes);
		if (ret < 0) {
			throw new IOException("Socket send failed: " + nativeError());
		}
	}

	public static byte[] recv(Socket socket) throws IOException {
		byte[] buffer = new byte[512];
		int msgSize = nativeRecv(socket.getFd(), buffer, buffer.length);
		if (msgSize < 0) {
			throw new IOException("Socket recv failed: " + nativeError());
		}
		return Arrays.copyOf(buffer, msgSize);
	}


	
	private native static int nativeGetFamilyId(String family);
	private native static int nativeGetTypeId(String type);
	private native static int nativeSocket(int family, int type);
	private native static String nativeError();
	private native static int nativeClose(int fd);
	private native static int nativeBind(int fd, int family, String address);
	private native static int nativeListen(int fd, int numberOfConnections);
	private native static int nativeAccept(int fd);
	private native static int nativeConnect(int fd, int family, String address);
	private native static int nativeSend(int fd, byte[] bytes);
	private native static int nativeRecv(int fd, byte[] bytes, int maxLength);


}
