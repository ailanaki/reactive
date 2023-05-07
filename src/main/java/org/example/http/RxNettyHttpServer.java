package org.example.http;
import org.example.dao.Service;
import org.example.model.Currency;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.List;

public class RxNettyHttpServer implements Server {

    private final Service service;

    public RxNettyHttpServer(Service service) {
        this.service = service;
    }

    @Override
    public Observable<String> getResponse(HttpServerRequest<ByteBuf> req) {
        long id;

        switch (req.getDecodedPath()) {
            case "/products", "/register", "/add-product" -> id = Long.parseLong(getQueryParam(req, "id"));
            default -> {
                return Observable.just(req.getDecodedPath());
            }
        }

        return switch (req.getDecodedPath()) {
            case "/products" -> getProducts(id);
            case "/register" -> register(id, req);
            case "/add-product" -> addProduct(id, req);
            default -> Observable.just(req.getDecodedPath());
        };
    }

    private Observable<String> getProducts(long id) {
        return service.getLocalProductsForUser(id);
    }

    private Observable<String> register(long id, HttpServerRequest<ByteBuf> req) {
        Currency currency = Currency.valueOf(getQueryParam(req, "currency").toUpperCase());
        return service.addUser(id, currency);
    }

    private Observable<String> addProduct(long id, HttpServerRequest<ByteBuf> req) {
        long price = Long.parseLong(getQueryParam(req, "price"));
        String name = getQueryParam(req, "name");
        return service.addProduct(id, price, name);
    }

    private String getQueryParam(HttpServerRequest<ByteBuf> req, String key) {
        List<String> values = req.getQueryParameters().get(key);
        if (values.isEmpty()) {
            throw new RuntimeException("No username provided");
        }
        return values.get(0);
    }
}