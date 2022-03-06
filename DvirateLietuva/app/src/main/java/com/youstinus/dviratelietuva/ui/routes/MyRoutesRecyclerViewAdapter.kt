package com.youstinus.dviratelietuva.ui.routes

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Route

import com.youstinus.dviratelietuva.ui.routes.RoutesFragment.OnRoutesItemFragmentInteractionListener
import com.youstinus.dviratelietuva.utilities.Helper

import kotlinx.android.synthetic.main.fragment_routes_item.view.*

fun Double.format(digits: Int) = "%.${digits}f".format(this)

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnRoutesItemFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRoutesRecyclerViewAdapter(
    //private val mValues: List<Route>,
    private val mListener: OnRoutesItemFragmentInteractionListener?,
    private val response: FirestoreRecyclerOptions<Route>
    ) : FirestoreRecyclerAdapter<Route, MyRoutesRecyclerViewAdapter.ViewHolder>(response) {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Route
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onRoutesItemFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_routes_item, parent, false)
        return ViewHolder(view)
    }

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTitleView.text = item.title
        holder.mLocationView.text = item.location
        holder.mDistanceView.text = item.distance.toString() + "km"
        holder.mRoadTypeView.text = item.roadType.cuToRoadType()
        holder.mDifficultyView.text = item.difficulty.cuToDifficultyType()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Route) {
        holder.mTitleView.text = model.title
        holder.mLocationView.text = model.location
        holder.mDistanceView.text = model.distance.format(1) + "km"
        holder.mRoadTypeView.text = Helper.getRoardTypeString(model.roadType)
        holder.mDifficultyView.text = Helper.getDifficultyString(model.difficulty)

        if (model.routeImage != "") {
            loadImage(holder, model)
        }else{
//         imgImage.setImageDrawable(null);
            holder.mImageView.setImageDrawable(holder.mImageView.context.resources.getDrawable(R.drawable.ic_picture)); //todo seems fishy
        }

        with(holder.mView) {
            tag = model
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onError(e: FirebaseFirestoreException) {
        Log.e("error", e.message)
    }

    //override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.textView_route_title
        val mLocationView: TextView = mView.textView_route_location
        val mDistanceView: TextView = mView.textView_distance
        val mRoadTypeView: TextView = mView.textView_road_type
        val mDifficultyView: TextView = mView.textView_difficulty
        val mImageView: ImageView = mView.imageView_route

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    // todo FireFun function
    private fun loadImage(holder: ViewHolder, model: Route) {
        var routeType = Helper.getRouteTypeString(model.routeType)
        val ref = FirebaseStorage.getInstance().reference.child("routes/"+ routeType +"/"+model.routeStorage + "/"+model.routeImage)//getReferenceFromUrl("gs://crocheting-guide.appspot.com/images/" + scheme!!.images + "/" + imagesIndex + ".jpg")
        ref.downloadUrl.addOnSuccessListener { uri ->
            /*Picasso.get().load(uri.toString()).into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        image.setScaleType(ImageView.ScaleType.FIT_CENTER);//Or ScaleType.FIT_CENTER
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });*/
            if (uri != null && uri.toString() != "") {
                Picasso.get()./*with(holder.itemView).*/load(uri).into(holder.mImageView)
            }
        }.addOnFailureListener {//ex->
            // Handle any errors
            //println(ex)
        }
    }
}
