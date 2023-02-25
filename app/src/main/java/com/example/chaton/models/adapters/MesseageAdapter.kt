package com.example.chaton.models.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chaton.R
import com.example.chaton.models.Messeage
import com.google.firebase.auth.FirebaseAuth

//class MesseageAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    var MssgArray:ArrayList<Messeage> = ArrayList()
//    val ITEM_RECV = 1
//    val ITEM_SENT = 2
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        if (viewType==1){
//            //inflate item recv layout
//            val viewHolder = receiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat_row,parent,false))
//            return viewHolder
//        }
//        else{
//            //inflate item sent layout
//            val viewHolder = sentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_chat_row,parent,false))
//            return viewHolder
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val currMssg = MssgArray[position]
//
//        if(holder.javaClass==sentViewHolder::class.java){
//            //Sent ka Kaam karo
//            val viewHolder = holder as sentViewHolder
//            viewHolder.sentMssg.text = currMssg.messeage.toString()
//        }
//        else{
//            //recv ka kaam karo
//            val viewHolder = holder as receiveViewHolder
//            viewHolder.recvMssg.text = currMssg.messeage.toString()
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return MssgArray.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val currMssg = MssgArray[position]
//        if(currMssg.senderID.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
//            return ITEM_SENT
//        }
//        else{
//            return ITEM_RECV
//        }
//    }
//    fun messgListChanged(newMssgArray:ArrayList<Messeage>){
//        MssgArray.clear()
//        MssgArray.addAll(newMssgArray)
//        notifyDataSetChanged()
//    }
//    //
//    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val sentMssg = itemView.findViewById<TextView>(R.id.tv_sender_mssg)
//    }
//    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val recvMssg = itemView.findViewById<TextView>(R.id.tv_receiver_mssg)
//    }
//}

/////////////..................\\\\\\\\\\\\\\\\\
class MessageAdapter : ListAdapter<Messeage, RecyclerView.ViewHolder>(MessageItemCallback()) {

    var oldList: List<Messeage> = emptyList()
    val ITEM_RECV = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_RECV) {
            //inflate item recv layout
            return receiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat_row, parent, false))
        } else {
            //inflate item sent layout
            return sentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_chat_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currMssg = getItem(position)

        if (holder.itemViewType == ITEM_SENT) {
            //Sent ka Kaam karo
            val viewHolder = holder as sentViewHolder
            viewHolder.sentMssg.text = currMssg.messeage
        } else {
            //recv ka kaam karo
            val viewHolder = holder as receiveViewHolder
            viewHolder.recvMssg.text = currMssg.messeage
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currMssg = getItem(position)
        if (currMssg.senderID == FirebaseAuth.getInstance().currentUser!!.uid) {
            return ITEM_SENT
        } else {
            return ITEM_RECV
        }
    }

    fun messgListChanged(newMssgArray: List<Messeage>) {
        val diffResult = DiffUtil.calculateDiff(MessageDiffCallback(oldList, newMssgArray))
        oldList = newMssgArray
        submitList(newMssgArray)
        diffResult.dispatchUpdatesTo(this)
    }

    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMssg = itemView.findViewById<TextView>(R.id.tv_sender_mssg)
    }

    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recvMssg = itemView.findViewById<TextView>(R.id.tv_receiver_mssg)
    }

    class MessageItemCallback : DiffUtil.ItemCallback<Messeage>() {
        override fun areItemsTheSame(oldItem: Messeage, newItem: Messeage): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Messeage, newItem: Messeage): Boolean {
            return oldItem.messeage == newItem.messeage
        }
    }

    class MessageDiffCallback(private val oldList: List<Messeage>, private val newList: List<Messeage>): DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.messeage == newItem.messeage
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            val diffBundle = Bundle()
            if (oldItem.messeage != newItem.messeage) {
                diffBundle.putString("messeage", newItem.messeage)
            }
            return if (diffBundle.size() == 0) null else diffBundle
        }
    }
}

