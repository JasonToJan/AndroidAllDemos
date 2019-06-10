package jan.jason.easyanimationsdemo;


import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

/**
 * An activity representing a list of Animations. This activity has different
 * presentations for phones and tablets. On phones, the activity presents a list
 * of items, which when touched, lead to a {@link AnimationDetailActivity}
 * representing item details. On tablets, the activity presents the list of
 * items and item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is
 * {@link AnimationListFragment} and the item details (if present) is
 * {@link AnimationDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link AnimationListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class AnimationListActivity extends Activity implements
		AnimationListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation_list);

		if (findViewById(R.id.animation_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((AnimationListFragment) getFragmentManager().findFragmentById(
					R.id.animation_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link AnimationListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(AnimationDetailFragment.ARG_ITEM_ID, id);
			AnimationDetailFragment fragment = new AnimationDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.animation_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					AnimationDetailActivity.class);
			detailIntent.putExtra(AnimationDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
