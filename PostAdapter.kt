package com.example.e_comm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private var posts: List<Post>,
    private val onLikeClick: (Post) -> Unit,
    private val onCommentSubmit: (Post, String) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAuthor: TextView = itemView.findViewById(R.id.tvPostAuthor)
        val tvContent: TextView = itemView.findViewById(R.id.tvPostContent)
        val btnLike: ImageButton = itemView.findViewById(R.id.btnLike)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        val etAddComment: EditText = itemView.findViewById(R.id.etAddComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvAuthor.text = post.authorName
        holder.tvContent.text = post.content
        holder.tvLikeCount.text = post.likeCount.toString()
        holder.tvCommentCount.text = "${post.commentCount} comments"

        holder.btnLike.setOnClickListener { onLikeClick(post) }

        holder.etAddComment.setOnEditorActionListener { textView, _, _ ->
            val text = textView.text.toString().trim()
            if (text.isNotEmpty()) {
                onCommentSubmit(post, text)
                textView.text = ""
            }
            true
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
