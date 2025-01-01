package com.lld.design.observer.pubsub;

public interface IPublisher {
    boolean addSubscriber(ISubscriber subscriber);
    boolean removeSubscriber(ISubscriber subscriber);
    void notifyAllSubscribers();
}
