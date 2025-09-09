package com.parcial.app;

import java.net.*;
import java.io.*;
import java.util.*;

public class HttpServer {
   static final int port = 8080;
   static final HashMap<String, String> map = new HashMap<>();

   public static void main(String[] args) throws IOException {
      ServerSocket s;
      try {
         s = new ServerSocket(port);
      } catch (IOException e) {
         System.err.println("Could not listen on port: " + port + ".");
         return;
      }
      System.out.println("Servidor corriendo en http://localhost:" + port);

      while (true) {
         Socket c;
         try {
            c = s.accept();
         } catch (IOException e) {
            System.err.println("Accept failed.");
            break;
         }
         handle(c);
      }
      try {
         s.close();
      } catch (Exception ignore) {
      }
   }

   static void handle(Socket c) {
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
         PrintWriter out = new PrintWriter(c.getOutputStream(), true);

         String line;
         StringBuilder raw = new StringBuilder();
         while ((line = in.readLine()) != null) {
            raw.append(line).append('\n');
            if (!in.ready())
               break;
         }

         String req = raw.toString();
         if (req.isEmpty()) {
            out.println(html("400 Bad Request", bad("Empty request")));
            out.close();
            in.close();
            c.close();

            return;
         }

         String first = req.split("\n", 2)[0];
         String[] parts = first.split(" ");
         String method = parts.length > 0 ? parts[0] : "";
         String path = parts.length > 1 ? parts[1] : "/";

         String q = "";
         int qIdx = path.indexOf('?');
         if (qIdx >= 0) {
            q = path.substring(qIdx + 1);
            path = path.substring(0, qIdx);
         }
         HashMap<String, String> p = new HashMap<>();
         if (!q.isEmpty()) {
            for (String kv : q.split("&")) {
               int eq = kv.indexOf('=');
               if (eq > 0 && eq < kv.length() - 1)
                  p.put(kv.substring(0, eq), kv.substring(eq + 1));
            }
         }

         if ("GET".equals(method) && path.startsWith("/getkv")) {
            String key = p.get("key");
            if (key == null) {
               out.println(html("400 Bad Request", bad("Missing param \"key\"")));
            } else {
               String val = map.get(key);
               if (val == null)
                  out.println(json404(key));
               else
                  out.println(okJson("{ \"key\": \"" + key + "\", \"value\": \"" + val + "\" }"));
            }
         } else if ("GET".equals(method) && path.startsWith("/setkv")) {
            String k = p.get("key");
            if (k == null) {
               out.println(html("400 Bad Request", bad("Missing param \"key\"")));
            } else {
               String v = p.get("value");
               if (v == null)
                  out.println(html("400 Bad Request", bad("Missing param \"value\"")));
               else {
                  map.put(k, v);
                  out.println(okJson("{ \"key\": \"" + k + "\", \"value\": \"" + v + "\", \"status\": \"created\" }"));
               }
            }
         } else {
            out.println(html("400 OK",
                  basic("Availiable routes /setkv or /getkv")));
         }

         out.close();
         in.close();
         c.close();
      } catch (Exception e) {

         try {
            PrintWriter out = new PrintWriter(c.getOutputStream(), true);
            out.println(html("500 Internal Server Error", bad("Internal Error")));
            out.close();
            c.close();
         } catch (Exception ignore) {
         }
      }
   }

   static String okJson(String body) {
      return "HTTP/1.1 200 OK\r\n" +
            "Content-Type: application/json; charset=utf-8\r\n\r\n" +
            body;
   }

   static String json404(String key) {
      return "HTTP/1.1 404 OK\r\n" +
            "Content-Type: application/json; charset=utf-8\r\n\r\n" +
            "{ \"key\": \"" + key + "\", \"error\": \"key_not_found\" }";
   }

   static String html(String status, String body) {
      return "HTTP/1.1 " + status + "\r\n" +
            "Content-Type: text/html\r\n\r\n" +
            body;
   }

   static String basic(String msg) {
      return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>Title of the document</title>\n</head>\n<body>\n<h1>"
            + msg + "</h1>\n</body>\n</html>\n";
   }

   static String bad(String msg) {

      return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>Error</title>\n</head>\n<body>\n" + msg
            + "\n</body>\n</html>\n";
   }
}