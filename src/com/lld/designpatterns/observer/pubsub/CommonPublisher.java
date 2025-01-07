package com.lld.designpatterns.observer.pubsub;

import java.util.ArrayList;
import java.util.List;

public class CommonPublisher implements IPublisher {
    List<ISubscriber> subscriberList = new ArrayList<>();
    @Override
    public boolean addSubscriber(ISubscriber subscriber) {
        return subscriberList.add(subscriber);
    }

    @Override
    public boolean removeSubscriber(ISubscriber subscriber) {
        return subscriberList.remove(subscriber);
    }

    @Override
    public void notifyAllSubscribers() {
        for(ISubscriber subscriber : subscriberList) {
            subscriber.getUpdate();
        }
    }
}
