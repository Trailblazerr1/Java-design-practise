package com.lld.designpatterns.observer.pubsub;

public class Driver {
    public static void main(String[] args) {
        CommonPublisher commonPublisher = new CommonPublisher();

        //If others want to subscribe to commonPUblisher then pass it in constructor
        IndividualSubscriber individualSubscriber = new IndividualSubscriber(commonPublisher);
        GroupSubscriber groupSubscriber = new GroupSubscriber(commonPublisher);
        commonPublisher.notifyAllSubscribers();
    }
}
