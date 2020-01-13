package upuphere.com.upuphere.Interface;

import java.util.List;

import upuphere.com.upuphere.models.UserModel;

public interface AuthListener {

    void onSuccess();

    void onFailure(String error);
}
