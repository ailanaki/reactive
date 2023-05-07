package org.example;

import org.example.dao.ReactiveDao;
import org.example.dao.Service;
import org.example.http.RxNettyHttpServer;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;
import io.reactivex.netty.protocol.http.server.HttpServer;
import org.example.http.Server;
import rx.Observable;

public class Main {

    public static final int SERVER_PORT = 8080;
    public static final int CLIENT_PORT = 27017;

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create("mongodb://localhost:" + CLIENT_PORT)) {
            MongoDatabase db = client.getDatabase("reactive");
            Server server = new RxNettyHttpServer(new Service(new ReactiveDao(
                    db.getCollection("users"), db.getCollection("products"))));

            HttpServer
                    .newServer(SERVER_PORT)
                    .start((req, resp) -> {
                        Observable<String> response = server.getResponse(req);
                        return resp.writeString(response);
                    })
                    .awaitShutdown();
        }
    }
}