package com.syubsyubboy.howlstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.syubsyubboy.howlstagram.R
import com.syubsyubboy.howlstagram.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {

    var firestore: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Init firestore
        firestore = FirebaseFirestore.getInstance()

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUids: ArrayList<String> = arrayListOf()


        init {
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUids.clear()

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUids.add(snapshot.id)
                    }

                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int = contentDTOs.size

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val selectedDto = contentDTOs[position]

            with(holder.itemView) {
                detailviewitem_profile_textview.text = selectedDto.userId
                Glide.with(this).load(selectedDto.imageUrl).into(detailviewitem_imageview_content)
                detailviewitem_explain_textview.text = selectedDto.explain
                detailviewitem_favoritecounter_textview.text = "Likes ${selectedDto.favoriteCount}"
                Glide.with(this).load(selectedDto.imageUrl).into(detailviewitem_profile_image)
            }
        }
    }
}