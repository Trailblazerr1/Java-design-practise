package com.lld.designpatterns.observer.pubsub;

public class GroupSubscriber implements ISubscriber {

    public GroupSubscriber(IPublisher publisher) {
        publisher.addSubscriber(this);
    }
    @Override
    public void getUpdate() {
        System.out.println("Upate received to group");
    }
}
