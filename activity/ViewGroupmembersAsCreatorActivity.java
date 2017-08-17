package dtu_se2.spotme.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dtu_se2.spotme.MapsActivity;
import dtu_se2.spotme.R;
import dtu_se2.spotme.adapter.UserListAdapter;
import dtu_se2.spotme.dtos.Group;
import dtu_se2.spotme.dtos.Invitation;
import dtu_se2.spotme.dtos.User;
import dtu_se2.spotme.fragment.InvitePeopleFragment;
import dtu_se2.spotme.fragment.SelectGroupMemberDialogFragment;
import dtu_se2.spotme.web_connection.DeleteRemoveGroupMemberTask;
import dtu_se2.spotme.web_connection.GetFriendsTask;
import dtu_se2.spotme.web_connection.PostGroupInviteTask;

/**
 * Activity that shows a list of {@link dtu_se2.spotme.dtos.User}s that are members of a specific
 * {@link dtu_se2.spotme.dtos.Group}. This activity is used for showing the group members and
 * associated options to the creator of a group and is not accessible for regular group members.
 * The group members are clickable. From this activity it is possible to invite
 * {@link dtu_se2.spotme.dtos.User}s to the group.
 *
 * Started from {@link dtu_se2.spotme.activity.ViewGroupsActivity}.
 *
 * @see dtu_se2.spotme.activity.ViewGroupmembersAsMemberActivity
 *
 * @author s123992
 */
public class ViewGroupmembersAsCreatorActivity extends Activity {

    private UserListAdapter listAdapter;
    private Group group;

    /**
     * Called when the activity is starting. Sets up the view.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = (Group) getIntent().getSerializableExtra("group");

        List<User> memberList = new ArrayList<>();

        if (group != null) {
            memberList = group.getMembers();
        }
        setContentView(R.layout.groupc_member_view);
        ListView listView = (ListView) findViewById(R.id.groupc_member_list_view);
        Button addButton = (Button) findViewById(R.id.groupc_member_add_button);

        setUpOnClickListener(addButton);
        setUpOnItemLongClickListener(listView);

        listAdapter = new UserListAdapter(this, R.layout.list_item, memberList);
        listView.setAdapter(listAdapter);
    }

    /**
     * Sets up the click listener for the add members button. Tapping the button will bring up a
     * dialog with the option to invite friends to the group.
     * @param addButton The {@link android.widget.Button} to set the onClickListener on.
     */
    private void setUpOnClickListener(Button addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInviteDialog();
            }
        });
    }

    /**
     * Sets up the long click listener for the group members in the list view. Long pressing a
     * group member will bring up a dialog with options for the group member.
     * @param listView ListView showing the group members.
     */
    private void setUpOnItemLongClickListener(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User member = (User) parent.getItemAtPosition(position);
                showMemberDialog(member);
                return true;
            }
        });
    }

    /**
     * Opens the Dialog Fragment for inviting people to the group.
     * Starts {@link dtu_se2.spotme.fragment.InvitePeopleFragment}
     */
    private void showInviteDialog() {
        List<User> friendList = getFriends();

        DialogFragment newFragment = InvitePeopleFragment.newInstance(friendList);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * Opens the Dialog Fragment for the group member with the options that apply when the user
     * logged in on this device is creator of the group for which we are viewing members. Starts
     * {@link dtu_se2.spotme.fragment.SelectGroupDialogFragment}.
     * @param member The group member for which the options should be shown.
     */
    public void showMemberDialog(User member) {
        DialogFragment newFragment = SelectGroupMemberDialogFragment.newInstance(member, true);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * Update the list view if a group member is deleted.
     * @param delete Whether the group member is to be deleted.
     * @param member The group member that was deleted.
     */
    public void onDismissDialog(boolean delete, User member) {
        if(delete) {
            listAdapter.remove(member);
        }
    }

    /**
     * Removes a member of the group using the
     * {@link dtu_se2.spotme.web_connection.DeleteRemoveGroupMemberTask}.
     * @param member The member of the group to be removed.
     */
    public void removeGroupMember(User member){
        Object[] objects = {group, member};
        new DeleteRemoveGroupMemberTask(getApplicationContext()).execute(objects);
    }

    /**
     * Invites the {@link User}s in the input list by sending an
     * {@link dtu_se2.spotme.dtos.Invitation} to each of them using
     * {@link dtu_se2.spotme.web_connection.PostGroupInviteTask}.
     *
     *
     * @param newlyInvited List of {@link dtu_se2.spotme.dtos.User} objects to be invited to the
     *                     group.
     */
    public void addToInvited(List<User> newlyInvited){
        Invitation groupInvitation = new Invitation();
        groupInvitation.setTarget(group);

        for(User invited : newlyInvited){
            groupInvitation.setInvitee(invited);
            new PostGroupInviteTask(getApplicationContext()).execute(groupInvitation);
        }
    }

    /**
     * Starts the {@link dtu_se2.spotme.MapsActivity} with extra data about what method to be
     * called. In this case, the map in MapsActivity will center and zoom on the group member.
     * Starts {@link dtu_se2.spotme.MapsActivity}.
     * @param member The group member to show on the map.
     */
    public void showOnMap(User member){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("method","showUserOnMap");
        intent.putExtra("user",member);
        startActivity(intent);
    }

    /**
     * Gets the user's friend list using the {@link dtu_se2.spotme.web_connection.GetFriendsTask}.
     * @return List of users that are friends with the user.
     */
    private List<User> getFriends() {
        List<User> friendList = new ArrayList<>();
        try {
            friendList = new GetFriendsTask(getApplicationContext()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return friendList;
    }
}

