package com.app.livewave.interfaces;

import com.app.livewave.models.ResponseModels.CommentModel;

public interface EditCommentInterface {
    void editComment(CommentModel commentModel,int position);
}
