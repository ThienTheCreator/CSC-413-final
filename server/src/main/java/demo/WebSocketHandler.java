package demo;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class WebSocketHandler {

  public static Map<Session,Session> users = new ConcurrentHashMap<>();

  public static void broadcast(String message){
    users.keySet().forEach(session -> {
      //Server to client message
      try{
        session.getRemote().sendString(message);
      }catch(IOException e){
        e.printStackTrace();
      }
    });
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    System.out.println("A client has connected");
    users.put(session,session);
    System.out.println("Total users : " + users.size());
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    System.out.println("A client has disconnected");
    users.remove(session);
    System.out.println("Total users : " + users.size());
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    System.out.println("Got: " + message);   // Print message
  }
}