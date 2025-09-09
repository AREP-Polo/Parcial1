package com.parcial.app;

import java.net.*;
import java.io.*;

public class HttpFacade {
    static final int PORT = 8081; // puerto del facade
    static final int BACKEND = 8080; // puerto del backend

    public static void main(String[] args) throws IOException {
        ServerSocket s;
        try {
            s = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + ".");
            return;
        }
        System.out.println("Facade running at http://localhost:" + PORT);
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
                out.println(resp("400 Bad Request", "text/plain", "Empty request"));
                out.close();
                in.close();
                c.close();
                return;
            }

            String first = req.split("\n", 2)[0];
            String[] parts = first.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";

            if ("/".equals(path)) {
                byte[] body = index();
                if (body == null) {
                    out.println(resp("404 Not Found", "text/plain", "index.html not found"));
                } else {
                    out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
                    out.flush();
                    c.getOutputStream().write(body);
                    c.getOutputStream().flush();
                }
                out.close();
                in.close();
                c.close();
                return;
            }

            URL u = new URI("http://localhost:" + BACKEND + path).toURL();
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int code = con.getResponseCode();
            String msg = con.getResponseMessage();
            String type = con.getContentType();
            InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
            StringBuilder body = new StringBuilder();
            if (is != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String l;
                while ((l = r.readLine()) != null)
                    body.append(l).append('\n');
                r.close();
            }
            String head = "HTTP/1.1 " + code + " " + (msg == null ? "" : msg) + "\r\n" +
                    "Content-Type: " + (type == null ? "text/plain" : type) + "\r\n\r\n";
            out.print(head);
            out.print(body.toString());
            out.flush();
            out.close();
            in.close();
            c.close();
        } catch (Exception e) {
            try {
                PrintWriter out = new PrintWriter(c.getOutputStream(), true);
                out.println(resp("500 Internal Server Error", "text/plain", "Internal Error"));
                out.close();
                c.close();
            } catch (Exception ignore) {
            }
        }
    }

    static byte[] index() {
        try {
            InputStream is = HttpFacade.class.getClassLoader().getResourceAsStream("index.html");
            if (is == null)
                return null;
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) > 0)
                o.write(buf, 0, n);
            is.close();
            return o.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    static String resp(String status, String type, String body) {
        return "HTTP/1.1 " + status + "\r\nContent-Type: " + type + "\r\n\r\n" + body;
    }
}