package com.sundaytools.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.sundaytools.app.repository.PlayerTagRepository;
import dagger.internal.DaggerGenerated;
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
public final class ArchiveWorker_Factory {
  private final Provider<PlayerTagRepository> repositoryProvider;

  public ArchiveWorker_Factory(Provider<PlayerTagRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public ArchiveWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get());
  }

  public static ArchiveWorker_Factory create(Provider<PlayerTagRepository> repositoryProvider) {
    return new ArchiveWorker_Factory(repositoryProvider);
  }

  public static ArchiveWorker newInstance(Context context, WorkerParameters params,
      PlayerTagRepository repository) {
    return new ArchiveWorker(context, params, repository);
  }
}
