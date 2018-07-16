package com.fabianbleile.fordigitalimmigrants.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabianbleile.fordigitalimmigrants.MainActivity;
import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Contact mContact;
    private boolean useContact;

    public ImageAdapter(Context c, boolean useContact, Contact contact) {
        Log.e("Test", contact + "");
        mContext = c;
        this.useContact = useContact;
        if(this.useContact){
            mContact = contact;
        }
    }

    @Override
    public int getCount() {
        return MainActivity.mIcons.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        int layoutIdForListItem = R.layout.grid_view_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(layoutIdForListItem,viewGroup,false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_grid_view_item);
        TextView textViewBubbleDescription = (TextView) rootView.findViewById(R.id.tv_bubble_description);
        TextView textViewCapitalIcon = (TextView) rootView.findViewById(R.id.tv_capital_icon);
        TextView textViewBubbleContent = (TextView) rootView.findViewById(R.id.tv_bubble_content);

        String iconDescription = mContext.getResources().getString(MainActivity.mIcons.get(position));

        if(iconDescription == mContext.getResources().getString(R.string.ctv_facebook)){
            imageView.setImageResource(R.drawable.ic_facebook);
        } else if(iconDescription == mContext.getResources().getString(R.string.ctv_instagram)){
            imageView.setImageResource(R.drawable.ic_instagram);
        }else if(iconDescription == mContext.getResources().getString(R.string.ctv_snapchat)){
            imageView.setImageResource(R.drawable.ic_snapchat);
        }else if(iconDescription == mContext.getResources().getString(R.string.ctv_twitter)){
            imageView.setImageResource(R.drawable.ic_twitter);
        }else {
            imageView.setImageResource(mIconImages[0]);
            String iconCapitalLetter = iconDescription.toString().substring(0,1);
            textViewCapitalIcon.setText(iconCapitalLetter);
        }
        textViewBubbleDescription.setText(iconDescription);

        if(mContact != null){
            String[] mFriendsIconContentLink = {
                    mContact.getName(),
                    mContact.getPhonenumber(),
                    mContact.getEmail(),
                    mContact.getBirthday(),
                    mContact.getHometown(),
                    mContact.getInstagram(),
                    mContact.getFacebook(),
                    mContact.getSnapchat(),
                    mContact.getTwitter(),
                    mContact.getLocation()
            };
            textViewBubbleContent.setText(mFriendsIconContentLink[position]);
        } else {
            textViewBubbleContent.setText(MainActivity.getDefaults(iconDescription, mContext));
        }
        rootView.setTag(iconDescription);
        rootView.setContentDescription(iconDescription);

        return rootView;
    }

    //background images for the icon gridView
    public Integer[] mIconImages = {
        R.drawable.ic_circle_primary
    };
}
