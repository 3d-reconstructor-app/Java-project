package com.example.a3dmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/* TODO @@@ANDREY
    this is main place, where you will write your code to tell AS what to do with this tab
    -- first of all you should decide the xml file structure for this tab -- "fragment_tab_main.xml"
    I've already made some structure there
    .
   TODO but not sure, that "View" button is necessary, so remove it if it is not needed
    .
    Recycle View is a place were all lines with projects will be shown
    for Recycle View you should create an Adapter class, to handle the information that lays inside it
    .
    I suggest this plan --- when user creates his first project, you should show a window, where he will type this project name
    and then save it and continue work as it should be
    .
    and then he will be able to click on "new project" and create a new one, and the previous will be saved somehow
    and displayed in the RecycleView
    .
    I would suggest to remember the date, time and maybe something else about every project and display it (this is easy part)

 */

/*
    ADVICE
    you can see the "tabPhoto.java", "GridAdapter" and "ImageData" as examples
 */


public class tabMainMenu extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_main_menu, container, false);
    }
}
