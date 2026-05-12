package com.example.antharjalawatch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antharjalawatch.data.model.BoreholeEntry
import com.example.antharjalawatch.data.model.DemoData
import com.example.antharjalawatch.data.model.District
import com.example.antharjalawatch.data.repository.FirestoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── SubmitState — TOP-LEVEL sealed class ──────────────────────────────────────
// IMPORTANT: Must be top-level (not nested inside ViewModel).
// Nesting a sealed class inside a ViewModel and using it with `by mutableStateOf`
// causes Kotlin to fail inferring the correct getValue/setValue operator overload,
// producing "Property delegate must have a getValue/setValue" compile error.
sealed class SubmitState {
    object Idle    : SubmitState()
    object Loading : SubmitState()
    data class Success(val id: String)     : SubmitState()
    data class Error(val message: String)  : SubmitState()
}

// ── AppViewModel ──────────────────────────────────────────────────────────────
/**
 * Single shared ViewModel for the entire app.
 *
 * Accessed in:
 *   MainActivity → NavGraph → every screen
 *
 * Compose state properties use `by mutableStateOf(...)` with explicit types
 * so the Kotlin compiler never has to infer delegate operators from a generic
 * or nullable type — the most common source of delegate compile errors.
 */
class AppViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    // ── Dark Mode ─────────────────────────────────────────────────────────
    // Explicit type annotation prevents ambiguous overload resolution
    var darkMode: Boolean by mutableStateOf(false)
        private set

    fun toggleDarkMode() {
        darkMode = !darkMode
    }

    // ── District Selection ────────────────────────────────────────────────
    // Nullable type must be annotated explicitly — mutableStateOf<District?>(null)
    // ensures compiler picks the correct MutableState<District?> overload
    var selectedDistrict: District? by mutableStateOf<District?>(null)
        private set

    val districts: List<District> = DemoData.districts

    fun selectDistrict(district: District?) {
        selectedDistrict = district
        loadFirestoreEntries()
    }

    // ── Borewell Display Data ─────────────────────────────────────────────
    // Explicit List<BoreholeEntry> type prevents wildcard inference issues
    var displayEntries: List<BoreholeEntry> by mutableStateOf<List<BoreholeEntry>>(DemoData.borewells)
        private set

    var firestoreEntries: List<BoreholeEntry> by mutableStateOf<List<BoreholeEntry>>(emptyList())
        private set

    var isLoadingEntries: Boolean by mutableStateOf(false)
        private set

    // Nullable String — explicit type required for delegate to compile
    var entriesError: String? by mutableStateOf<String?>(null)
        private set

    // ── Filtered entries (derived, not stored as state) ───────────────────
    // Using a computed property (not mutableStateOf) avoids double-state sync bugs.
    // Compose will recompose any screen reading this whenever displayEntries
    // or selectedDistrict change, because those ARE state.
    val filteredEntries: List<BoreholeEntry>
        get() {
            val districtName = selectedDistrict?.name ?: return displayEntries
            return displayEntries.filter { entry: BoreholeEntry ->
                entry.district == districtName
            }
        }

    // ── Submission State ──────────────────────────────────────────────────
    // Top-level SubmitState (not nested) — resolves delegate operator ambiguity
    var submitState: SubmitState by mutableStateOf<SubmitState>(SubmitState.Idle)
        private set

    fun resetSubmitState() {
        submitState = SubmitState.Idle
    }

    // ── Init ──────────────────────────────────────────────────────────────
    init {
        loadFirestoreEntries()
    }

    // ── Data Loading ──────────────────────────────────────────────────────
    fun loadFirestoreEntries() {
        viewModelScope.launch {
            isLoadingEntries = true
            entriesError     = null
            try {
                val districtName = selectedDistrict?.name
                val result = if (districtName != null) {
                    repository.getEntriesByDistrict(districtName)
                } else {
                    repository.getEntries()
                }
                result.fold(
                    onSuccess = { entries: List<BoreholeEntry> ->
                        firestoreEntries = entries
                        val combined = DemoData.borewells.toMutableList()
                        combined.addAll(0, entries)
                        displayEntries = if (districtName != null) {
                            combined.filter { entry: BoreholeEntry ->
                                entry.district == districtName
                            }
                        } else {
                            combined
                        }
                    },
                    onFailure = { error: Throwable ->
                        entriesError   = error.message
                        displayEntries = DemoData.borewells
                    }
                )
            } finally {
                isLoadingEntries = false
            }
        }
    }

    // ── Entry Submission ──────────────────────────────────────────────────
    fun submitEntry(entry: BoreholeEntry) {
        viewModelScope.launch {
            submitState = SubmitState.Loading
            delay(300L)
            val result = repository.saveEntry(entry)
            submitState = result.fold(
                onSuccess = { id: String ->
                    val updated = displayEntries.toMutableList()
                    val saved   = entry.copy(
                        id          = id,
                        waterStatus = entry.computeStatus()
                    )
                    updated.add(0, saved)
                    displayEntries = updated
                    SubmitState.Success(id)
                },
                onFailure = { error: Throwable ->
                    SubmitState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}
