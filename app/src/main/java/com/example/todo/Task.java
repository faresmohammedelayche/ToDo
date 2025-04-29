package com.example.todo;

public class Task {
    private String title;
    private String content;

    private boolean isCompleted;
    private boolean isArchived;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Task() {}

    public Task(String title, String content, boolean isCompleted, boolean isArchived) {
        this.title = title;
        this.content = content;
        this.isCompleted = isCompleted;
        this.isArchived = isArchived;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }
}
