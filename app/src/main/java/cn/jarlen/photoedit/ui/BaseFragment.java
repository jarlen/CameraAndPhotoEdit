package cn.jarlen.photoedit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
    //根视图
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(), container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onDestroy() {
//		ImageLoader.getInstance(getActivity()).clearMemoryCache();
        //Glide.get(getActivity()).clearMemory();
        ActivityPageManager.unbindReferences(root);
        super.onDestroy();
        root = null;
    }

    /**
     * 返回当前视图
     *
     * @return R.layout.xxx;
     */
    public abstract int getLayoutId();

    /**
     * 在onViewCreated方法中执行
     *
     * @param root
     */
    public abstract void initViews(View root);
}
