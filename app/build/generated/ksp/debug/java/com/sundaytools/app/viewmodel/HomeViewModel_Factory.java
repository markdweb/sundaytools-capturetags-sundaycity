package com.sundaytools.app.viewmodel;

import com.sundaytools.app.network.BlacklistApiService;
import com.sundaytools.app.repository.PlayerTagRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<PlayerTagRepository> repositoryProvider;

  private final Provider<BlacklistApiService> blacklistApiServiceProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public HomeViewModel_Factory(Provider<PlayerTagRepository> repositoryProvider,
      Provider<BlacklistApiService> blacklistApiServiceProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.blacklistApiServiceProvider = blacklistApiServiceProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), blacklistApiServiceProvider.get(), settingsRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<PlayerTagRepository> repositoryProvider,
      Provider<BlacklistApiService> blacklistApiServiceProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new HomeViewModel_Factory(repositoryProvider, blacklistApiServiceProvider, settingsRepositoryProvider);
  }

  public static HomeViewModel newInstance(PlayerTagRepository repository,
      BlacklistApiService blacklistApiService, SettingsRepository settingsRepository) {
    return new HomeViewModel(repository, blacklistApiService, settingsRepository);
  }
}
