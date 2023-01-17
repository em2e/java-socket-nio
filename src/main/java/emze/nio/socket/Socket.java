package emze.nio.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.StandardProtocolFamily;

public class Socket implements Closeable {

    private final int fd; //native file handle
    private final StandardProtocolFamily family;
    private final SocketType type;
    private final int nativeFamily;
    private final int nativeType;
    private boolean isClosed;
    
    public Socket(int fd, StandardProtocolFamily family, int nativeFamily, SocketType type, int nativeType) {
    	this.fd = fd;
    	this.family = family;
    	this.nativeFamily = nativeFamily;
    	this.type = type;
    	this.nativeType = nativeType;
    	this.isClosed = fd < 0;
	}
    
	public boolean isOpen() {
		return !isClosed;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	

	@Override
	public void close() throws IOException {
		if (!isClosed) {
			SocketUtil.close(this);
			isClosed = true;
		}
	}
	
	public StandardProtocolFamily getFamily() {
		return family;
	}
	
	public SocketType getType() {
		return type;
	}
	
	public int getFd() {
		return fd;
	}

	public ServerSocket bind(String address) throws IOException {
		if (isClosed) {
			throw new IOException("Socket is closed");
		}
		SocketUtil.bind(this, address);
		return new ServerSocket(this, address);
	}
	
	public int getNativeFamily() {
		return nativeFamily;
	}
	
	public int getNativeType() {
		return nativeType;
	}

	public SocketConnection connect(String address) throws IOException {
		if (isClosed) {
			throw new IOException("Socket is closed");
		}
		SocketUtil.connect(this, address);
		return SocketConnection.fromClient(this, address);
	};
}
