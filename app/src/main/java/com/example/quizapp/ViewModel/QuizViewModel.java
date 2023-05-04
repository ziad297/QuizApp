package com.example.quizapp.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizapp.Model.QuestionModel;
import com.example.quizapp.Model.QuizModel;
import com.example.quizapp.Repository.QuizRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends ViewModel implements QuizRepository.onFirestoreTaskComplete {

    public FirestoreDao firestoreDao;

    private MutableLiveData<List<QuizModel>> quizLiveData = new MutableLiveData<>();

    private QuizRepository repository = new QuizRepository(this);

    public MutableLiveData<List<QuizModel>> getQuizLiveData() {
        return quizLiveData;
    }

    public QuizViewModel() {
        repository.getQuizData();
        firestoreDao = new FirestoreDao();
    }
    @Override
    public void quizDataLoaded(List<QuizModel> quizListModels) {
        quizLiveData.setValue(quizListModels);
    }

    @Override
    public void onError(Exception e) {
        Log.d("QuizERROR", "onError: " + e.getMessage());
    }
    public void loadQuizList() {
        firestoreDao.getQuizList().addOnSuccessListener(new OnSuccessListener<List<QuizModel>>() {
            @Override
            public void onSuccess(List<QuizModel> quizList) {
                quizLiveData.setValue(quizList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                quizLiveData.setValue(null);
            }
        });
    }
    public class FirestoreDao {
        private final FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

        public Task<List<QuizModel>> getQuizList() {
            CollectionReference quizColRef = firestoreDB.collection("Quiz");

            return quizColRef.get().continueWith(new Continuation<QuerySnapshot, List<QuizModel>>() {
                @Override
                public List<QuizModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        List<QuizModel> quizList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            QuizModel quiz = doc.toObject(QuizModel.class);
                            quizList.add(quiz);
                        }
                        return quizList;
                    } else {
                        throw task.getException();
                    }
                }
            });
        }
    }
}