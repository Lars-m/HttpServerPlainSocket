package simplehttp;

import java.io.*;
import java.net.*;

public class HttpServerPlainSocket extends Thread 
{
  static String NL = System.getProperty("line.separator");
  static final String BEGIN = "<!DOCTYPE html>\n<HTML>\n<HEAD>\n<TITLE>Simple HTTP Server</TITLE></HEAD>\n<BODY>";
  static final String END = "</BODY>\n</HTML>";
  private static String ip = "127.0.0.1";
  private static int port = 23456;

  Socket socket = null;

  public HttpServerPlainSocket(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {

    try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {

      StringBuilder response = new StringBuilder();
      String request = in.readLine();
      if (request == null) {
        return;
      }
      String[] firstLineItems = request.split(" ");
      System.out.println("First Line: " + request);
      if (request != null) {
        response.append("<H2>Simple HTTP Server</H2>");
        response.append("<H3>Request Details</H3>");
      }
      while (in.ready()) {
        System.out.println(in.readLine());
      }
      if (firstLineItems[1].equals("/")) {
        sendResponse(out, 200, response.toString(), false);
      } else {
        sendResponse(out, 404, "<h2>404 Not Found</h2>No content found for request", false);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Disconnected");
  }

  public void sendResponse(DataOutputStream out, int statusCode, String responseString, boolean isFile) throws IOException {
    String status = null;
    String fileName = null;
    if (statusCode == 200) {
      status = "HTTP/1.1 200 OK" + NL;
    } else if (statusCode == 404) {
      status = "HTTP/1.1 404 NOT FOUND" + NL;
    }
    responseString = BEGIN + responseString + END;
    out.writeBytes(status);
    //out.writeBytes("Connection: close" + NL);
    out.writeBytes(NL);
    out.writeBytes(responseString);
    out.close();
  }

  public static void main(String args[]) throws Exception {
    if (args.length == 2) {
      ip = args[0];
      port = Integer.parseInt(args[1]);
    }
    ServerSocket server = new ServerSocket();
    server.bind(new InetSocketAddress(ip, port));
    System.out.println("HTTP Server Listening on: " + port);

    while (true) {
      Socket socket = server.accept();
      System.out.println("New Client " + socket.getInetAddress() + ":" + socket.getPort() + " connected");
      (new HttpServerPlainSocket(socket)).start();
    }
  }
}
