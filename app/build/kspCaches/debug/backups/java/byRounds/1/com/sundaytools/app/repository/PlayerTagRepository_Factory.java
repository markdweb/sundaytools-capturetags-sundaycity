package com.sundaytools.app.repository;

import com.sundaytools.app.database.dao.PlayerTagDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PlayerTagRepository_Factory implements Factory<PlayerTagRepository> {
  private final Provider<PlayerTagDao> daoProvider;

  public PlayerTagRepository_Factory(Provider<PlayerTagDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public PlayerTagRepository get() {
    return newInstance(daoProvider.get());
  }

  public static PlayerTagRepository_Factory create(Provider<PlayerTagDao> daoProvider) {
    return new PlayerTagRepository_Factory(daoProvider);
  }

  public static PlayerTagRepository newInstance(PlayerTagDao dao) {
    return new PlayerTagRepository(dao);
  }
}
