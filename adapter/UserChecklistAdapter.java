package dtu_se2.spotme.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dtu_se2.spotme.R;
import dtu_se2.spotme.dtos.User;

/**
 * Adapter class specifying how to show {@link dtu_se2.spotme.dtos.User} objects along with
 * {@link android.widget.CheckBox} objects for each user in a list view.
 *
 * Used in {@link dtu_se2.spotme.fragment.InvitePeopleFragment}
 *
 * @author s123992
 */
public class UserChecklistAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private Context context;

    /**
     * Constructor for creating a new instance of this class.
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param users The users to represent in the list view.
     */
    public UserChecklistAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Gets a View that displays the data at the specified position in the list view.
     * @param position The position of the item in the list view to return the view for.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will be attached to.
     * @return A view for the user at the given position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserHolder userHolder;
        User user = users.get(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            userHolder = new UserHolder();
            convertView = inflater.inflate(R.layout.checklist_item, null);
            userHolder.mainText = (TextView) convertView.findViewById(R.id.checklist_item_main_text);
            userHolder.secondaryText = (TextView) convertView.findViewById(R.id.checklist_item_secondary_text);
            convertView.setTag(userHolder);
        } else {
            userHolder = (UserHolder) convertView.getTag();
        }

        userHolder.mainText.setText(user.getFirstName() + " " + user.getLastName());
        userHolder.secondaryText.setText(user.getUserName());

        return convertView;
    }

    /**
     * Holder class for a view in the list.
     */
    static class UserHolder {
        TextView mainText;
        TextView secondaryText;
    }
}