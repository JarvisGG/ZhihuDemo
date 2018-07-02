package com.jarvis.zhihudemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.avtivity.GalleryViewPagerActivity;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

public final class TestFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";

	private WebView mWebView;


	public static TestFragment newInstance(String content) {
		TestFragment fragment = new TestFragment();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 30; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();
		
		return fragment;
	}
	
	private String mContent = "???";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

		ZhihuRecyclerView recyclerView = new ZhihuRecyclerView(getContext());
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(getContext()));
		ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
		recyclerView.setObjectAdapter(adapter);
		for (int i = 0 ; i < 20; i++) {
			adapter.add(new InnerData(mContent));
		}


		TextView text = new TextView(getActivity());
		text.setText(mContent);
		text.setTextSize(20 * getResources().getDisplayMetrics().density);
		text.setPadding(20, 20, 20, 20);
		text.setGravity(Gravity.CENTER);

		mWebView = new WebView(getContext());
		mWebView.loadUrl("http://www.qq.com");
		
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);
		layout.addView(mWebView);
		
		return layout;
	}

	public WebView getWebView() {
		return mWebView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	class InnerPresenter extends Presenter {

		private Context mContext;
		private LayoutInflater mInflater;

		public InnerPresenter(Context context) {
			this.mContext = context;
			this.mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent) {
			View view = mInflater.inflate(R.layout.item_float_view, parent, false);
			return new InnerViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
			final InnerData data = (InnerData) item;
			final InnerViewHolder holder = (InnerViewHolder) viewHolder;
			if (position == 10) {
				View child = viewHolder.view;
				ViewGroup.LayoutParams params = child.getLayoutParams();
				params.height = ExplosionUtils.dp2Px(180);
			}
			holder.tv.setText(data.title);
			holder.view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});
		}

		@Override
		public void onUnBindViewHolder(ViewHolder viewHolder) {

		}

		class InnerViewHolder extends ViewHolder {

			public TextView tv;
			public InnerViewHolder(View view) {
				super(view);
				tv = view.findViewById(R.id.main_tv);
			}
		}


	}

	static class InnerData {
		String title;

		public InnerData(String text) {
			this.title = text;
		}
	}



}
