package com.smona.app.editorimage;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class FontFamilyAdapter extends BaseAdapter {

    private ArrayList<String> mFontFamily = new ArrayList<String>();
    private Context mContext;
    private String mSelectedFont;
    private AssetManager mAsset;

    public FontFamilyAdapter(Context context, ArrayList<String> family) {
        mContext = context;
        mAsset = context.getAssets();
        mFontFamily = family;
    }

    public void setSelectedFont(String font) {
        mSelectedFont = font;
    }

    @Override
    public int getCount() {
        return mFontFamily.size();
    }

    @Override
    public Object getItem(int position) {
        return mFontFamily.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String fontFamily = mFontFamily.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.font_family_item, null);
        }
        convertView.setTag(fontFamily);
        convertView.setSelected(fontFamily.equals(mSelectedFont));
        Typeface typefaceRegular = Typeface.createFromAsset(mAsset, fontFamily);
        TextView text = (TextView) convertView.findViewById(R.id.font_text);
        text.setTypeface(typefaceRegular);
        return convertView;
    }
}
