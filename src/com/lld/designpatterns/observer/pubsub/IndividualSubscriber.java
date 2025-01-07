package com.lld.designpatterns.observer.pubsub;

public class IndividualSubscriber implements ISubscriber {

    public IndividualSubscriber(IPublisher publisher) {
        publisher.addSubscriber(this);
    }

    @Override
    public void getUpdate() {
        System.out.println("Update received to individual");
    }
}
