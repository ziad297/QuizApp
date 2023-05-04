package com.example.quizapp.Views;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizapp.Model.QuizModel;
import com.example.quizapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AddQuizFragment extends Fragment {

    private ImageView imageView;
    private EditText difficulty, numQuestions, quizName;
    private NavController navController;
    private Button nextButton;
    int SELECT_PICTURE = 200;
    FirebaseFirestore firestore;
    String quizId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_quiz, container, false);

        firestore = FirebaseFirestore.getInstance();
        imageView = view.findViewById(R.id.ImgeQuiz);
        difficulty = view.findViewById(R.id.Diff);
        numQuestions = view.findViewById(R.id.NumQues);
        quizName = view.findViewById(R.id.QuizName);
        nextButton = view.findViewById(R.id.Next);
        navController = NavHostFragment.findNavController(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qname, qdiff;

                qname = quizName.getText().toString();
                qdiff = difficulty.getText().toString();
                Long nques = Long.parseLong(numQuestions.getText().toString());

                // Get the image from the ImageView and convert it to a byte array
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Upload the image to Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = imagesRef.putBytes(data);

                // Add an OnSuccessListener to get the download URL of the image and store it in Firestore
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                // Create a new QuizModel object and store it in Firestore
                                QuizModel quizModel = new QuizModel();
                                quizModel.setTitle(qname);
                                quizModel.setDifficulty(qdiff);
                                quizModel.setQuestions(nques);
                                quizModel.setImage(imageUrl);

                                // Generate a unique ID for the quiz and store it in Firestore
                                DocumentReference quizRef = firestore.collection("Quiz").document();
                                quizModel.setQuizId(quizRef.getId());
                                quizRef.set(quizModel);

                                // Pass the document ID and total number of questions to the next fragment
                                quizId = quizModel.getQuizId();
                                AddQuestionsFragment addQuestionsFragment = new AddQuestionsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("quizId", quizId);
                                bundle.putLong("numQuestions", nques);
                                addQuestionsFragment.setArguments(bundle);
                                navController.navigate(R.id.action_addQuizFragment_to_addQuestionsFragment, bundle);

                                // Show a success message and finish the activity
                                Toast.makeText(getContext(), "Quiz added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show an error message if there was an error uploading the image
                        Toast.makeText(getContext(), "Could not upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }
}