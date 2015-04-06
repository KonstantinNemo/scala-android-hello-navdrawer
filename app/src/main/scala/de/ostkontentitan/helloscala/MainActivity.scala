package de.ostkontentitan.helloscala

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.{ActionBar, ActionBarActivity}
import android.view.{LayoutInflater, Menu, MenuItem, View, ViewGroup}

object MainActivity {

  object PlaceholderFragment {
    private val ARG_SECTION_NUMBER: String = "section_number"

    def newInstance(sectionNumber: Int): MainActivity.PlaceholderFragment = {
      val fragment: MainActivity.PlaceholderFragment = new MainActivity.PlaceholderFragment
      val args: Bundle = new Bundle
      args.putInt(ARG_SECTION_NUMBER, sectionNumber)
      fragment.setArguments(args)
      fragment
    }
  }

  class PlaceholderFragment extends Fragment {
    override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
      val rootView: View = inflater.inflate(R.layout.fragment_main, container, false)
      rootView
    }

    override def onAttach(activity: Activity) {
      super.onAttach(activity)
      activity.asInstanceOf[MainActivity].onSectionAttached(getArguments.getInt(PlaceholderFragment.ARG_SECTION_NUMBER))
    }
  }

}

class MainActivity extends ActionBarActivity with NavigationDrawerFragment.NavigationDrawerCallbacks {
  private var mNavigationDrawerFragment: NavigationDrawerFragment = null
  private var mTitle: String = ""

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    mNavigationDrawerFragment = getSupportFragmentManager.findFragmentById(R.id.navigation_drawer).asInstanceOf[NavigationDrawerFragment]
    mTitle = getTitle.toString
    mNavigationDrawerFragment.setUp(R.id.navigation_drawer, findViewById(R.id.drawer_layout).asInstanceOf[DrawerLayout])
  }

  override def onNavigationDrawerItemSelected(position: Int) {
    val fragmentManager: FragmentManager = getSupportFragmentManager
    fragmentManager.beginTransaction.replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(position + 1)).commit
  }

  def onSectionAttached(number: Int) {
    number match {
      case 1 =>
        mTitle = getString(R.string.title_section1)
      case 2 =>
        mTitle = getString(R.string.title_section2)
      case 3 =>
        mTitle = getString(R.string.title_section3)
    }
  }

  def restoreActionBar() {
    val actionBar: ActionBar = getSupportActionBar
    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD)
    actionBar.setDisplayShowTitleEnabled(true)
    actionBar.setTitle(mTitle)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    if (!mNavigationDrawerFragment.isDrawerOpen) {
      getMenuInflater.inflate(R.menu.main, menu)
      restoreActionBar()
      return true
    }
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    val id: Int = item.getItemId
    if (id == R.id.action_settings) {
      return true
    }
    super.onOptionsItemSelected(item)
  }
}
