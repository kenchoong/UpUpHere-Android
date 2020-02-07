package upuphere.com.upuphere.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import upuphere.com.upuphere.app.AppController;
import upuphere.com.upuphere.helper.PrefManager;
import upuphere.com.upuphere.repositories.UserRepo;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Log.d("New Token 1",newToken);

        new PrefManager(AppController.getContext()).setFirebaseToken(newToken);
        UserRepo.getInstance().updateFirebaseTokenToServer(AppController.getContext(),newToken);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

    }
}
