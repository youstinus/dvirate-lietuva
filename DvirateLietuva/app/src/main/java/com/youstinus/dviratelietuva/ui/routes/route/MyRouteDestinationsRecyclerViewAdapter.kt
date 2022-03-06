package com.youstinus.dviratelietuva.ui.routes.route

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.FirebaseFirestoreException
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Destination
import com.youstinus.dviratelietuva.models.Route

import com.youstinus.dviratelietuva.ui.routes.RoutesFragment.OnRoutesItemFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_route_destinations_item.view.*

fun Double.format(digits: Int) = "%.${digits}f".format(this)

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnRoutesItemFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRouteDestinationsRecyclerViewAdapter(
    private val googleMap: GoogleMap?,
    private val mValues: List<Destination>,
    private val mListener: RouteFragment.OnRouteDestinationsItemFragmentInteractionListener?
    ) : RecyclerView.Adapter<MyRouteDestinationsRecyclerViewAdapter.ViewHolder>(){

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Destination
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onRouteDestinationsItemFragmentInteractionListener(item, googleMap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_route_destinations_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTitleView.text = item.title

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return mValues.size
    }

    /*override fun onError(e: FirebaseFirestoreException) {
        Log.e("error", e.message)
    }*/

    //override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.textView_destination_title
        //val mLocationView: TextView = mView.textView_route_location

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
