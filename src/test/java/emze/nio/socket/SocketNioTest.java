package emze.nio.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class SocketNioTest {

	private static final String TEST_ABSTRACT_SOCKET_PATH = "@/emze/nio/socket"; //the @ character at the beginning will be replaced with a \0 byte at the low level io call
	private static final String GREETING_FROM_SRV = "HELO";
	private static final String REPLY_FROM_CLI = "EHLO"; 

	/**
	 * Testing bidirectional sequential packet communication through unix domain sockets with abstract path
	 * 
	 * @throws IOException
	 */
	@Test
	public void testBidirectionalConnectionWithAbstractPath() throws IOException {

		//assert we could create sockets of UNIX family and SEQPACKET type 
		try (
				Socket srv = SocketUtil.socket(StandardProtocolFamily.UNIX, SocketType.SEQPACKET);
				Socket cli = SocketUtil.socket(StandardProtocolFamily.UNIX, SocketType.SEQPACKET)
		) {

			
			//assert we could bind the server socket to the given abstract path 
			try(ServerSocket serverSocket = srv.bind(TEST_ABSTRACT_SOCKET_PATH)) {

				//assert server socket could start listening for connection 
				serverSocket.listen();

				//assert client socket could connect to the server socket, and the server socket could accept the connection 
				try (
						SocketConnection cliConn = cli.connect(TEST_ABSTRACT_SOCKET_PATH);
						SocketConnection srvConn = serverSocket.accept()
				) {

					//assert server could send a greeting message to the client 
					{
						srvConn.send(GREETING_FROM_SRV.getBytes());
						String msg = new String(new String(cliConn.recv(), StandardCharsets.UTF_8));
						assertEquals(GREETING_FROM_SRV, msg);
					}

					//assert client could reply to the server
					{
						cliConn.send(REPLY_FROM_CLI.getBytes());
						String msg = new String(new String(srvConn.recv(), StandardCharsets.UTF_8));
						assertEquals(REPLY_FROM_CLI, msg);
					}

				}
			}

			//both sockets are closed after the connection is closed 
			assertFalse(srv.isOpen());
			assertFalse(cli.isOpen());
		}
	}
}
