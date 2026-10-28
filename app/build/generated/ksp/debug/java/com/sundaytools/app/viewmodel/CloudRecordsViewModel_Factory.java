package com.sundaytools.app.viewmodel;

import com.sundaytools.app.network.BlacklistApiService;
import com.sundaytools.app.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CloudRecordsViewModel_Factory implements Factory<CloudRecordsViewModel> {
  private final Provider<BlacklistApiService> apiProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public CloudRecordsViewModel_Factory(Provider<BlacklistApiService> apiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.apiProvider = apiProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public CloudRecordsViewModel get() {
    return newInstance(apiProvider.get(), settingsRepositoryProvider.get());
  }

  public static CloudRecordsViewModel_Factory create(Provider<BlacklistApiService> apiProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new CloudRecordsViewModel_Factory(apiProvider, settingsRepositoryProvider);
  }

  public static CloudRecordsViewModel newInstance(BlacklistApiService api,
      SettingsRepository settingsRepository) {
    return new CloudRecordsViewModel(api, settingsRepository);
  }
}
