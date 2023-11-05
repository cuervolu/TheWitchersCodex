package com.cuervolu.witcherscodex.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.Query.Direction.ASCENDING

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val pageSize = 10

    @Provides
    @Singleton
    fun provideQueryMonstersByName(): Query {
        return FirebaseFirestore.getInstance()
            .collection("bestiary")
            .orderBy("name", ASCENDING)
            .limit(pageSize.toLong())
    }
}
