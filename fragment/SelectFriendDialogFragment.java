package dtu_se2.spotme.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dtu_se2.spotme.R;
import dtu_se2.spotme.activity.ViewFriendsActivity;
import dtu_se2.spotme.dtos.User;
import dtu_se2.spotme.web_connection.DeleteFriendTask;

/**
 * Specifies a Dialog Fragment where the user can choose different options for a user that is a
 * friend.
 *
 * Started from {@link dtu_se2.spotme.activity.ViewFriendsActivity}.
 *
 * @author s123992
 */
public class SelectFriendDialogFragment extends DialogFragment {

    private static User clickedFriend = null;

    /**
     * Specifies a convenient way of starting the fragment with extra data when the user long
     * presses a friend in the friend list.
     * @param friend The friend to show options for.
     * @return A new instance of {@link dtu_se2.spotme.fragment.SelectFriendDialogFragment}.
     */
    public static SelectFriendDialogFragment newInstance(User friend) {
        SelectFriendDialogFragment frag = new SelectFriendDialogFragment();
        Bundle args = new Bundle();
        args.putString("friend", friend.getFirstName() + " " + friend.getLastName());
        frag.setArguments(args);
        clickedFriend = friend;
        return frag;
    }

    /**
     * Builds a custom Dialog container where the user can choose between different options for the
     * friend. The user will be able to show the friend on the map, delete the friend and cancel the
     * dialog. Sets up the title, message and buttons of the dialog.
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a
     *                           freshly created Fragment
     * @return A new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String friend = getArguments().getString("friend");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.what_to_do_msg))
                .setMessage(friend)
                .setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                doPositiveClick();
                            }
                        }
                )
                .setNeutralButton(R.string.show_on_map,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                doNeutralClick();
                            }
                        })

                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Just dismiss the dialog.
                            }
                        });

        return builder.create();
    }

    /**
     * Shows the clicked user on the map by calling the method
     * {@link dtu_se2.spotme.activity.ViewFriendsActivity#showOnMap(dtu_se2.spotme.dtos.User)}
     */
    private void doNeutralClick() {
        if(clickedFriend != null){
            ((ViewFriendsActivity)getActivity()).showOnMap(clickedFriend);
        }
    }

    /**
     * Deletes the user from the user's friend list using
     * {@link dtu_se2.spotme.web_connection.DeleteFriendTask}.
     */
    public void doPositiveClick() {
        if(clickedFriend != null) {
            new DeleteFriendTask(getActivity().getApplicationContext()).execute(clickedFriend);
            ((ViewFriendsActivity)getActivity()).onDismissDialog(true, clickedFriend);
        }
    }
}
