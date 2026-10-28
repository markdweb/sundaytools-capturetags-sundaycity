package com.sundaytools.app.network;

import com.sundaytools.app.repository.SettingsRepository;
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
public final class BlacklistApiService_Factory implements Factory<BlacklistApiService> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public BlacklistApiService_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public BlacklistApiService get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static BlacklistApiService_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new BlacklistApiService_Factory(settingsRepositoryProvider);
  }

  public static BlacklistApiService newInstance(SettingsRepository settingsRepository) {
    return new BlacklistApiService(settingsRepository);
  }
}
