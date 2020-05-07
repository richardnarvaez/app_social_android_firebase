package com.richardnarvaez.up.Interface;

/**
 * Created by macbookpro on 5/31/18.
 */

public interface OnFeedPostListener {

    void onPostComment(String postKey);

    void onPostLike(String postKey);

    void onPostSave(String postKey);

}
