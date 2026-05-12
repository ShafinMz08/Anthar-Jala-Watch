package com.example.antharjalawatch.data.repository

import com.example.antharjalawatch.data.model.BoreholeEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * FirestoreRepository — single source of truth for borewell data.
 */
class FirestoreRepository {

    private val db         = FirebaseFirestore.getInstance()
    private val collection = "borehole_entries"

    suspend fun saveEntry(entry: BoreholeEntry): Result<String> = try {
        val status = entry.computeStatus()
        val data = mapOf(
            "depth"       to entry.depth,
            "yield"       to entry.yield,
            "yearOfDig"   to entry.yearOfDig,
            "latitude"    to entry.latitude,
            "longitude"   to entry.longitude,
            "district"    to entry.district,
            "waterStatus" to status,
            "timestamp"   to entry.timestamp
        )
        val ref = db.collection(collection).add(data).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getEntries(): Result<List<BoreholeEntry>> = try {
        val snapshot = db.collection(collection)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()

        val list = snapshot.documents.mapNotNull { doc ->
            runCatching {
                BoreholeEntry(
                    id          = doc.id,
                    depth       = doc.getString("depth")       ?: "",
                    yield       = doc.getString("yield")       ?: "",
                    yearOfDig   = doc.getString("yearOfDig")   ?: "",
                    latitude    = doc.getDouble("latitude")    ?: 0.0,
                    longitude   = doc.getDouble("longitude")   ?: 0.0,
                    district    = doc.getString("district")    ?: "",
                    waterStatus = doc.getString("waterStatus") ?: "moderate",
                    timestamp   = doc.getLong("timestamp")     ?: 0L
                )
            }.getOrNull()
        }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getEntriesByDistrict(district: String): Result<List<BoreholeEntry>> = try {
        val snapshot = db.collection(collection)
            .whereEqualTo("district", district)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()

        val list = snapshot.documents.mapNotNull { doc ->
            runCatching {
                BoreholeEntry(
                    id          = doc.id,
                    depth       = doc.getString("depth")       ?: "",
                    yield       = doc.getString("yield")       ?: "",
                    yearOfDig   = doc.getString("yearOfDig")   ?: "",
                    latitude    = doc.getDouble("latitude")    ?: 0.0,
                    longitude   = doc.getDouble("longitude")   ?: 0.0,
                    district    = doc.getString("district")    ?: "",
                    waterStatus = doc.getString("waterStatus") ?: "moderate",
                    timestamp   = doc.getLong("timestamp")     ?: 0L
                )
            }.getOrNull()
        }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
