package com.sundaytools.app.viewmodel;

import androidx.lifecycle.SavedStateHandle;
import com.sundaytools.app.repository.PlayerTagRepository;
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
public final class HistoryDetailViewModel_Factory implements Factory<HistoryDetailViewModel> {
  private final Provider<PlayerTagRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public HistoryDetailViewModel_Factory(Provider<PlayerTagRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public HistoryDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static HistoryDetailViewModel_Factory create(
      Provider<PlayerTagRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new HistoryDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static HistoryDetailViewModel newInstance(PlayerTagRepository repository,
      SavedStateHandle savedStateHandle) {
    return new HistoryDetailViewModel(repository, savedStateHandle);
  }
}
