package com.example.tdm_social_media.Model;

public class Comment {

    private String comment;
    private String publisher;
    private String commentid;



    private long create_at;

    public Comment(String comment, String publisher, String commentid, long create_at) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.create_at = create_at;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }

}
