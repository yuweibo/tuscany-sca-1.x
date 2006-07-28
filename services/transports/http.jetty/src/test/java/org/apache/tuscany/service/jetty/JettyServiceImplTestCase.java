package org.apache.tuscany.service.jetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.spi.services.work.WorkScheduler;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class JettyServiceImplTestCase extends TestCase {

    private static final String REQUEST1_HEADER =
        "GET / HTTP/1.0\n"
            + "Host: localhost\n"
            + "Content-Type: text/xml\n"
            + "Connection: close\n"
            + "Content-Length: ";
    private static final String REQUEST1_CONTENT =
        "";
    private static final String REQUEST1 =
        REQUEST1_HEADER + REQUEST1_CONTENT.getBytes().length + "\n\n" + REQUEST1_CONTENT;

    private static final int HTTP_PORT = 8585;

    private TransportMonitor monitor;
    private WorkScheduler scheduler;
    private ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Verifies requests are properly routed according to the servlet mapping
     */
    public void testRegisterServletMapping() throws Exception {
        JettyServiceImpl service = new JettyServiceImpl(monitor);
        service.setHttpPort(HTTP_PORT);
        service.init();
        TestServlet servlet = new TestServlet();
        service.registerMapping("/", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
        assertTrue(servlet.invoked);
    }

    public void testUseWorkScheduler() throws Exception {
        JettyServiceImpl service = new JettyServiceImpl(monitor, scheduler);
        service.setDebug(true);
        service.setHttpPort(HTTP_PORT);
        service.init();
        TestServlet servlet = new TestServlet();
        service.registerMapping("/", servlet);
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
        assertTrue(servlet.invoked);
    }

    public void testRestart() throws Exception {
        JettyServiceImpl service = new JettyServiceImpl(monitor);
        service.setHttpPort(HTTP_PORT);
        service.init();
        service.destroy();
        service.init();
        service.destroy();
    }

    public void testNoMappings() throws Exception {
        JettyServiceImpl service = new JettyServiceImpl(monitor);
        service.setHttpPort(HTTP_PORT);
        service.init();
        Socket client = new Socket("127.0.0.1", HTTP_PORT);
        OutputStream os = client.getOutputStream();
        os.write(REQUEST1.getBytes());
        os.flush();
        read(client);
        service.destroy();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        //executor.submit();
    }

    protected void setUp() throws Exception {
        super.setUp();
        monitor = createMock(TransportMonitor.class);
        scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));

        expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                executor.execute(runnable);
                return null;
            }
        });
        replay(scheduler);
    }

    private static String read(Socket socket) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private class TestServlet extends HttpServlet {
        boolean invoked;

        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            invoked = true;
            OutputStream writer = resp.getOutputStream();
            try {
                writer.write("result".getBytes());
            } finally {
                writer.close();
            }
        }


    }
}
