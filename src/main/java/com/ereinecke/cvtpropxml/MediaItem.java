package com.ereinecke.cvtpropxml;

/**
 * Definition and constructor of Media class for cvtprop
 * This is used to find a list of <wp:post_id> for a given </wp:post_parent>,
 * so that media items are assigned to property listings
 *
 * MediaItems sort in ascending order by post_parent
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class MediaItem implements Comparable<MediaItem> {

    int post_id;
    int post_parent;
    String post_name;
    String link;

    public MediaItem(int post_id, int post_parent,
                     String post_name, String link) {
        this.post_id = post_id;
        this.post_parent = post_parent;
        this.post_name = post_name;
        this.link = link;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getPost_id() {
        return post_id;
    }
    
    public void setPost_parent(int post_parent) {
        this.post_parent = post_parent;
    }
    
    public int getPost_parent() {
        return post_parent;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }
    
    public String getPost_name() {
        return post_name;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
    public String getLink() {
        return link;
    }


    // MediaItem sorts ascending by post_parent
    public int compareTo(MediaItem compareMediaItemParent) {

        int compareParent = ((MediaItem) compareMediaItemParent).getPost_parent();

        return this.getPost_parent() - compareParent;
    }

}

