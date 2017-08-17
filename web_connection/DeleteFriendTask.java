package dtu_se2.spotme.web_connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import dtu_se2.spotme.R;
import dtu_se2.spotme.dtos.User;

/**
 * {@link android.os.AsyncTask} for using the webservice at
 * {@link dtu_se2.spotme.web_connection.DeleteFriendTask#URL_DELETE_FRIEND_SERVICE}.
 *
 * @author s123992
 */
public class DeleteFriendTask extends AsyncTask<User, Void, Void> {

    private final String URL_DELETE_FRIEND_SERVICE;
    private Context context;

    public DeleteFriendTask(Context context){
        this.context = context;
        URL_DELETE_FRIEND_SERVICE = "https://" + context.getString(R.string.bluemix_url) + "/api/friends/{ID}?authName={authName}&secret={secret}";
    }

    /**
     * Deletes a friend from the user's friend list. This deletes the friend relation,
     * i.e. the friend will no longer be friends with the user either.
     *
     * @param users Contains the friend of the user to be deleted.
     * @return Always returns null.
     */
    @Override
    protected Void doInBackground(User... users) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String authName = sharedPreferences.getString(context.getString(R.string.authUsername), null);
        String secret = sharedPreferences.getString(context.getString(R.string.authPassword), null);

        if (users.length > 0 && users[0] != null) {
            User user = users[0];
            Map<String, Object> map = new HashMap<>();
            map.put("authName", authName);
            map.put("secret", secret);
            map.put("ID", user.getId());
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.delete(URL_DELETE_FRIEND_SERVICE, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}