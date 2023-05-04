package com.example.quizapp.Views;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizapp.Model.QuestionModel;
import com.example.quizapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddQuestionsFragment extends Fragment {

    private EditText question, answer, optionA, optionB, optionC;
    private Button saveButton, nextButton;
    private NavController navController;
    private FirebaseFirestore firestore;
    private String quizId;
    private int questionNumber = 1, totalQuestions;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_questions, container, false);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        firestore = FirebaseFirestore.getInstance();
        question = view.findViewById(R.id.Question);
        answer = view.findViewById(R.id.Answer);
        optionA = view.findViewById(R.id.Option_a);
        optionB = view.findViewById(R.id.option_b);
        optionC = view.findViewById(R.id.option_c);
        saveButton = view.findViewById(R.id.Save);
        nextButton = view.findViewById(R.id.NextQues);

        // Retrieve the quiz ID and total number of questions passed from the previous fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            quizId = bundle.getString("quizId");
            totalQuestions = (int) bundle.getLong("numQuestions");
        }



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
                navController.navigate(R.id.action_addQuestionsFragment_to_listFragment);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();

                // Increment the question number and update the UI
                questionNumber++;
                if (questionNumber == totalQuestions
                ) {
                    // Hide the "Next" button if this is the last or second to last question
                    nextButton.setVisibility(View.INVISIBLE);
                    Log.d("AddQuestionsFragment", "nextButton visibility: " + nextButton.getVisibility());
                }
                Log.d("AddQuestionsFragment", "questionNumber: " + questionNumber);
                Log.d("AddQuestionsFragment", "totalQuestions: " + totalQuestions);
            }
        });

        return view;
    }
    private void saveQuestion() {
        String questionStr = question.getText().toString();
        String answerStr = answer.getText().toString();
        String optionAStr = optionA.getText().toString();
        String optionBStr = optionB.getText().toString();
        String optionCStr = optionC.getText().toString();
        QuestionModel model = new QuestionModel(answerStr, questionStr, optionAStr, optionBStr, optionCStr, 15L);

        // Add the QuestionModel object to Firestore and clear the input fields
        firestore.collection("Quiz").document(quizId).collection("Questions").add(model)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Show a success message and clear the input fields
                        Toast.makeText(getContext(), "Question added successfully", Toast.LENGTH_SHORT).show();
                        question.setText("");
                        answer.setText("");
                        optionA.setText("");
                        optionB.setText("");
                        optionC.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show an error message if there was an error adding the question
                        Toast.makeText(getContext(), "Could not add question", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}