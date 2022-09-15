package com.app.livewave.utils;

import com.app.livewave.models.ResponseModels.UserModel;

import java.util.List;

public class ReactionsCategoryList {
    List<UserModel> allReactions;
    List<UserModel> smileReactions;
    List<UserModel> mehReactions;
    List<UserModel> sadReactions;
    List<UserModel> wowReactions;
    List<UserModel> angryReactions;
    List<UserModel> laughingReactions;
    List<UserModel> cryingReactions;

    public List<UserModel> getCryingReactions() {
        return cryingReactions;
    }

    public void setCryingReactions(List<UserModel> cryingReactions) {
        this.cryingReactions = cryingReactions;
    }

    public List<UserModel> getAllReactions() {
        return allReactions;
    }

    public void setAllReactions(List<UserModel> allReactions) {
        this.allReactions = allReactions;
    }

    public List<UserModel> getSmileReactions() {
        return smileReactions;
    }

    public void setSmileReactions(List<UserModel> smileReactions) {
        this.smileReactions = smileReactions;
    }

    public List<UserModel> getMehReactions() {
        return mehReactions;
    }

    public void setMehReactions(List<UserModel> mehReactions) {
        this.mehReactions = mehReactions;
    }

    public List<UserModel> getSadReactions() {
        return sadReactions;
    }

    public void setSadReactions(List<UserModel> sadReactions) {
        this.sadReactions = sadReactions;
    }

    public List<UserModel> getWowReactions() {
        return wowReactions;
    }

    public void setWowReactions(List<UserModel> wowReactions) {
        this.wowReactions = wowReactions;
    }

    public List<UserModel> getAngryReactions() {
        return angryReactions;
    }

    public void setAngryReactions(List<UserModel> angryReactions) {
        this.angryReactions = angryReactions;
    }

    public List<UserModel> getLaughingReactions() {
        return laughingReactions;
    }

    public void setLaughingReactions(List<UserModel> laughingReactions) {
        this.laughingReactions = laughingReactions;
    }

    public ReactionsCategoryList(List<UserModel> allReactions,List<UserModel> smileReactions, List<UserModel> mehReactions, List<UserModel> sadReactions,
                                 List<UserModel> wowReactions, List<UserModel> angryReactions
            ,List<UserModel> laughingReactions,List<UserModel> cryingReaction) {
        this.allReactions = allReactions;
        this.smileReactions = smileReactions;
        this.mehReactions = mehReactions;
        this.sadReactions = sadReactions;
        this.wowReactions = wowReactions;
        this.angryReactions = angryReactions;
        this.laughingReactions = laughingReactions;
        this.cryingReactions = cryingReaction;
    }
}
