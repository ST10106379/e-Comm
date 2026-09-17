package com.example.e_comm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_comm.databinding.ActivitySocialsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SocialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySocialsBinding
    private lateinit var adapter: PostAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var postsCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!ECommApp.isFirebaseAvailable(this)) {
            Toast.makeText(this, "Social features are unavailable. Missing google-services.json.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            binding = ActivitySocialsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            db = FirebaseFirestore.getInstance()
            auth = FirebaseAuth.getInstance()
            postsCollection = db.collection("posts")
        } catch (t: Throwable) {
            Log.e("SocialsActivity", "Firebase initialization failed", t)
            Toast.makeText(this, "Social features failed to load.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupRecyclerView()
        listenForPosts()

        binding.btnPost.setOnClickListener { submitPost() }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(
            posts = emptyList(),
            onLikeClick = { post -> likePost(post) },
            onCommentSubmit = { post, text -> addComment(post, text) }
        )
        binding.rvFeed.layoutManager = LinearLayoutManager(this)
        binding.rvFeed.adapter = adapter
    }

    private fun listenForPosts() {
        postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.apply { id = doc.id }
                }
                adapter.updatePosts(posts)
            }
    }

    private fun submitPost() {
        val content = binding.etNewPost.text.toString().trim()
        if (content.isEmpty()) return

        val user = auth.currentUser
        val post = Post(
            authorName = user?.displayName ?: "Anonymous",
            authorUid = user?.uid ?: "",
            content = content
        )

        postsCollection.add(post)
            .addOnSuccessListener { binding.etNewPost.text?.clear() }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to post: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun likePost(post: Post) {
        postsCollection.document(post.id)
            .update("likeCount", post.likeCount + 1)
    }

    private fun addComment(post: Post, text: String) {
        val user = auth.currentUser
        val comment = mapOf(
            "authorName" to (user?.displayName ?: "Anonymous"),
            "authorUid" to (user?.uid ?: ""),
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        postsCollection.document(post.id)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                postsCollection.document(post.id)
                    .update("commentCount", post.commentCount + 1)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to comment: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
