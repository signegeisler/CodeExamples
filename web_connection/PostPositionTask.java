package dtu_se2.spotme.web_connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import dtu_se2.spotme.R;
import dtu_se2.spotme.dtos.Position;

/**
 * {@link android.os.AsyncTask} for using the webservice at
 * {@link dtu_se2.spotme.web_connection.PostPositionTask#URL_POST_POSITION_SERVICE}.
 *
 * @author s123992
 */
public class PostPositionTask extends AsyncTask<Position, Void, Position> {

    private final String URL_POST_POSITION_SERVICE;
    private Context context;

    /**
     * Constructor used when this task is executed.
     *
     * @param context The context from which the task is executed.
     */
    public PostPositionTask(Context context){
        this.context = context;
        URL_POST_POSITION_SERVICE = "https://" + context.getString(R.string.bluemix_url) + "/api/user/position?authName={authName}&secret={secret}";
    }

    /**
     * Posts a {@link dtu_se2.spotme.dtos.Position}.
     *
     * @param positions Contains the newest user {@link dtu_se2.spotme.dtos.Position}.
     * @return The {@link dtu_se2.spotme.dtos.Position} received by the webservice or null if there
     *         was an error.
     */
    @Override
    protected Position doInBackground(Position... positions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String authName = sharedPreferences.getString(context.getString(R.string.authUsername), null);
        String secret = sharedPreferences.getString(context.getString(R.string.authPassword), null);

        if (positions.length > 0 && positions[0] != null) {
            Map<String, String> map = new HashMap<>();
            map.put("authName", authName);
            map.put("secret", secret);

            Position position = positions[0];
            try {
                RestTemplate restTemplate = new RestTemplate();
                position = restTemplate.postForObject(URL_POST_POSITION_SERVICE, position, Position.class, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return position;
        }
        return null;
    }
}