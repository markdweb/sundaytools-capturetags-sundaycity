package com.sundaytools.app.overlay;

import com.sundaytools.app.network.BlacklistApiService;
import com.sundaytools.app.ocr.TagOcrProcessor;
import com.sundaytools.app.repository.PlayerTagRepository;
import com.sundaytools.app.repository.SettingsRepository;
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
public final class OverlayBubbleService_MembersInjector implements MembersInjector<OverlayBubbleService> {
  private final Provider<PlayerTagRepository> tagRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<TagOcrProcessor> ocrProcessorProvider;

  private final Provider<BlacklistApiService> blacklistApiServiceProvider;

  public OverlayBubbleService_MembersInjector(Provider<PlayerTagRepository> tagRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<TagOcrProcessor> ocrProcessorProvider,
      Provider<BlacklistApiService> blacklistApiServiceProvider) {
    this.tagRepositoryProvider = tagRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.ocrProcessorProvider = ocrProcessorProvider;
    this.blacklistApiServiceProvider = blacklistApiServiceProvider;
  }

  public static MembersInjector<OverlayBubbleService> create(
      Provider<PlayerTagRepository> tagRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<TagOcrProcessor> ocrProcessorProvider,
      Provider<BlacklistApiService> blacklistApiServiceProvider) {
    return new OverlayBubbleService_MembersInjector(tagRepositoryProvider, settingsRepositoryProvider, ocrProcessorProvider, blacklistApiServiceProvider);
  }

  @Override
  public void injectMembers(OverlayBubbleService instance) {
    injectTagRepository(instance, tagRepositoryProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectOcrProcessor(instance, ocrProcessorProvider.get());
    injectBlacklistApiService(instance, blacklistApiServiceProvider.get());
  }

  @InjectedFieldSignature("com.sundaytools.app.overlay.OverlayBubbleService.tagRepository")
  public static void injectTagRepository(OverlayBubbleService instance,
      PlayerTagRepository tagRepository) {
    instance.tagRepository = tagRepository;
  }

  @InjectedFieldSignature("com.sundaytools.app.overlay.OverlayBubbleService.settingsRepository")
  public static void injectSettingsRepository(OverlayBubbleService instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.sundaytools.app.overlay.OverlayBubbleService.ocrProcessor")
  public static void injectOcrProcessor(OverlayBubbleService instance,
      TagOcrProcessor ocrProcessor) {
    instance.ocrProcessor = ocrProcessor;
  }

  @InjectedFieldSignature("com.sundaytools.app.overlay.OverlayBubbleService.blacklistApiService")
  public static void injectBlacklistApiService(OverlayBubbleService instance,
      BlacklistApiService blacklistApiService) {
    instance.blacklistApiService = blacklistApiService;
  }
}
