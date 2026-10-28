package com.sundaytools.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ArchiveWorker_AssistedFactory_Impl implements ArchiveWorker_AssistedFactory {
  private final ArchiveWorker_Factory delegateFactory;

  ArchiveWorker_AssistedFactory_Impl(ArchiveWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ArchiveWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ArchiveWorker_AssistedFactory> create(
      ArchiveWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ArchiveWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ArchiveWorker_AssistedFactory> createFactoryProvider(
      ArchiveWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ArchiveWorker_AssistedFactory_Impl(delegateFactory));
  }
}
