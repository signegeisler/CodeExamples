package dtu_se2.spotme.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

import dtu_se2.spotme.R;
import dtu_se2.spotme.activity.ViewEventInfoActivity;
import dtu_se2.spotme.activity.ViewGroupmembersAsCreatorActivity;
import dtu_se2.spotme.adapter.GroupCheckListAdapter;
import dtu_se2.spotme.adapter.UserChecklistAdapter;
import dtu_se2.spotme.dtos.Group;
import dtu_se2.spotme.dtos.User;

/**
 *  Specifies a Dialog Fragment where the user can invite other users (friends or possibly friends
 *  and groups) to an {@link dtu_se2.spotme.dtos.Event} or a {@link dtu_se2.spotme.dtos.Group}.
 *
 *  Started from {@link dtu_se2.spotme.activity.ViewEventInfoActivity} and
 *  {@link dtu_se2.spotme.activity.ViewGroupmembersAsCreatorActivity}.
 *
 * @author s123992
 */
public class InvitePeopleFragment extends DialogFragment {

    private static List<User> friendList;
    private static List<Group> groupList;
    private List<User> inviteThesePeople = new ArrayList<User>();
    private TabHost tabs;
    private static boolean onlyFriends;
    private static boolean fromEvent;

    /**
     * Specifies a convenient way of starting the fragment with extra data when the user wants to
     * invite friends or groups to an {@link dtu_se2.spotme.dtos.Event}
     * @param friends A list of the user's friends.
     * @param groups A list of the groups the user is member of or creator of.
     * @return A new instance of {@link dtu_se2.spotme.fragment.InvitePeopleFragment}.
     */
    public static InvitePeopleFragment newInstance(List<User> friends, List<Group> groups) {
        InvitePeopleFragment frag = new InvitePeopleFragment();
        friendList = friends;
        groupList = groups;
        onlyFriends = false;
        fromEvent = true;
        return frag;
    }

    /**
     * Specifies a convenient way of starting the fragment with extra data when the user wants to
     * invite friends to a {@link dtu_se2.spotme.dtos.Group}
     * @param friends A list of the user's friends.
     * @return A new instance of {@link dtu_se2.spotme.fragment.InvitePeopleFragment}.
     */
    public static InvitePeopleFragment newInstance(List<User> friends){
        InvitePeopleFragment frag = new InvitePeopleFragment();
        friendList = friends;
        onlyFriends = true;
        fromEvent = false;
        return frag;
    }

    /**
     * Builds a custom Dialog container where the user can choose people to invite to a group or an
     * event. If the user is inviting people to become members of a group, the dialog shows just the
     * user's friends available for invitation. Otherwise, if the user is inviting people to an
     * event, the dialog shows two tabs - one with the user's friends available for invitation and
     * one with the user's groups available for invitation. Sets up the title, message and buttons
     * of the dialog.
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a
     *                           freshly created Fragment
     * @return A new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.fragment_invite_people, null);
        builder.setView(convertView)
                .setTitle(getString(R.string.invite_people))
                .setPositiveButton(R.string.done,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Just dismiss the dialog
                            }
                        }
                );

        UserChecklistAdapter userChecklistAdapter = new UserChecklistAdapter(getActivity(), R.layout.checklist_item, friendList);
        ListView friendListView = (ListView) convertView.findViewById(R.id.invite_friends_list_view);
        friendListView.setAdapter(userChecklistAdapter);

        setUpOnFriendClickListener(friendListView);

        // only set up the group list, click listener and tabs if groups are available for invitation
        if(!onlyFriends) {
            GroupCheckListAdapter groupCheckListAdapter = new GroupCheckListAdapter(getActivity(), R.layout.checklist_item, groupList);
            ListView groupListView = (ListView) convertView.findViewById(R.id.invite_groups_list_view);
            groupListView.setAdapter(groupCheckListAdapter);
            setUpOnGroupClickListener(groupListView);

            tabs = (TabHost) convertView.findViewById(R.id.tabhost);
            setUpTabs();
        }
        return builder.create();
    }

    /**
     * Sets up the click listener for the groups in the list view. Tapping a group will add or
     * remove its members from the list of people to be invited, depending on whether the members
     * are already in the list.
     * @param groupListView ListView showing the groups and checkboxes.
     */
    private void setUpOnGroupClickListener(ListView groupListView) {
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checklist_item_checkbox);
                checkBox.setChecked(!checkBox.isChecked());
                Group group = (Group) parent.getItemAtPosition(position);

                if (checkBox.isChecked()) {
                    inviteThesePeople.addAll(group.getMembers());
                } else {
                    inviteThesePeople.removeAll(group.getMembers());
                }
            }
        });
    }

    /**
     * Sets up the click listener for the users in the list view. Tapping a user will add or
     * remove the user from the list of people to be invited, depending on whether the user is
     * already in the list.
     * @param friendListView ListView showing the friends and checkboxes.
     */
    private void setUpOnFriendClickListener(ListView friendListView) {
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checklist_item_checkbox);
                checkBox.setChecked(!checkBox.isChecked());
                User user = (User) parent.getItemAtPosition(position);

                if (checkBox.isChecked()) {
                    inviteThesePeople.add(user);
                } else {
                    inviteThesePeople.remove(user);
                }
            }
        });
    }

    /**
     * Sets up the tabs in the dialog.
     */
    private void setUpTabs() {
        tabs.setup();

        TabHost.TabSpec friendsTab = tabs.newTabSpec("friends");
        friendsTab.setContent(R.id.invite_friends_list_view);
        friendsTab.setIndicator(getString(R.string.friends));

        TabHost.TabSpec groupsTab = tabs.newTabSpec("groups");
        groupsTab.setContent(R.id.invite_groups_list_view);
        groupsTab.setIndicator(getString(R.string.groups));

        tabs.addTab(friendsTab);
        tabs.addTab(groupsTab);
    }

    /**
     * Adds the users (friends or group members) to the invited people in either an event or a group
     * depending on which activity started the fragment.
     */
    private void onPositiveClick() {
        if(fromEvent) {
            ((ViewEventInfoActivity) getActivity()).addToInvited(inviteThesePeople);
        } else {
            ((ViewGroupmembersAsCreatorActivity) getActivity()).addToInvited(inviteThesePeople);
        }
    }
}
