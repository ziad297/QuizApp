package com.example.quizapp.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.Adapter.QuizAdapter;
import com.example.quizapp.Model.QuizModel;
import com.example.quizapp.R;
import com.example.quizapp.ViewModel.QuizViewModel;

import java.util.List;


public class ListFragment extends Fragment implements QuizAdapter.OnItemClickedListner {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NavController navController;
    private QuizViewModel viewModel;
    private QuizAdapter adapter;
    TextView textView;
    TextView textView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(QuizViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.ListQuiz);
        progressBar = view.findViewById(R.id.quizListProgressbar);
        navController = Navigation.findNavController(view);
        textView = view.findViewById(R.id.Add);
        textView2 = view.findViewById(R.id.signOut);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_listFragment_to_signInFragment);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              navController.navigate(R.id.action_listFragment_to_addQuizFragment);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuizAdapter(this);

        recyclerView.setAdapter(adapter);

        viewModel.getQuizLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizModel>>() {
            @Override
            public void onChanged(List<QuizModel> quizListModels) {
                progressBar.setVisibility(View.GONE);
                adapter.setQuizModels(quizListModels);
                adapter.notifyDataSetChanged();
            }
        });


    }
    @Override
    public void onBackPressed() {
        // Check if the current destination is the ListFragment
        if (navController.getCurrentDestination().getId() == R.id.listFragment) {
            // Navigate back to the sign-in fragment
            navController.navigate(R.id.action_listFragment_to_signInFragment);

        } else {
            // Otherwise, proceed with the default back button behavior
            navController.navigate(R.id.action_listFragment_to_signInFragment);

        }
    }
    @Override
    public void onItemClick(int position) {
        ListFragmentDirections.ActionListFragmentToDetailFragment action =
                ListFragmentDirections.actionListFragmentToDetailFragment();
        action.setPosition(position);
        navController.navigate(action);
    }
    @Override
    public void onResume() {
        super.onResume();

        // Load the quiz list from Firestore
        viewModel.loadQuizList();
    }
}