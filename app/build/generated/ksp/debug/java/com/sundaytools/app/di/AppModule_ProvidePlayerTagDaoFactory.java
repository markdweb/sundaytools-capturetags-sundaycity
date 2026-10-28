package com.sundaytools.app.di;

import com.sundaytools.app.database.AppDatabase;
import com.sundaytools.app.database.dao.PlayerTagDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvidePlayerTagDaoFactory implements Factory<PlayerTagDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvidePlayerTagDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PlayerTagDao get() {
    return providePlayerTagDao(databaseProvider.get());
  }

  public static AppModule_ProvidePlayerTagDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvidePlayerTagDaoFactory(databaseProvider);
  }

  public static PlayerTagDao providePlayerTagDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePlayerTagDao(database));
  }
}
