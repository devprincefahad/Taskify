package dev.prince.taskify.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.prince.taskify.database.TaskifyDatabase
import dev.prince.taskify.sync.FirestoreService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomInstance(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        TaskifyDatabase::class.java,
        "taskify_database"
    ).build()

    @Singleton
    @Provides
    fun provideTaskDao(db: TaskifyDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreService(firestore: FirebaseFirestore, context: Context): FirestoreService {
        return FirestoreService(firestore, context)
    }
}