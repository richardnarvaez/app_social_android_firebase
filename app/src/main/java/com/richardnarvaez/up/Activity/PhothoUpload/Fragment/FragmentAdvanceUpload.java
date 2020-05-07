package com.richardnarvaez.up.Activity.PhothoUpload.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.richardnarvaez.up.Fragment.NewPostUploadTaskFragment;

import java.util.List;

import com.richardnarvaez.up.R;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by macbookpro on 3/30/18.
 */

@SuppressLint("ValidFragment")
public class FragmentAdvanceUpload extends Fragment implements EasyPermissions.PermissionCallbacks {

    private Upload callbacks;
    private Context mApplicationContext;
    private ImageView mImageView;

    public interface Upload {
        void setData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload, container, false);
        mImageView = v.findViewById(R.id.new_post_picture);
        return v;
    }

    public void sendData(Bitmap image) {
        mImageView.setImageBitmap(image);
        //return thumbnail;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewPostUploadTaskFragment.TaskCallbacks) {
            callbacks = (FragmentAdvanceUpload.Upload) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
        mApplicationContext = context.getApplicationContext();
    }

}
