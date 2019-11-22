package com.syubsyubboy.howlstagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.syubsyubboy.howlstagram.R
import com.syubsyubboy.howlstagram.model.ContentDTO
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
                //Load userId(email)
                detailviewitem_profile_textview.text = selectedDto.userId
                //Load content image
                Glide.with(this).load(selectedDto.imageUrl).into(detailviewitem_imageview_content)
                //Load explain
                detailviewitem_explain_textview.text = selectedDto.explain
                //Load favorite count
                detailviewitem_favoritecounter_textview.text = "Likes ${selectedDto.favoriteCount}"
                //Load profile image
                Glide.with(this).load(selectedDto.imageUrl).into(detailviewitem_profile_image)
                //On favorite click
                detailviewitem_favorite_imageview.setOnClickListener {
                    favoriteEvent(position)
                }
            }
        }

        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUids[position])

            firestore?.runTransaction { transaction ->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    //이미 눌렀을 경우
                    contentDTO.favoriteCount -= 1
                    contentDTO.favorites.remove(uid)
                } else {
                    //안 눌렀을 때
                    contentDTO.favoriteCount += 1
                    contentDTO.favorites[uid!!] = true
                }
            }
        }
    }
}