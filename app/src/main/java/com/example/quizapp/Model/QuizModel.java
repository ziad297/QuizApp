package com.example.quizapp.Model;


import com.google.firebase.firestore.DocumentId;

import java.security.PrivateKey;

public class QuizModel {
    @DocumentId
    private String QuizId;
    private String title,image,difficulty;
    private Long questions;

    public QuizModel(String quizId, String title, String image, String difficulty, Long questions) {
        QuizId = quizId;
        this.title = title;
        this.image = image;
        this.difficulty = difficulty;
        this.questions = questions;
    }

    public QuizModel() {
    }

    public String getQuizId() {
        return QuizId;
    }

    public void setQuizId(String quizId) {
        QuizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getQuestions() {
        return questions;
    }

    public void setQuestions(Long questions) {
        this.questions = questions;
    }
}
