package com.community.mua.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.DayBean;
import com.community.mua.views.MyGridLayoutManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodDiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodDiaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private ArrayList<DayBean> mParam1;
    private View rootView;

    public MoodDiaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dayBeans Parameter 1.
     * @return A new instance of fragment MoodDiaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoodDiaryFragment newInstance(ArrayList<DayBean> dayBeans ) {
        MoodDiaryFragment fragment = new MoodDiaryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, dayBeans);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.month_view, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView  = rootView.findViewById(R.id.rv_diary);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getContext(),7);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(new MoodDiaryAdapter(mParam1,getContext()));
    }
}