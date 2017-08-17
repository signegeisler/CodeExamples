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
import dtu_se2.spotme.dtos.User;
import dtu_se2.spotme.wrapper.ListWithUser;

/**
 * {@link android.os.AsyncTask} for using the webservice at
 * {@link dtu_se2.spotme.web_connection.GetUsersFromQueryTask#URL_GET_USERS_FROM_QUERY_SERVICE}.
 *
 * @author s123992
 */
public class GetUsersFromQueryTask extends AsyncTask<String, Void, List<User>> {

    private final String URL_GET_USERS_FROM_QUERY_SERVICE;
    private Context context;

    /**
     * Constructor used when this task is executed.
     *
     * @param context The context from which the task is executed.
     */
    public GetUsersFromQueryTask(Context context){
        this.context = context;
        URL_GET_USERS_FROM_QUERY_SERVICE = "https://" + context.getString(R.string.bluemix_url) + "/api/user/find?authName={authName}&secret={secret}&searchParam={searchParam}";
    }

    /**
     * Gets the search results matching a search query.
     *
     * @param strings Contains the search query entered by the user.
     * @return {@link List<dtu_se2.spotme.dtos.User>} List of Users matching the query in username,
     * first name and/or last name or empty list if there was an error or no results.
     */
    @Override
    protected List<User> doInBackground(String... strings) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String authName = sharedPreferences.getString(context.getString(R.string.authUsername), null);
        String secret = sharedPreferences.getString(context.getString(R.string.authPassword), null);

        List<User> queryResults = new ArrayList<User>();

        if (strings.length > 0 && strings[0] != null) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("authName", authName);
            map.put("secret", secret);
            map.put("searchParam", strings[0]);

            ListWithUser listWithUser;
            try {
                RestTemplate restTemplate = new RestTemplate();
                listWithUser = restTemplate.getForObject(URL_GET_USERS_FROM_QUERY_SERVICE, ListWithUser.class, map);
                queryResults.addAll(listWithUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return queryResults;
    }
}