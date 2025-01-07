package com.lld.designpatterns.observer.pubsub;

public interface IPublisher {
    boolean addSubscriber(ISubscriber subscriber);
    boolean removeSubscriber(ISubscriber subscriber);
    void notifyAllSubscribers();
}
