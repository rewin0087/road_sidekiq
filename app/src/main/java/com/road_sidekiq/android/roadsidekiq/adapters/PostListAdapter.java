package com.road_sidekiq.android.roadsidekiq.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.models.Post;

import org.apache.http.ParseException;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by rewin0087 on 4/19/15.
 */
public class PostListAdapter extends BaseAdapter {

    public Activity activity;

    public List<Post> posts;

    public PostListAdapter(Activity activity, List<Post> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_post, parent, false);

            holder = new ViewHolder();
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.createdAt = (TextView) convertView.findViewById(R.id.created_at);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Post post = posts.get(position);

        long msTime = System.currentTimeMillis();


        holder.description.setText(post.getDescription());
        holder.location.setText(post.getLocation().toString());
        holder.category.setText(post.getCategory().toString());
        holder.createdAt.setText(post.getCreated_at().toString());

        return convertView;
    }

    public long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "d MMMM yyyy, hh:mm aa");

        long dateInMillis = 0;
        try {
            java.util.Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.d("ERROR PARSING", "Exception while parsing date. " + e.getMessage());
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public class ViewHolder {
        TextView description;
        TextView category;
        TextView location;
        TextView createdAt;
    }
}
