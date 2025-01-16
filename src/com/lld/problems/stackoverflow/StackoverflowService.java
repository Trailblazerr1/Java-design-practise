package com.lld.problems.stackoverflow;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//////services
public  class User {
    String userId;
    String name;
    int reps;

    public void addReps(int reputationForQuestion) {
        this.reps +=reputationForQuestion;
    }
}

public abstract class Post {
    int postId;
    String postContent;
    Instant postCreatedTime;
    User postedBy;
    List<Comment> comments;

    public Post(int postId, String postContent, Instant postCreatedTime, User postedBy) {
        this.postId = postId;
        this.postContent = postContent;
        this.postCreatedTime = postCreatedTime;
        this.postedBy = postedBy;
    }

    public void createComment(Comment comment) {
        this.comments.add(comment);
    }
}


public class Question extends Post{
    List<PostTag> tags;
    int votes;

    public Question(int questionId, User user, String content, List<PostTag> tags) {
        super(questionId,content,Instant.now(),user);
        addTags(tags);
    }
    
    private void addTags(List<PostTag> tags) {
        this.tags = tags;
    }

    public void addVote(int value) {
        this.votes = this.votes + value;
    }
}

public  class Answer extends Post{
    int votes;

    public Answer(int answerId, User user, String content) {
        super(answerId,content,Instant.now(),user);
    }
    
    public void addVote(int value) {
        this.votes = this.votes + value;
    }}

public class Comment extends Post {

    public Comment(int commentId, User user, String content) {
        super(commentId,content,Instant.now(),user);
    }
}

public enum PostTag {
    PROGRAMMING, DEVOPS, CLOUD
}

public class InMemoryRepository { //We can make separate UserRepo, AnswerRepo and all, but to simplify only one
    private static Map<Integer,User> userMap = new ConcurrentHashMap<>();
    private static Map<Integer,Question> questionMap = new ConcurrentHashMap<>();
    private static Map<Integer,Answer> answerMap = new ConcurrentHashMap<>();

    public static Map<Integer,User> getUserMap() {
        return userMap;
    }

    public static Map<Integer,Question> getQuestionMap() {
        return questionMap;
    }
    public static Map<Integer,Answer> getAnswerMap() {
        return answerMap;
    }
}

public class UserService {
    private static final int REPUTATION_FOR_QUESTION = 5;
    private static final int REPUTATION_FOR_ANSWER = 10;
    private static final int REPUTATION_FOR_COMMENT = 1;

    private static final AtomicInteger questionIDCounter = new AtomicInteger(0);
    private static final AtomicInteger answerIDCounter = new AtomicInteger(0);
    private static final AtomicInteger commentIDCounter = new AtomicInteger(0);
    private static final AtomicInteger voteIDCounter = new AtomicInteger(0);

    public void addQuestion(User user, String content, List<PostTag> tags) {
        int questionId = questionIDCounter.incrementAndGet();
        Question question = new Question(questionId, user,content,tags);
        InMemoryRepository.getQuestionMap().put(questionId,question);
        user.addReps(REPUTATION_FOR_QUESTION);
    }

    public void addAnswer(User user, String content) {
        int answerId = answerIDCounter.incrementAndGet();
        Answer answer = new Answer(answerId,user,content);
        InMemoryRepository.getAnswerMap().put(answerId,answer);
        user.addReps(REPUTATION_FOR_ANSWER);
    }

    public void addCommentinQuestion(User user, String content, int questionId) {
        Question question = InMemoryRepository.getQuestionMap().get(questionId);
        Objects.requireNonNull(question);
        int commentId = commentIDCounter.incrementAndGet();
        Comment comment = new Comment(commentId,user,content);
        question.createComment(comment);
        user.addReps(REPUTATION_FOR_COMMENT);
    }

    public void addCommentinAnswer(User user, String content, int answerId) {
        Question answer = InMemoryRepository.getQuestionMap().get(answerId);
        Objects.requireNonNull(answer);
        int commentId = commentIDCounter.incrementAndGet();
        Comment comment = new Comment(commentId,user,content);
        answer.createComment(comment);
        user.addReps(REPUTATION_FOR_COMMENT);    }

    public void voteQuestion(int questionId, int value, User user) {
        Question question = InMemoryRepository.getQuestionMap().get(questionId);
        Objects.requireNonNull(question);
        question.addVote(value);
    }

    public void voteAnswer(int answerId, int value, User user) {
        Answer answer = InMemoryRepository.getAnswerMap().get(answerId);
        Objects.requireNonNull(answer);
        answer.addVote(value);
    }

    public List<Question> searchQuestions(String query) {
        //make an index for search
        return null;
    }
}
