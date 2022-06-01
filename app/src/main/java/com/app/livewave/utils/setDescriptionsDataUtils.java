package com.app.livewave.utils;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.CommentModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.TagModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class setDescriptionsDataUtils {
    public static final String URL_REGEX = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
    public static List<TagModel> tagsData = new ArrayList<>();
    //    public static PostModel postModel;
    public static CommentModel commentModel;
    public static Context mcontext;
    public static List<String> allHashTags = new ArrayList<>();

    public static void setTagsForComments(Context mcontext, CommentModel mcommentModel, TextView tv_comments, int position) {
        Log.d("123!@#", "i am herer  " + mcommentModel + "  : " + mcontext);
        if (mcommentModel == null || mcontext == null) {
            return;
        }

        mcontext = mcontext;
        commentModel = mcommentModel;
        if (commentModel.getComment() != null && !commentModel.getComment().equals("")) {
            if (isContainLink(commentModel.getComment())) {
                String url = extractUrls(commentModel.getComment());
                if (!url.equalsIgnoreCase("")) {
                    //make sure it's youtube
                    if (url.contains("youtube") || url.contains("youtu")) {
                        String youtubeUrl = url.substring(url.lastIndexOf("=") + 1);
                        if (!youtubeUrl.equals(url)) {
                            String youtube;
                            youtube = "https://img.youtube.com/vi/" + youtubeUrl + "/0.jpg";
                        }
                    }

                    if (commentModel.getTagData().size() > 0) {
                        Log.d("123!@#", "i am here3");

                        tagsData = commentModel.getTagData();
                        highlightTagsForComment(mcontext, tv_comments, commentModel.getComment(), position);
                    } else {
                        tv_comments.setText(commentModel.getComment());
                    }
                }
            } else {
                if (commentModel.getTagData().size() > 0) {
                    Log.d("123!@#", "i am here4");

                    tagsData = commentModel.getTagData();
                    highlightTagsForComment(mcontext, tv_comments, commentModel.getComment(), position);
                } else {
                    tv_comments.setText(commentModel.getComment());
                }
            }
            if (commentModel.getComment().contains("#")) {
                highlightTagsForComment(mcontext, tv_comments, commentModel.getComment(), position);
            }
        }
    }

    public static boolean isContainLink(String comment) {
        Pattern p = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(comment);//replace with string to compare
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String extractUrls(String text) {
        String url = "";
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        if (urlMatcher.find()) {
            url = text.substring(urlMatcher.start(0),
                    urlMatcher.end(0));

        } else if (url.equals("")) {
            String urlRegex2 = "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

            Pattern pattern2 = Pattern.compile(urlRegex2, Pattern.CASE_INSENSITIVE);
            Matcher urlMatcher2 = pattern2.matcher(text);
            if (urlMatcher2.find()) {
                url = text.substring(urlMatcher2.start(0),
                        urlMatcher2.end(0));
            }
        }


        return url;
    }

    public static void highlightTagsForPost(Context context, TextView tv_post, PostModel postModel, int position) {

        SpannableString ss = new SpannableString(postModel.getDescription());
        String remainingString = postModel.getDescription();
        ArrayList<Integer> temp = new ArrayList<>();


        int first, last, previous = 0;
        int counterTags = 0;
        int counterHashtags = 0;
        for (int i = 0; i < postModel.getDescription().length(); i++) {
            if (postModel.getDescription().charAt(i) == ' ') {
                remainingString = postModel.getDescription().substring(i + 1);
            } else {
                if (postModel.getDescription().charAt(i) == '@' && !temp.contains(i)) {
                    temp.add(i);
                    first = i;
                    if (remainingString.contains(" ")) {
                        last = first + remainingString.indexOf(" ");
                    } else {
                        last = postModel.getDescription().length() - 1;
                    }
                    try {
                        ss.setSpan(new MyClickableSpanForPost(context, counterTags, position, false, postModel), first, last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        previous = last + 1;
                        remainingString = postModel.getDescription().substring(previous);
                        counterTags++;
                    } catch (Exception e) {

                    }
                }
                if (postModel.getDescription().charAt(i) == '#' && !temp.contains(i)) {
                    temp.add(i);
                    first = i;
                    if (remainingString.contains(" ")) {
                        last = first + remainingString.indexOf(" ");
                    } else {
                        last = postModel.getDescription().length() - 1;
                    }
                    try {
                        if (allHashTags.size() <= 0) {
                            allHashTags = Arrays.asList(remainingString.split(" "));
                        }
                        ss.setSpan(new MyClickableSpanForPost(context, counterHashtags, position, true, postModel, allHashTags), first, last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        previous = last + 1;
                        remainingString = postModel.getDescription().substring(previous);
                        counterHashtags++;
                    } catch (Exception e) {

                    }
                }
            }
        }
        tv_post.setText(ss);
        tv_post.setMovementMethod(LinkMovementMethod.getInstance());
        tv_post.setHighlightColor(Color.TRANSPARENT);
    }

    public static class MyClickableSpanForPost extends ClickableSpan {

        int pos, counter;

        // type boolean explain: FALSE means tag type TRUE means # type
        boolean type_hash_tag;
        Context mcontext;
        PostModel postModel;
        String hashTagClicked;

        public MyClickableSpanForPost(Context context, int counter, int position, boolean type_hash_tag, PostModel postModel) {
            this.pos = position;
            this.counter = counter;
            this.type_hash_tag = type_hash_tag;
            this.mcontext = context;
            this.postModel = postModel;
        }

        //this is to get hast tags list
        public MyClickableSpanForPost(Context context, int counter, int position, boolean type_hash_tag, PostModel postModel, List<String> hashtags) {
            this.pos = position;
            this.counter = counter;
            this.type_hash_tag = type_hash_tag;
            this.mcontext = context;
            this.postModel = postModel;
            this.hashTagClicked = hashtags.get(counter).replace("#", "");
        }

        @Override
        public void onClick(View widget) {
            if (!type_hash_tag) {
                if (postModel != null) {
                    if (postModel.getTagsData().size() > 0)
                        ((HomeActivity)mcontext).openUserProfile(postModel.getTagsData().get(counter).getId().toString());
//                        BaseUtils.openUserProfile(postModel.getTagsData().get(counter).getId().toString(), mcontext);
                }
            } else {
//                Intent intent = new Intent(mcontext, HashtagFragment.class);
//                intent.putExtra(Constants.HASH_TAG, hashTagClicked);
//                mcontext.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HASH_TAG, hashTagClicked);
                bundle.putString(HEADER_TITLE, hashTagClicked);
                ((HomeActivity)mcontext).loadFragment(R.string.tag_hashtag,bundle);
            }
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mcontext.getResources().getColor(R.color.buttercup));

            if (type_hash_tag) {
                ds.setColor(mcontext.getResources().getColor(R.color.purple_700));
            } else {
                ds.setColor(mcontext.getResources().getColor(R.color.buttercup));
            }
            ds.setUnderlineText(false);
        }
    }

    public static void highlightTagsForComment(Context context, TextView tv_comment, String commentDes, int position) {

        SpannableString ss = new SpannableString(commentDes);
        String remainingString = commentDes;
        ArrayList<Integer> temp = new ArrayList<>();
        Log.d("!@#hello", "hilight");

        int first, last, previous = 0;
        int counterTags = 0;
        int counterHashtags = 0;
        for (int i = 0; i < commentDes.length(); i++) {
            if (commentDes.charAt(i) == ' ') {
                remainingString = commentDes.substring(i + 1);
            } else {
                if (commentDes.charAt(i) == '@' && !temp.contains(i)) {
                    temp.add(i);
                    first = i;
                    if (remainingString.contains(" ")) {
                        last = first + remainingString.indexOf(" ");
                    } else {
                        last = commentDes.length() - 1;
                    }
                    try {

                        if (allHashTags.size() <= 0) {
                            allHashTags = Arrays.asList(remainingString.split(" "));
                        }
                        ss.setSpan(new MyClickableSpanForComments(context, counterTags, position, false, commentModel, allHashTags), first, last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        previous = last + 1;
                        remainingString = commentDes.substring(previous);
                        counterTags++;
                    } catch (Exception e) {
                        Log.d("!@#hello2", "hilight");

                    }
                }
                if (commentDes.charAt(i) == '#' && !temp.contains(i)) {
                    temp.add(i);
                    first = i;
                    if (remainingString.contains(" ")) {
                        last = first + remainingString.indexOf(" ");
                    } else {
                        last = commentDes.length() - 1;
                    }
                    try {
                        ss.setSpan(new MyClickableSpanForComments(context, counterHashtags, position, true, commentModel), first, last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        previous = last + 1;
                        remainingString = commentDes.substring(previous);
                        counterHashtags++;
                    } catch (Exception e) {
                        Log.d("2@#hello", "hilight");

                    }
                }
            }
        }
        tv_comment.setText(ss);
        tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
        tv_comment.setHighlightColor(Color.TRANSPARENT);
    }

    public static class MyClickableSpanForComments extends ClickableSpan {

        int pos, counter;

        // type boolean explain: FALSE means tag type TRUE means # type
        boolean type_hash_tag;
        Context mcontext;
        CommentModel commentModel;
        String hashTagClicked;

        public MyClickableSpanForComments(Context context, int counter, int position, boolean type_hash_tag, CommentModel comm) {
            this.pos = position;
            this.counter = counter;
            this.type_hash_tag = type_hash_tag;
            this.mcontext = context;
            this.commentModel = comm;
        }

        //this is to get hast tags list
        public MyClickableSpanForComments(Context context, int counter, int position, boolean type_hash_tag, CommentModel comm, List<String> hashtags) {
            this.pos = position;
            this.counter = counter;
            this.type_hash_tag = type_hash_tag;
            this.mcontext = context;
            this.commentModel = comm;
            this.hashTagClicked = hashtags.get(counter).replace("#", "");
        }


        @Override
        public void onClick(View widget) {
            if (!type_hash_tag) {
                ((HomeActivity)mcontext).openUserProfile(commentModel.getTagData().get(counter).getId().toString());
//                BaseUtils.openUserProfile(commentModel.getTagData().get(counter).getId().toString(), mcontext);
            } else {
//                Intent intent = new Intent(mcontext, HashtagFragment.class);
//                intent.putExtra(Constants.HASH_TAG, hashTagClicked);
//                mcontext.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HASH_TAG, hashTagClicked);
                bundle.putString(HEADER_TITLE, hashTagClicked);
                ((HomeActivity)mcontext).loadFragment(R.string.tag_hashtag,bundle);
            }
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mcontext.getResources().getColor(R.color.buttercup));

            if (type_hash_tag) {
                ds.setColor(mcontext.getResources().getColor(R.color.purple_700));
            } else {
                ds.setColor(mcontext.getResources().getColor(R.color.buttercup));
            }
            ds.setUnderlineText(false);
        }
    }

}