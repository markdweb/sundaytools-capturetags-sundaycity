package com.sundaytools.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SundayToolsApp_MembersInjector implements MembersInjector<SundayToolsApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public SundayToolsApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<SundayToolsApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new SundayToolsApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(SundayToolsApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.sundaytools.app.SundayToolsApp.workerFactory")
  public static void injectWorkerFactory(SundayToolsApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
