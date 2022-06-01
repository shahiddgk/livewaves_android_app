package com.app.livewave.models;

import java.util.ArrayList;
import java.util.List;

public class IdsAndTagsListModel {


    String ids,tags;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public IdsAndTagsListModel(String ids, String tags) {
        this.ids = ids;
        this.tags = tags;
    }
    //
//    List<String> ids=new ArrayList();
//    List<String> tags=new ArrayList();
//
//    public IdsAndTagsListModel(List<String> ids, List<String> tags) {
//        this.ids = ids;
//        this.tags = tags;
//    }
//
//    public List<String> getIds() {
//        return ids;
//    }
//
//    public void setIds(List<String> ids) {
//        this.ids = ids;
//    }
//
//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
}
