package com.cntt2.flashcard.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cntt2.flashcard.App;
import com.cntt2.flashcard.R;
import com.cntt2.flashcard.data.repository.DeskRepository;
import com.cntt2.flashcard.data.repository.FolderRepository;
import com.cntt2.flashcard.model.Desk;
import com.cntt2.flashcard.model.Folder;
import com.cntt2.flashcard.ui.adapters.ShowFoldersAndDecksAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ListView ShowFolderAndDeckLV;
    private ArrayList<Folder> folderList;
    private ShowFoldersAndDecksAdapter adapter;
    private FloatingActionButton AddFolderAndDeckFAB;

    private FolderRepository folderRepository = App.getInstance().getFolderRepository();
    private DeskRepository deskRepository = App.getInstance().getDeskRepository();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ShowFolderAndDeckLV = view.findViewById(R.id.ShowFoldersAndDecksLV);
        AddFolderAndDeckFAB = view.findViewById(R.id.AddFoldersAndDecksFAB);
        folderList = getFoldersFromLocalDb();
        adapter = new ShowFoldersAndDecksAdapter(getActivity(), folderList);
        ShowFolderAndDeckLV.setAdapter(adapter);
        AddFolderAndDeckFAB.setOnClickListener(v -> showCreateBottomSheet());
        return view;
    }

    private void showCreateBottomSheet() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_new, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(view);
        dialog.show();

        view.findViewById(R.id.CreateFolder).setOnClickListener(v -> {
            showCreateFolderDialog();
            dialog.dismiss();
        });
        view.findViewById(R.id.CreateDeck).setOnClickListener(v -> {
            showCreateDeckDialog();
            dialog.dismiss();
        });
    }

    private void showCreateFolderDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_folder, null);
        BottomSheetDialog folderDialog = new BottomSheetDialog(requireContext());
        folderDialog.setContentView(view);

        Spinner parentFolderSpinner = view.findViewById(R.id.parentFolderSpinner);
        List<String> folderNames = getFolderNamesWithIndent();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, folderNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentFolderSpinner.setAdapter(spinnerAdapter);

        view.findViewById(R.id.btnFolderSave).setOnClickListener(v -> {
            EditText folderNameInput = view.findViewById(R.id.folderNameInput);
            String folderName = folderNameInput.getText().toString().trim();
            if (folderName.isEmpty()) {
                folderNameInput.setError("Folder name cannot be empty");
                return;
            }
            int selectedPosition = parentFolderSpinner.getSelectedItemPosition();
            createNewFolder(folderName, selectedPosition);
            folderDialog.dismiss();
        });

        folderDialog.show();
    }

    private void createNewFolder(String folderName, int selectedPosition) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Folder newFolder = new Folder(folderName, createdAt);
        List<Folder> allFolders = getAllFoldersList();

        if (selectedPosition == 0) {
            folderList.add(newFolder);
        } else {
            Folder parentFolder = allFolders.get(selectedPosition - 1);
            newFolder.setParentFolderId(parentFolder.getId());
            parentFolder.addSubFolder(newFolder);
            parentFolder.setExpanded(true);
        }

        long insertedId = folderRepository.insertFolder(newFolder);
        if (insertedId != -1) {
            newFolder.setId((int) insertedId);
            adapter.updateFolderList(folderList);
            Toast.makeText(requireContext(), "Folder created successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCreateDeckDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_deck, null);
        BottomSheetDialog deckDialog = new BottomSheetDialog(requireContext());
        deckDialog.setContentView(view);

        Spinner folderSpinner = view.findViewById(R.id.folderSpinner);
        List<String> folderNames = getFolderNamesWithIndent();
        folderNames.set(0, getString(R.string.select_folder_prompt)); // "Please select a folder"
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, folderNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        folderSpinner.setAdapter(spinnerAdapter);

        view.findViewById(R.id.btnDeckSave).setOnClickListener(v -> {
            EditText deckNameInput = view.findViewById(R.id.deckNameInput);
            String deckName = deckNameInput.getText().toString().trim();
            if (deckName.isEmpty()) {
                deckNameInput.setError("Deck name cannot be empty");
                return;
            }
            int selectedPosition = folderSpinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                Toast.makeText(requireContext(), "Please select a folder", Toast.LENGTH_SHORT).show();
                return;
            }
            createNewDesk(deckName, selectedPosition);
            deckDialog.dismiss();
        });

        deckDialog.show();
    }

    private void createNewDesk(String deckName, int selectedPosition) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Desk newDesk = new Desk(deckName, 0, createdAt);
        List<Folder> allFolders = getAllFoldersList();
        Folder parentFolder = allFolders.get(selectedPosition - 1);
        newDesk.setFolderId(parentFolder.getId());

        long insertedId = deskRepository.insertDesk(newDesk);
        if (insertedId != -1) {
            newDesk.setId((int) insertedId);
            parentFolder.addDesk(newDesk);
            parentFolder.setExpanded(true);
            adapter.updateFolderList(folderList);
            Toast.makeText(requireContext(), "Desk created successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to create desk", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getFolderNamesWithIndent() {
        List<String> folderNames = new ArrayList<>();
        folderNames.add(getString(R.string.no_parent_folder)); // "No Parent Folder"
        getAllFoldersWithIndent(folderList, folderNames, "");
        return folderNames;
    }

    private void getAllFoldersWithIndent(List<Folder> folders, List<String> folderNames, String indent) {
        for (Folder folder : folders) {
            folderNames.add(indent + folder.getName());
            getAllFoldersWithIndent(folder.getSubFolders(), folderNames, indent + "  ");
        }
    }

    private List<Folder> getAllFoldersList() {
        List<Folder> allFolders = new ArrayList<>();
        getAllFolders(folderList, allFolders);
        return allFolders;
    }

    private void getAllFolders(List<Folder> folders, List<Folder> allFolders) {
        for (Folder folder : folders) {
            allFolders.add(folder);
            getAllFolders(folder.getSubFolders(), allFolders);
        }
    }

    private ArrayList<Folder> getFoldersFromLocalDb() {
        return (ArrayList<Folder>) folderRepository.getNestedFolders();
    }
}