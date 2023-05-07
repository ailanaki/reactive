package org.example.dao;
import org.example.model.*;
import rx.Observable;

public class Service {

    private final ReactiveDao dao;

    public Service(ReactiveDao dao) {
        this.dao = dao;
    }

    public Observable<String> getLocalProductsForUser(long id) {
        Observable<Currency> currency = dao.getUser(id).map(User::getCurrency);
        Observable<Product> products = dao.getProducts();
        return Observable.combineLatest(currency, products, (cur, product) -> product.toString(cur));
    }

    public Observable<String> addUser(long id, Currency currency) {
        return dao.addUser(new User(id, currency));
    }

    public Observable<String> addProduct(long id, long price, String name) {
        return dao.getUser(id)
                .map(User::getCurrency)
                .switchMap(currency -> dao.addProduct(new Product(name, price, currency)));
    }
}