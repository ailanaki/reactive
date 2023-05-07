package org.example.http;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

public interface Server {
   Observable<String> getResponse(HttpServerRequest<ByteBuf> req);
}