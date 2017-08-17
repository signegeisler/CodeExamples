package dtu_se2.spotme.web_connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dtu_se2.spotme.R;
import dtu_se2.spotme.dtos.Group;
import dtu_se2.spotme.dtos.User;

/**
 * {@link android.os.AsyncTask} for using the webservice at
 * {@link dtu_se2.spotme.web_connection.GetFriendsTask#URL_GET_FRIENDS_SERVICE}.
 *
 * @author s123992, s113415
 */
public class GetFriendsTask extends AsyncTask<Void, Void, List<User>> {

    private final String URL_GET_FRIENDS_SERVICE;
    private Context context;

    /**
     * Constructor used when this task is executed.
     *
     * @param context The context from which the task is executed.
     */
    public GetFriendsTask(Context context){
        this.context = context;
        URL_GET_FRIENDS_SERVICE = "https://" + context.getString(R.string.bluemix_url) + "/api/friends?authName={authName}&secret={secret}";
    }

    /**
     * Gets the user's friends.
     *
     * @return {@link List<dtu_se2.spotme.dtos.User>} List of {@link dtu_se2.spotme.dtos.User}s
     * that are friends with the user logged in on this device or empty list if there was an error.
     */
    @Override
    protected List<User> doInBackground(Void... voids) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String authName = sharedPreferences.getString(context.getString(R.string.authUsername), null);
        String secret = sharedPreferences.getString(context.getString(R.string.authPassword), null);

        Map<String, String> map = new HashMap<String, String>();
        map.put("authName", authName);
        map.put("secret", secret);

        Group friendGroup = new Group();
        try {
            RestTemplate restTemplate = new RestTemplate();
            friendGroup = restTemplate.getForObject(URL_GET_FRIENDS_SERVICE, Group.class, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (friendGroup != null) {
            return friendGroup.getMembers();
        }
        return new ArrayList<User>();
    }
}


