package de.ostkontentitan.helloscala

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.{ActionBar, ActionBarActivity, ActionBarDrawerToggle}
import android.view.{LayoutInflater, Menu, MenuInflater, MenuItem, View, ViewGroup}
import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, ArrayAdapter, ListView, Toast}

object NavigationDrawerFragment {
  private val STATE_SELECTED_POSITION: String = "selected_navigation_drawer_position"
  private val PREF_USER_LEARNED_DRAWER: String = "navigation_drawer_learned"

  trait NavigationDrawerCallbacks {
    def onNavigationDrawerItemSelected(position: Int)
  }
}

class NavigationDrawerFragment extends Fragment {
  private var mCallbacks: NavigationDrawerFragment.NavigationDrawerCallbacks = null
  private var mDrawerToggle: ActionBarDrawerToggle = null
  private var mDrawerLayout: DrawerLayout = null
  private var mDrawerListView: ListView = null
  private var mFragmentContainerView: View = null
  private var mCurrentSelectedPosition: Int = 0
  private var mFromSavedInstanceState: Boolean = false
  private var mUserLearnedDrawer: Boolean = false

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity)
    mUserLearnedDrawer = sp.getBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER, false)
    if (savedInstanceState != null) {
      mCurrentSelectedPosition = savedInstanceState.getInt(NavigationDrawerFragment.STATE_SELECTED_POSITION)
      mFromSavedInstanceState = true
    }
    selectItem(mCurrentSelectedPosition)
  }

  override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    mDrawerListView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false).asInstanceOf[ListView]
    mDrawerListView.setOnItemClickListener(new OnItemClickListener {
      override def onItemClick(adapterView: AdapterView[_], view: View, i: Int, l: Long): Unit = selectItem(i)
    })
    mDrawerListView.setAdapter(new ArrayAdapter[String](getActionBar.getThemedContext, android.R.layout.simple_list_item_activated_1, android.R.id.text1, Array[String](getString(R.string.title_section1), getString(R.string.title_section2), getString(R.string.title_section3))))
    mDrawerListView.setItemChecked(mCurrentSelectedPosition, true)
    mDrawerListView
  }

  def isDrawerOpen: Boolean = {
    mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView)
  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId   The android:id of this fragment in its activity's layout.
   * @param drawerLayout The DrawerLayout containing this fragment's UI.
   */
  def setUp(fragmentId: Int, drawerLayout: DrawerLayout) {
    mFragmentContainerView = getActivity.findViewById(fragmentId)
    mDrawerLayout = drawerLayout
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
    val actionBar: ActionBar = getActionBar
    actionBar.setDisplayHomeAsUpEnabled(true)
    actionBar.setHomeButtonEnabled(true)
    mDrawerToggle = new ActionBarDrawerToggle(getActivity, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
      @Override override def onDrawerClosed(drawerView: View) {
        super.onDrawerClosed(drawerView)
        if (!isAdded) {
          return
        }
        getActivity.supportInvalidateOptionsMenu()
      }

      @Override override def onDrawerOpened(drawerView: View) {
        super.onDrawerOpened(drawerView)
        if (!isAdded) {
          return
        }
        if (!mUserLearnedDrawer) {
          mUserLearnedDrawer = true
          val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity)
          sp.edit.putBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER, true).apply()
        }
        getActivity.supportInvalidateOptionsMenu()
      }
    }
    if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
      mDrawerLayout.openDrawer(mFragmentContainerView)
    }
    mDrawerLayout.post( new Runnable {
      override def run() {
        mDrawerToggle.syncState()
      }
    })
    mDrawerLayout.setDrawerListener(mDrawerToggle)
  }

  private def selectItem(position: Int) {
    mCurrentSelectedPosition = position
    if (mDrawerListView != null) {
      mDrawerListView.setItemChecked(position, true)
    }
    if (mDrawerLayout != null) {
      mDrawerLayout.closeDrawer(mFragmentContainerView)
    }
    if (mCallbacks != null) {
      mCallbacks.onNavigationDrawerItemSelected(position)
    }
  }

  override def onAttach(activity: Activity) {
    super.onAttach(activity)
    try {
      mCallbacks = activity.asInstanceOf[NavigationDrawerFragment.NavigationDrawerCallbacks]
    }
    catch {
      case e: Exception => throw new NotImplementedError("Activity must implement NavigationDrawerCallbacks.")
    }
  }

  override def onDetach() {
    super.onDetach()
    mCallbacks = null
  }

  override def onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(NavigationDrawerFragment.STATE_SELECTED_POSITION, mCurrentSelectedPosition)
  }

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    mDrawerToggle.onConfigurationChanged(newConfig)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    if (mDrawerLayout != null && isDrawerOpen) {
      inflater.inflate(R.menu.global, menu)
      showGlobalContextActionBar()
    }
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true
    }
    if (item.getItemId == R.id.action_example) {
      Toast.makeText(getActivity, "Example action.", Toast.LENGTH_SHORT).show()
      return true
    }
    super.onOptionsItemSelected(item)
  }

  /**
   * Per the navigation drawer design guidelines, updates the action bar to show the global app
   * 'context', rather than just what's in the current screen.
   */
  private def showGlobalContextActionBar() {
    val actionBar: ActionBar = getActionBar
    actionBar.setDisplayShowTitleEnabled(true)
    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD)
    actionBar.setTitle(R.string.app_name)
  }

  private def getActionBar: ActionBar = {
    getActivity.asInstanceOf[ActionBarActivity].getSupportActionBar
  }
}
