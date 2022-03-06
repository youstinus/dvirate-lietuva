package com.youstinus.dviratelietuva.ui.routes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.youstinus.dviratelietuva.R
import com.youstinus.dviratelietuva.models.Route
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.youstinus.dviratelietuva.utilities.FireFun


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RoutesFragment.OnRoutesItemFragmentInteractionListener] interface.
 */
class RoutesFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnRoutesItemFragmentInteractionListener? = null
    private lateinit var globalAdapter: MyRoutesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        val query = FireFun.getRoutes()
        val response = FirestoreRecyclerOptions.Builder<Route>()
            .setQuery(query, Route::class.java)
            .build()

        globalAdapter = MyRoutesRecyclerViewAdapter(
            //mutableListOf(Route(0, "Wow", "nise"), Route(0, "Wosdasdasw", "niseasd"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise"),Route(0, "Wow", "nise")),
            listener,
            response
        )
        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_routes)
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = globalAdapter
            }
            recyclerView.adapter?.notifyDataSetChanged();
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRoutesItemFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnRoutesItemFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onStart() {
        super.onStart()
        globalAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        globalAdapter.stopListening()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnRoutesItemFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onRoutesItemFragmentInteraction(item: Route?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RoutesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
