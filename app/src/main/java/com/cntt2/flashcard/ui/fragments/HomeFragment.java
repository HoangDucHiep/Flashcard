package com.cntt2.flashcard.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.cntt2.flashcard.R;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private ListView ShowFolderAndDeckLV;
    private ArrayList<Folder> folderList;
    private ShowFoldersAndDecksAdapter adapter;
    private FloatingActionButton AddFolderAndDeckFAB;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        ShowFolderAndDeckLV = view.findViewById(R.id.ShowFoldersAndDecksLV);
        AddFolderAndDeckFAB = view.findViewById(R.id.AddFoldersAndDecksFAB);
        folderList = createSampleFolderStructure();

        adapter = new ShowFoldersAndDecksAdapter(getActivity(), folderList);
        ShowFolderAndDeckLV.setAdapter(adapter);

        // Click của fab
        AddFolderAndDeckFAB.setOnClickListener(view1 -> {
            showCreateBottomSheet();
        });

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

        // Xử lý nút Save
        view.findViewById(R.id.btnFolderSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText folderNameInput= view.findViewById(R.id.folderNameInput);
                EditText parentFolderNameInput= view.findViewById(R.id.SelectParentFolder);

                String folderName=folderNameInput.getText().toString();
                String parentFolderName=parentFolderNameInput.getText().toString();
                String createAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date());
                Folder newFolder = new Folder(folderName, createAt);

                // Nếu không chọn parent hoặc chuỗi rỗng → thêm vào folder gốc
                if (parentFolderName == null || parentFolderName.trim().isEmpty()) {
                    folderList.add(newFolder);
                } else {
                    Folder parentFolder = findFolderByName(folderList, parentFolderName);
                    if (parentFolder != null) {
                        parentFolder.setExpanded(true);
                        newFolder.setParentFolderId(parentFolder.getId());
                        parentFolder.addSubFolder(newFolder);
                    }
                }

                adapter.updateFolderList(folderList);
                folderDialog.dismiss();
            }
        });

        folderDialog.show();
    }

    private Folder findFolderByName(List<Folder> folders, String name) {
        for (Folder folder : folders) {
            if (folder.getName().equals(name)) {
                return folder;
            }

            Folder found = findFolderByName(folder.getSubFolders(), name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }


    private void showCreateDeckDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_deck, null);
        BottomSheetDialog deckDialog = new BottomSheetDialog(requireContext());
        deckDialog.setContentView(view);

        view.findViewById(R.id.btnDeckSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText deckNameInput = view.findViewById(R.id.deckNameInput);
                EditText selectedFolderInput = view.findViewById(R.id.SelectedFolder);

                String deckName = deckNameInput.getText().toString();
                String selectedFolderName = selectedFolderInput.getText().toString();

                String createAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date());


                Desk newDeck = new Desk(deckName, -1, createAt);

                Folder targetFolder = findFolderByName(folderList, selectedFolderName);
                if (targetFolder != null) {
                    newDeck.setFolderId(targetFolder.getId());
                    targetFolder.addDesk(newDeck);
                    targetFolder.setExpanded(true);
                }

                adapter.updateFolderList(folderList);
                deckDialog.dismiss();
            }
        });

        deckDialog.show();
    }




    private ArrayList<Folder> createSampleFolderStructure() {
        ArrayList<Folder> folders = new ArrayList<>();

        // Folder 1
        Folder folder1 = new Folder("Android Development", "2024-04-05");
        folder1.setId(1);


        Folder subfolder1 = new Folder("UI Components", "2024-04-05");
        subfolder1.setId(11);
        subfolder1.setParentFolderId(1);

        Folder subfolder2 = new Folder("Android 3", "2024-04-05");
        subfolder2.setId(12);
        subfolder2.setParentFolderId(1);


        Folder subsubfolder1 = new Folder("MVVM", "2024-04-05");
        subsubfolder1.setId(121);
        subsubfolder1.setParentFolderId(12);

        Folder subsubfolder2 = new Folder("Clean", "2024-04-05");
        subsubfolder2.setId(122);
        subsubfolder2.setParentFolderId(12);

        subfolder2.addSubFolder(subsubfolder1);
        subfolder2.addSubFolder(subsubfolder2);

        folder1.addSubFolder(subfolder1);
        folder1.addSubFolder(subfolder2);

        // Folder 2
        Folder folder2 = new Folder("Java Program", "2024-04-05");
        folder2.setId(2);

        Folder subfolder3 = new Folder("OOP Concepts", "2024-04-05");
        subfolder3.setId(21);
        subfolder3.setParentFolderId(2);

        Desk desk1= new Desk("Desk1",1,"2024-04-05");
        Desk desk2= new Desk("Desk2",2,"2024-04-05");
        Desk desk3= new Desk("Desk3",12,"2024-04-05");
        subfolder3.addDesk(desk1);
        folder2.addDesk(desk2);
        folder2.addDesk(desk3);


        folder2.addSubFolder(subfolder3);

        // Folder 3
        Folder folder3 = new Folder("Design", "2024-04-05");
        folder3.setId(3);
        Desk desk4= new Desk("Desk4",3,"2024-04-05");


        folders.add(folder1);
        folders.add(folder2);
        folders.add(folder3);


        return folders;
    }
}



