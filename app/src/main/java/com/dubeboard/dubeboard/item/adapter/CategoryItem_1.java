package com.dubeboard.dubeboard.item.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dubeboard.dubeboard.R;
import com.dubeboard.dubeboard.clsCategory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryItem_1 extends ArrayAdapter<clsCategory> {
    Context context;
    int layoutResourceId;
    ArrayList<clsCategory> data = new ArrayList<clsCategory>();

    public CategoryItem_1(Context context, int layoutResourceId, ArrayList<clsCategory> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        clsCategory Record = data.get(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, null);

            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Ejecutar la Tarea de acuerdo a la version de Android
        LoadView Task = new LoadView(convertView, Record);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder);
        } else {
            Task.execute(holder);
        }
        return convertView;
    }

    /** Clase Asincrona para recuperar los datos de la fila **/
    protected class LoadView extends AsyncTask<ViewHolder, Void, HashMap<String, Object>> {
        protected ViewHolder v;

        protected clsCategory Record;
        protected View convertView;

        public LoadView(View convertView, clsCategory Record) {
            this.Record = Record;
            this.convertView = convertView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected HashMap<String, Object> doInBackground(ViewHolder... params) {
            v = params[0];
            // Obtener la imagen
            byte[] outImage = Record.get_image();
            Bitmap bmp;
            if (outImage != null){
                ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                bmp = BitmapFactory.decodeStream(imageStream);
            } else {
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_def_48x48);
            }
            HashMap<String, Object> res = new HashMap<String, Object>();
            res.put("bmp", bmp);
            // Obtener el numero de Imagenes relacionadas a esta categoria
            res.put("num_images", Record.getNumImages(context));
            return res;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> res) {
            super.onPostExecute(res);

            v.txtTitle.setText(Record.get_name());
            v.txtTitle.setVisibility(View.VISIBLE);
            v.txtCount.setText(res.get("num_images") + "");
            v.txtCount.setVisibility(View.VISIBLE);
            v.imgIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.imgIcon.setImageBitmap((Bitmap) res.get("bmp"));
        }
    }

    static class ViewHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtCount;
    }
}
