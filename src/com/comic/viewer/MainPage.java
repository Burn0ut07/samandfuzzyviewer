package com.comic.viewer;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.comic.globals.Globals;

public class MainPage extends ListActivity {
	private String[] volumeNames = {Globals.VolFiveName, 
			Globals.VolFourName, Globals.VolThreeName, 
			Globals.VolTwoName, Globals.VolOneName, Globals.VolZeroName};
	private String[] volumeInfo = {Globals.VolFiveInfo, 
			Globals.VolFourInfo, Globals.VolThreeInfo, 
			Globals.VolTwoInfo, Globals.VolOneInfo, Globals.VolZeroInfo};
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setListAdapter(new ListVolumesAdapter(this));
	}
	public void launchVolume(int volumeNameIndex){
		Intent i = new Intent(this, ComicViewer.class);
		switch (volumeNameIndex){
			case 5:
				i.putExtra("volumeRange", Globals.ZeroRange);
				break;
			case 4:
				i.putExtra("volumeRange", Globals.OneRange);
				break;
			case 3:
				i.putExtra("volumeRange", Globals.TwoRange);
				break;
			case 2:
				i.putExtra("volumeRange", Globals.ThreeRange);
				break;
			case 1:
				i.putExtra("volumeRange", Globals.FourRange);
				break;
			case 0:
				i.putExtra("volumeRange", Globals.FiveRange);
				break;
		}
		startActivity(i);
	}
	class ListVolumesAdapter extends BaseAdapter implements Filterable {
		private LayoutInflater mInflater;
		private Context context;

		public ListVolumesAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls to findViewById() on each row.
			ViewHolder holder;
			// When convertView is not null, we can reuse it directly, there is
			// no need to reinflate it. We only inflate a new View when the
			// convertView
			// supplied by ListView is null.
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

				convertView.setOnClickListener(new OnClickListener() {
					private int pos = position;

					public void onClick(View v) {
						//launch correct volume
						launchVolume(position);
					}
				});
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}
			holder.volumeName.setText(volumeNames[position]);
			holder.volumeDescription
					.setText(volumeInfo[position]);
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
			return 0;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return volumeNames.length;
		}

		public Object getItem(int position) {
			return volumeNames[position];
		}

	}
}
