package com.jarvis.zhihudemo.fragment.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/2/26 上午10:57
 * @changeRecord [修改记录] <br/>
 */

public class Fragment1 extends Fragment {

    RecyclerView recyclerView;

    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setOnClickListener(v -> {});

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        inflater = LayoutInflater.from(getContext());

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new VH(inflater.inflate(R.layout.item_fragment1, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setTransitionName(position+"");
                transaction.addSharedElement(holder.itemView, "recyclerview_" + position);
            }

            @Override
            public int getItemCount() {
                return 15;
            }
        });


        Fragment2 fragment2 = Fragment2.newInstance();
        fragment2.setSharedElementEnterTransition(new DetailsTransition());
        fragment2.setSharedElementReturnTransition(new DetailsTransition());

        view.findViewById(R.id.btn).setOnClickListener(v -> {
            transaction.replace(R.id.container, fragment2)
                    .addToBackStack(null)
                    .commit();
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
