package upuphere.com.upuphere.ui.onboarding;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Objects;

import upuphere.com.upuphere.R;
import upuphere.com.upuphere.app.AppConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgreementFragment extends Fragment {


    public static final int PRIVACY_POLICY = 1111;
    public static final int TERM_OF_USE = 2222;

    public AgreementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_agreement, container, false);

        WebView webView = view.findViewById(R.id.webview);
        final ProgressBar loading = view.findViewById(R.id.progressBar9);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loading.bringToFront();
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                loading.setVisibility(View.GONE);
            }
        });

        int agreementType = AgreementFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getAgreementType();

        if(agreementType == PRIVACY_POLICY){
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.privacy_policy);
            webView.loadUrl(AppConfig.URL_PRIVACY_POLICY);
        }
        else if(agreementType == TERM_OF_USE){
            Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.term_of_use);
            webView.loadUrl(AppConfig.URL_TERM_AND_CONDITION);
        }

        return view;
    }

}
