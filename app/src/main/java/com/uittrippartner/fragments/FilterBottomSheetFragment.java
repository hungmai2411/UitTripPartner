package com.uittrippartner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.uittrippartner.R;
import com.uittrippartner.adapter.FilterAdapter;
import com.uittrippartner.hotel.Sort;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {
    RecyclerView rcvSort;
    FilterAdapter sortAdapter;
    int index;
    int state;
    IClickItem iClickItem;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    //create custom theme for your bottom sheet modal
    @Override
    public int getTheme() {
        //return super.getTheme();
        return R.style.AppBottomSheetDialogTheme;
    }

    public FilterBottomSheetFragment(IClickItem iClickItem) {
        this.iClickItem = iClickItem;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvSort = view.findViewById(R.id.rcvSort);
        LinearLayoutManager linearLayoutPaymentManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        sortAdapter = new FilterAdapter(getContext(), new FilterAdapter.IClickSortItem() {
            @Override
            public void onCallBack(String choice) {
//                if(choice.equals("")){
//                    setState(0);
//                    sortAdapter.notifyDataSetChanged();
//                    iClickItem.onCallBack(choice);
//                    dismiss();
//                }else{
                    setState(1);
                    sortAdapter.notifyDataSetChanged();
                    iClickItem.onCallBack(choice);
                    dismiss();
                //}
            }
        });

        List<Sort> listSort = new ArrayList<>();
        listSort.add(new Sort("Booked",false));
        listSort.add(new Sort("Cancelled",false));
        listSort.add(new Sort("Successfully",false));

        sortAdapter.setData(listSort);
        rcvSort.setLayoutManager(linearLayoutPaymentManager);
        rcvSort.setAdapter(sortAdapter);
    }

    public interface IClickItem{
        void onCallBack(String status);
    }
}