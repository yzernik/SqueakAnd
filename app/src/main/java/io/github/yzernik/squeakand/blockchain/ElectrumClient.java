package io.github.yzernik.squeakand.blockchain;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Stream;

public class ElectrumClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private InetAddress address;
    private int port;

    public ElectrumClient(String host, int port) throws UnknownHostException {
        this.address = ElectrumClient.getInetAddress(host);
        this.port = port;
    }

    public ElectrumClient(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start() throws IOException {
        clientSocket = new Socket(address, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        InputStream socketInputStream = clientSocket.getInputStream();
        in = new BufferedReader(new InputStreamReader(socketInputStream));
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
        //String resp = in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void sendSubscribeMessage() throws IOException {
        String subscribeMessage = "{ \"id\": \"blk\", \"method\": \"blockchain.headers.subscribe\"}";
        Log.i(getClass().getName(), subscribeMessage);
        sendMessage(subscribeMessage);
    }

    public void sendGetBlockHeaderMessage() throws IOException {
        String getBlockHeaderMessage = "{ \"id\": \"blk\", \"method\": \"blockchain.block.header\", \"params\": [23]}";
        Log.i(getClass().getName(), getBlockHeaderMessage);
        sendMessage(getBlockHeaderMessage);
    }

    public static InetAddress selectAddress(InetAddress[] addresses) {
        // Get ip6 address if available
        for (InetAddress addr : addresses) {
            if (addr instanceof Inet6Address) {
                return (Inet6Address) addr;
            }
        }

        // Get any address if available
        if(addresses.length > 0) {
            return addresses[0];
        }

        return null;
    }

    public static InetAddress getInetAddress(String host) throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(host);
        return selectAddress(addresses);
    }

    public Stream<String> getResponseLines() throws IOException {
        // sendSubscribeMessage();

        //read file into stream, try-with-resources
        return in.lines();
    }

    public String getResponseLine() throws IOException {
        sendSubscribeMessage();

        return in.readLine();
    }

}
