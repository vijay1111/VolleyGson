package vijay.com.volleygson;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by anupamchugh on 16/11/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomRecyclerView> {

    private List<UserList.UserDataList> itemList;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    public RecyclerViewAdapter(Context context, List<UserList.UserDataList> itemList) {
        this.itemList = itemList;
        mRequestQueue = SingletonRequestQueue.getInstance(context).getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @Override
    public CustomRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, null);
        CustomRecyclerView rcv = new CustomRecyclerView(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomRecyclerView holder, int position) {

        UserList.UserDataList myData = itemList.get(position);
        holder.txtLabel.setText(myData.first_name + " " + myData.last_name);
        holder.avatar.setImageUrl(myData.avatar, mImageLoader);


    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class CustomRecyclerView extends RecyclerView.ViewHolder {
        TextView txtLabel;
        NetworkImageView avatar;

        CustomRecyclerView(View itemView) {
            super(itemView);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            avatar = itemView.findViewById(R.id.imgNetwork);
        }
    }
}
