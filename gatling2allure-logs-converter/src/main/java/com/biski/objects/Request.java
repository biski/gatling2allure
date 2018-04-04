package com.biski.objects;

/**
 * Created by wojciech on 14.01.18.
 */
public class Request {
    Session session;

    public Request(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
