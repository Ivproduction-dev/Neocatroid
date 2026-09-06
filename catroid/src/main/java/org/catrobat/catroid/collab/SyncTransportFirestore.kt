package org.catrobat.catroid.collab

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration

class SyncTransportFirestore : SyncTransport {
    private val tag = "SyncTransport"

    private fun root(sid: String) = CollabFirebase.firestore()
        ?.collection(CollabSession.ROOT)?.document(sid)

    private fun putChunks(
        chunksRef: com.google.firebase.firestore.CollectionReference,
        chunks: List<SyncChunk>,
        callback: (Boolean) -> Unit
    ) {
        try {
            val batch = chunksRef.firestore.batch()
            for (chunk in chunks) {
                batch.set(chunksRef.document(chunk.docId()), chunk.toMap())
            }
            batch.commit()
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { e ->
                    Log.w(tag, "chunks failed", e)
                    callback(false)
                }
        } catch (e: Exception) {
            Log.w(tag, "chunks failed", e)
            callback(false)
        }
    }

    private fun putPayload(
        sid: String,
        collection: String,
        id: String,
        payload: SyncPayload,
        chunks: List<SyncChunk>,
        callback: (Boolean) -> Unit
    ) {
        try {
            val parent = root(sid)?.collection(collection) ?: run {
                callback(false)
                return
            }
            parent.document(id).set(payload.toMap())
                .addOnSuccessListener {
                    putChunks(parent.document(id).collection("chunks"), chunks, callback)
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "payload failed", e)
                    callback(false)
                }
        } catch (e: Exception) {
            Log.w(tag, "payload failed", e)
            callback(false)
        }
    }

    private fun dropPayload(sid: String, collection: String, id: String, callback: (Boolean) -> Unit) {
        try {
            val parent = root(sid)?.collection(collection) ?: run {
                callback(false)
                return
            }
            val doc = parent.document(id)
            doc.collection("chunks").get()
                .addOnSuccessListener { snap ->
                    try {
                        val batch = doc.firestore.batch()
                        for (child in snap.documents) batch.delete(child.reference)
                        batch.delete(doc)
                        batch.commit()
                            .addOnSuccessListener { callback(true) }
                            .addOnFailureListener { callback(false) }
                    } catch (e: Exception) {
                        callback(false)
                    }
                }
                .addOnFailureListener { callback(false) }
        } catch (e: Exception) {
            Log.w(tag, "drop failed", e)
            callback(false)
        }
    }

    override fun uploadPatch(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit) {
        putPayload(sid, "patches", id, payload, chunks, callback)
    }

    override fun listenPatches(sid: String, callback: (String, SyncPayload) -> Unit): Any? {
        return try {
            root(sid)?.collection("patches")?.addSnapshotListener { snap, error ->
                if (error != null || snap == null) return@addSnapshotListener
                for (doc in snap.documents) {
                    SyncPayload.fromMap(doc.data)?.let { callback(doc.id, it) }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "listen patches failed", e)
            null
        }
    }

    override fun fetchChunks(sid: String, collection: String, id: String, callback: (List<SyncChunk>) -> Unit) {
        try {
            val ref = root(sid)?.collection(collection)?.document(id)?.collection("chunks")
            if (ref == null) {
                callback(emptyList())
                return
            }
            ref.get()
                .addOnSuccessListener { snap ->
                    callback(snap.documents.mapNotNull { SyncChunk.fromMap(it.data) })
                }
                .addOnFailureListener { callback(emptyList()) }
        } catch (e: Exception) {
            Log.w(tag, "fetch chunks failed", e)
            callback(emptyList())
        }
    }

    override fun fetchChunksPaged(
        sid: String,
        collection: String,
        id: String,
        pageSize: Int,
        startAfterId: String?,
        callback: (List<SyncChunk>) -> Unit
    ) {
        try {
            var query: com.google.firebase.firestore.Query = root(sid)
                ?.collection(collection)?.document(id)?.collection("chunks")
                ?.orderBy(com.google.firebase.firestore.FieldPath.documentId())
                ?: run {
                    callback(emptyList())
                    return
                }
            if (startAfterId != null) query = query.startAfter(startAfterId)
            query.limit(pageSize.toLong()).get()
                .addOnSuccessListener { snap ->
                    callback(snap.documents.mapNotNull { SyncChunk.fromMap(it.data) })
                }
                .addOnFailureListener { callback(emptyList()) }
        } catch (e: Exception) {
            Log.w(tag, "fetch page failed", e)
            callback(emptyList())
        }
    }

    override fun putMeta(sid: String, collection: String, id: String, payload: SyncPayload, callback: (Boolean) -> Unit) {
        try {
            val doc = root(sid)?.collection(collection)?.document(id)
            if (doc == null) {
                callback(false)
                return
            }
            doc.set(payload.toMap())
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { e ->
                    Log.w(tag, "put meta failed", e)
                    callback(false)
                }
        } catch (e: Exception) {
            Log.w(tag, "put meta failed", e)
            callback(false)
        }
    }

    override fun putChunkBatch(
        sid: String,
        collection: String,
        id: String,
        chunks: List<SyncChunk>,
        callback: (Boolean) -> Unit
    ) {
        val chunksRef = root(sid)?.collection(collection)?.document(id)?.collection("chunks")
        if (chunksRef == null) {
            callback(false)
            return
        }
        putChunks(chunksRef, chunks, callback)
    }

    override fun deletePatch(sid: String, id: String, callback: (Boolean) -> Unit) {
        dropPayload(sid, "patches", id, callback)
    }

    override fun publishState(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit) {
        putPayload(sid, "states", id, payload, chunks, callback)
    }

    override fun listenStates(sid: String, callback: (String, SyncPayload) -> Unit): Any? {
        return try {
            root(sid)?.collection("states")?.addSnapshotListener { snap, error ->
                if (error != null || snap == null) return@addSnapshotListener
                for (doc in snap.documents) {
                    SyncPayload.fromMap(doc.data)?.let { callback(doc.id, it) }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "listen states failed", e)
            null
        }
    }

    override fun deleteState(sid: String, id: String, callback: (Boolean) -> Unit) {
        dropPayload(sid, "states", id, callback)
    }

    override fun unlisten(handle: Any?) {
        try {
            (handle as? ListenerRegistration)?.remove()
        } catch (e: Exception) {
            Log.w(tag, "unlisten failed", e)
        }
    }

    override fun requestSnapshot(sid: String, request: SnapshotRequest, callback: (Boolean) -> Unit) {
        try {
            val ref = root(sid)?.collection("snapshotRequests")?.document(request.uid)
            if (ref == null) {
                callback(false)
                return
            }
            ref.set(request.toMap())
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        } catch (e: Exception) {
            Log.w(tag, "snapshot request failed", e)
            callback(false)
        }
    }

    override fun listenSnapshotRequests(sid: String, callback: (SnapshotRequest) -> Unit): Any? {
        return try {
            root(sid)?.collection("snapshotRequests")?.addSnapshotListener { snap, error ->
                if (error != null || snap == null) return@addSnapshotListener
                for (doc in snap.documents) {
                    SnapshotRequest.fromMap(doc.data)?.let { callback(it) }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "listen snapshot requests failed", e)
            null
        }
    }

    override fun deleteSnapshotRequest(sid: String, uid: String, callback: (Boolean) -> Unit) {
        try {
            val ref = root(sid)?.collection("snapshotRequests")?.document(uid)
            if (ref == null) {
                callback(false)
                return
            }
            ref.delete()
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        } catch (e: Exception) {
            Log.w(tag, "delete snapshot request failed", e)
            callback(false)
        }
    }

    override fun memberRole(sid: String, uid: String, callback: (String?) -> Unit) {
        try {
            val ref = root(sid)?.collection("members")?.document(uid)
            if (ref == null) {
                callback(null)
                return
            }
            ref.get()
                .addOnSuccessListener { snap ->
                    callback(CollabMember.fromMap(snap?.data).role.takeIf { snap != null && snap.exists() })
                }
                .addOnFailureListener { callback(null) }
        } catch (e: Exception) {
            Log.w(tag, "role failed", e)
            callback(null)
        }
    }
}
