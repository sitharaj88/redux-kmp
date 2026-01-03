package `in`.sitharaj.reduxkmp.toolkit

import `in`.sitharaj.reduxkmp.core.Action
import `in`.sitharaj.reduxkmp.core.State

/**
 * Entity Adapter - Normalized state management for Redux KMP.
 * 
 * Provides a set of reducer functions and selectors for managing 
 * normalized entity collections in Redux.
 * 
 * ## Usage
 * 
 * ```kotlin
 * // 1. Create the adapter
 * val usersAdapter = createEntityAdapter<User> { it.id }
 * 
 * // 2. Use EntityState in your state
 * data class UsersState(
 *     val users: EntityState<User> = usersAdapter.getInitialState(),
 *     val loading: Boolean = false
 * ) : State
 * 
 * // 3. Use in reducer
 * val usersReducer = reducer<UsersState> {
 *     on<UsersAction.AddUser> { state, action ->
 *         state.copy(users = usersAdapter.addOne(state.users, action.user))
 *     }
 *     on<UsersAction.AddUsers> { state, action ->
 *         state.copy(users = usersAdapter.addMany(state.users, action.users))
 *     }
 *     on<UsersAction.UpdateUser> { state, action ->
 *         state.copy(users = usersAdapter.updateOne(state.users, action.id) { it.copy(name = action.name) })
 *     }
 *     on<UsersAction.RemoveUser> { state, action ->
 *         state.copy(users = usersAdapter.removeOne(state.users, action.id))
 *     }
 * }
 * 
 * // 4. Use selectors
 * val allUsers = usersAdapter.selectAll(state.users)
 * val userById = usersAdapter.selectById(state.users, "123")
 * ```
 */

// ============================================
// Entity State
// ============================================

/**
 * Normalized entity state containing a list of IDs and a map of entities by ID.
 */
public data class EntityState<T>(
    /** Ordered list of entity IDs */
    val ids: List<String> = emptyList(),
    /** Map of entities by their ID */
    val entities: Map<String, T> = emptyMap()
) {
    public companion object {
        public fun <T> empty(): EntityState<T> = EntityState()
    }
}

// ============================================
// Entity Adapter
// ============================================

/**
 * An adapter for managing normalized entity state.
 */
public class EntityAdapter<T>(
    /** Function to extract the ID from an entity */
    private val selectId: (T) -> String,
    /** Optional function to sort entities */
    private val sortComparer: Comparator<T>? = null
) {
    
    // =========================================
    // State Initialization
    // =========================================
    
    /**
     * Returns an empty EntityState.
     */
    public fun getInitialState(): EntityState<T> = EntityState.empty()
    
    /**
     * Returns an EntityState with initial entities.
     */
    public fun getInitialState(entities: List<T>): EntityState<T> {
        return addMany(EntityState.empty(), entities)
    }
    
    // =========================================
    // CRUD Operations
    // =========================================
    
    /**
     * Adds a single entity to the state.
     * If the entity already exists (same ID), it will not be added.
     */
    public fun addOne(state: EntityState<T>, entity: T): EntityState<T> {
        val id = selectId(entity)
        if (state.entities.containsKey(id)) {
            return state // Already exists
        }
        
        val newEntities = state.entities + (id to entity)
        val newIds = if (sortComparer != null) {
            sortIds(newEntities.values.toList())
        } else {
            state.ids + id
        }
        
        return EntityState(ids = newIds, entities = newEntities)
    }
    
    /**
     * Adds multiple entities to the state.
     * Existing entities with the same ID will not be replaced.
     */
    public fun addMany(state: EntityState<T>, entities: List<T>): EntityState<T> {
        val newEntities = state.entities.toMutableMap()
        var added = false
        
        for (entity in entities) {
            val id = selectId(entity)
            if (!newEntities.containsKey(id)) {
                newEntities[id] = entity
                added = true
            }
        }
        
        if (!added) return state
        
        val newIds = if (sortComparer != null) {
            sortIds(newEntities.values.toList())
        } else {
            val existingIds = state.ids.toSet()
            state.ids + entities.map(selectId).filter { it !in existingIds }
        }
        
        return EntityState(ids = newIds, entities = newEntities)
    }
    
    /**
     * Sets all entities, replacing any existing entities.
     */
    public fun setAll(state: EntityState<T>, entities: List<T>): EntityState<T> {
        val newEntities = entities.associateBy(selectId)
        val newIds = if (sortComparer != null) {
            sortIds(entities)
        } else {
            entities.map(selectId)
        }
        
        return EntityState(ids = newIds, entities = newEntities)
    }
    
    /**
     * Updates a single entity by ID.
     * If the entity doesn't exist, nothing happens.
     */
    public fun updateOne(
        state: EntityState<T>, 
        id: String, 
        changes: (T) -> T
    ): EntityState<T> {
        val existing = state.entities[id] ?: return state
        val updated = changes(existing)
        val newId = selectId(updated)
        
        // Handle ID change
        if (newId != id) {
            val newEntities = (state.entities - id) + (newId to updated)
            val newIds = if (sortComparer != null) {
                sortIds(newEntities.values.toList())
            } else {
                state.ids.map { if (it == id) newId else it }
            }
            return EntityState(ids = newIds, entities = newEntities)
        }
        
        val newEntities = state.entities + (id to updated)
        val newIds = if (sortComparer != null) {
            sortIds(newEntities.values.toList())
        } else {
            state.ids
        }
        
        return EntityState(ids = newIds, entities = newEntities)
    }
    
    /**
     * Updates multiple entities.
     */
    public fun updateMany(
        state: EntityState<T>,
        updates: List<Pair<String, (T) -> T>>
    ): EntityState<T> {
        var result = state
        for ((id, changes) in updates) {
            result = updateOne(result, id, changes)
        }
        return result
    }
    
    /**
     * Adds or updates a single entity.
     * If the entity exists, it will be replaced.
     */
    public fun upsertOne(state: EntityState<T>, entity: T): EntityState<T> {
        val id = selectId(entity)
        val newEntities = state.entities + (id to entity)
        
        val newIds = if (state.entities.containsKey(id)) {
            if (sortComparer != null) {
                sortIds(newEntities.values.toList())
            } else {
                state.ids
            }
        } else {
            if (sortComparer != null) {
                sortIds(newEntities.values.toList())
            } else {
                state.ids + id
            }
        }
        
        return EntityState(ids = newIds, entities = newEntities)
    }
    
    /**
     * Adds or updates multiple entities.
     */
    public fun upsertMany(state: EntityState<T>, entities: List<T>): EntityState<T> {
        var result = state
        for (entity in entities) {
            result = upsertOne(result, entity)
        }
        return result
    }
    
    /**
     * Removes a single entity by ID.
     */
    public fun removeOne(state: EntityState<T>, id: String): EntityState<T> {
        if (!state.entities.containsKey(id)) {
            return state
        }
        
        return EntityState(
            ids = state.ids.filter { it != id },
            entities = state.entities - id
        )
    }
    
    /**
     * Removes multiple entities by ID.
     */
    public fun removeMany(state: EntityState<T>, ids: List<String>): EntityState<T> {
        val idsToRemove = ids.toSet()
        return EntityState(
            ids = state.ids.filter { it !in idsToRemove },
            entities = state.entities.filterKeys { it !in idsToRemove }
        )
    }
    
    /**
     * Removes all entities.
     */
    public fun removeAll(state: EntityState<T>): EntityState<T> {
        return EntityState.empty()
    }
    
    // =========================================
    // Selectors
    // =========================================
    
    /**
     * Selects all entities as a list (in order).
     */
    public fun selectAll(state: EntityState<T>): List<T> {
        return state.ids.mapNotNull { state.entities[it] }
    }
    
    /**
     * Selects a single entity by ID.
     */
    public fun selectById(state: EntityState<T>, id: String): T? {
        return state.entities[id]
    }
    
    /**
     * Selects all entity IDs.
     */
    public fun selectIds(state: EntityState<T>): List<String> {
        return state.ids
    }
    
    /**
     * Selects the entities map.
     */
    public fun selectEntities(state: EntityState<T>): Map<String, T> {
        return state.entities
    }
    
    /**
     * Selects the total number of entities.
     */
    public fun selectTotal(state: EntityState<T>): Int {
        return state.ids.size
    }
    
    // =========================================
    // Helpers
    // =========================================
    
    private fun sortIds(entities: List<T>): List<String> {
        return if (sortComparer != null) {
            entities.sortedWith(sortComparer).map(selectId)
        } else {
            entities.map(selectId)
        }
    }
}

// ============================================
// Factory Function
// ============================================

/**
 * Creates an EntityAdapter for managing normalized entity state.
 * 
 * @param selectId Function to extract the unique ID from an entity
 * @param sortComparer Optional comparator for sorting entities
 */
public fun <T> createEntityAdapter(
    sortComparer: Comparator<T>? = null,
    selectId: (T) -> String
): EntityAdapter<T> {
    return EntityAdapter(
        selectId = selectId,
        sortComparer = sortComparer
    )
}

/**
 * Creates an EntityAdapter with sorting.
 */
public fun <T : Comparable<T>> createEntityAdapter(
    selectId: (T) -> String,
    sorted: Boolean = false
): EntityAdapter<T> {
    return EntityAdapter(
        selectId = selectId,
        sortComparer = if (sorted) compareBy { it } else null
    )
}
