/**
 * @author Yixin Zhu
 */
package com.comic.viewer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.comic.globals.Globals;

public class MainPage extends ListActivity {
	//list of volume names
	private String[] volumeNames = {Globals.VolFiveName, 
			Globals.VolFourName, Globals.VolThreeName, 
			Globals.VolTwoName, Globals.VolOneName, Globals.VolZeroName};
	//list of volume info
	private String[] volumeInfo = {Globals.VolFiveInfo, 
			Globals.VolFourInfo, Globals.VolThreeInfo, 
			Globals.VolTwoInfo, Globals.VolOneInfo, Globals.VolZeroInfo};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setListAdapter(new ListVolumesAdapter(this));
	}
	
	/**
	 * Launches the corresponding volume based on user selection
	 * 
	 * @param volumeNameIndex The index of the user selection
	 */
	public void launchVolume(int volumeNameIndex){
		Toast.makeText(this, "Beginning Volume- "+ (5 - volumeNameIndex), 1000).show();
		Intent i = new Intent(this, ComicViewer.class);
		switch (volumeNameIndex){
			case 5:
				i.putExtra("volumeRange", Globals.ZeroRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
			case 4:
				i.putExtra("volumeRange", Globals.OneRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
			case 3:
				i.putExtra("volumeRange", Globals.TwoRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
			case 2:
				i.putExtra("volumeRange", Globals.ThreeRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
			case 1:
				i.putExtra("volumeRange", Globals.FourRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
			case 0:
				i.putExtra("volumeRange", Globals.FiveRange);
				i.putExtra("volumeNumber", (5 - volumeNameIndex));
				break;
		}
		startActivity(i);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position,long id){
		launchVolume(position); //perform action based on user click
	}
	
	/**
	 * Custom adapter for custom list view
	 * 
	 * @author Yixin Zhu
	 */
	class ListVolumesAdapter extends BaseAdapter implements Filterable {
		private LayoutInflater mInflater;

		public ListVolumesAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.main_context,
						null);
				// Creates a ViewHolder and store references to the two children
				// views we want to bind data to.
				holder = new ViewHolder();
				holder.volumeName = (TextView) convertView
						.findViewById(R.id.volumeName);
				holder.volumeDescription = (TextView) convertView
						.findViewById(R.id.volumeDiscription);
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}
			holder.volumeName.setText(volumeNames[position]);
			holder.volumeDescription.setText(volumeInfo[position]);
			return convertView;
		}

		class ViewHolder {
			TextView volumeName;
			TextView volumeDescription;
		}

		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return volumeNames.length;
		}

		public Object getItem(int position) {
			return position;
		}

	}
}
