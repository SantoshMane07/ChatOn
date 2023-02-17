package com.example.chaton.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chaton.R
import com.example.chaton.models.Messeage
import com.google.firebase.auth.FirebaseAuth

class MesseageAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var MssgArray:ArrayList<Messeage> = ArrayList()
    val ITEM_RECV = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            //inflate item recv layout
            val viewHolder = receiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat_row,parent,false))
            return viewHolder
        }
        else{
            //inflate item sent layout
            val viewHolder = sentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_chat_row,parent,false))
            return viewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currMssg = MssgArray[position]

        if(holder.javaClass==sentViewHolder::class.java){
            //Sent ka Kaam karo
            val viewHolder = holder as sentViewHolder
            viewHolder.sentMssg.text = currMssg.messeage.toString()
        }
        else{
            //recv ka kaam karo
            val viewHolder = holder as receiveViewHolder
            viewHolder.recvMssg.text = currMssg.messeage.toString()
        }
    }

    override fun getItemCount(): Int {
        return MssgArray.size
    }

    override fun getItemViewType(position: Int): Int {
        val currMssg = MssgArray[position]
        if(currMssg.senderID.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECV
        }
    }
    fun messgListChanged(newMssgArray:ArrayList<Messeage>){
        MssgArray.clear()
        MssgArray.addAll(newMssgArray)
        notifyDataSetChanged()
    }
    //
    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMssg = itemView.findViewById<TextView>(R.id.tv_sender_mssg)
    }
    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recvMssg = itemView.findViewById<TextView>(R.id.tv_receiver_mssg)
    }
}