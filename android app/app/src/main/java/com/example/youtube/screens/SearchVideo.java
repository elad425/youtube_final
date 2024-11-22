package com.example.youtube.screens;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.adapters.SearchAdapter;
import com.example.youtube.viewmodels.SearchViewModel;

import java.util.ArrayList;

public class SearchVideo extends AppCompatActivity {

    private SearchAdapter searchAdapter;
    private SearchViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupWindow();
        setupUI();
        setupSearchView();
        observeViewModel();
    }

    private void setupWindow() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
    }

    private void setupUI() {
        setupRecyclerView();
        setupBackButton();
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchAdapter(new ArrayList<>(), this, this);
        RecyclerView rvSearch = findViewById(R.id.rv_search);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(searchAdapter);
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.search_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.filterVideos(newText);
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getFilteredVideos().observe(this, filteredVideos ->
                searchAdapter.setFilteredList(new ArrayList<>(filteredVideos)));
    }
}