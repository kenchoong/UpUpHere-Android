package upuphere.com.upuphere.helper;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SpannableStringHelper {

    public static Button createSpannableTextButton(Button targetTextView, String completeString,
                                    String partToClick, ClickableSpan clickableAction) {

        SpannableString spannableString = new SpannableString(completeString);

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        int startPosition = completeString.indexOf(partToClick);
        int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();

        spannableString.setSpan(clickableAction, startPosition, endPosition,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        targetTextView.setText(spannableString);

        return targetTextView;
    }

    public static TextView createLink(TextView targetTextView, String completeString,
                                      String partToClick, ClickableSpan clickableAction) {

        SpannableString spannableString = new SpannableString(completeString);

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        int startPosition = completeString.indexOf(partToClick);
        int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();

        spannableString.setSpan(clickableAction, startPosition, endPosition,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return targetTextView;
    }

    public static TextView makeLinks(TextView targetTextView,String completeString, List<String> partToBeClick, List<ClickableSpan> clickableActions ){
        SpannableString spannableString = new SpannableString(completeString);

        for(int i=0; i < partToBeClick.size(); i++){
            ClickableSpan clickableAction = clickableActions.get(i);
            String partToClick = partToBeClick.get(i);

            int startPosition = completeString.indexOf(partToClick);
            int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();

            spannableString.setSpan(clickableAction, startPosition, endPosition,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return targetTextView;
    }
}
