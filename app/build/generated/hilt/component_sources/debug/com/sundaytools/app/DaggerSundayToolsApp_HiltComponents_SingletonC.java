package com.sundaytools.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.sundaytools.app.database.AppDatabase;
import com.sundaytools.app.database.dao.PlayerTagDao;
import com.sundaytools.app.di.AppModule_ProvideDatabaseFactory;
import com.sundaytools.app.di.AppModule_ProvidePlayerTagDaoFactory;
import com.sundaytools.app.network.BlacklistApiService;
import com.sundaytools.app.ocr.TagOcrProcessor;
import com.sundaytools.app.overlay.OverlayBubbleService;
import com.sundaytools.app.overlay.OverlayBubbleService_MembersInjector;
import com.sundaytools.app.repository.PlayerTagRepository;
import com.sundaytools.app.repository.SettingsRepository;
import com.sundaytools.app.viewmodel.CloudRecordsViewModel;
import com.sundaytools.app.viewmodel.CloudRecordsViewModel_HiltModules;
import com.sundaytools.app.viewmodel.HistoryDetailViewModel;
import com.sundaytools.app.viewmodel.HistoryDetailViewModel_HiltModules;
import com.sundaytools.app.viewmodel.HistoryViewModel;
import com.sundaytools.app.viewmodel.HistoryViewModel_HiltModules;
import com.sundaytools.app.viewmodel.HomeViewModel;
import com.sundaytools.app.viewmodel.HomeViewModel_HiltModules;
import com.sundaytools.app.viewmodel.SettingsViewModel;
import com.sundaytools.app.viewmodel.SettingsViewModel_HiltModules;
import com.sundaytools.app.worker.ArchiveWorker;
import com.sundaytools.app.worker.ArchiveWorker_AssistedFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerSundayToolsApp_HiltComponents_SingletonC {
  private DaggerSundayToolsApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SundayToolsApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SundayToolsApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SundayToolsApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SundayToolsApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SundayToolsApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SundayToolsApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SundayToolsApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SundayToolsApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SundayToolsApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SundayToolsApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SundayToolsApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SundayToolsApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SundayToolsApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(5).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_CloudRecordsViewModel, CloudRecordsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HistoryDetailViewModel, HistoryDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sundaytools_app_viewmodel_SettingsViewModel = "com.sundaytools.app.viewmodel.SettingsViewModel";

      static String com_sundaytools_app_viewmodel_CloudRecordsViewModel = "com.sundaytools.app.viewmodel.CloudRecordsViewModel";

      static String com_sundaytools_app_viewmodel_HistoryDetailViewModel = "com.sundaytools.app.viewmodel.HistoryDetailViewModel";

      static String com_sundaytools_app_viewmodel_HistoryViewModel = "com.sundaytools.app.viewmodel.HistoryViewModel";

      static String com_sundaytools_app_viewmodel_HomeViewModel = "com.sundaytools.app.viewmodel.HomeViewModel";

      @KeepFieldType
      SettingsViewModel com_sundaytools_app_viewmodel_SettingsViewModel2;

      @KeepFieldType
      CloudRecordsViewModel com_sundaytools_app_viewmodel_CloudRecordsViewModel2;

      @KeepFieldType
      HistoryDetailViewModel com_sundaytools_app_viewmodel_HistoryDetailViewModel2;

      @KeepFieldType
      HistoryViewModel com_sundaytools_app_viewmodel_HistoryViewModel2;

      @KeepFieldType
      HomeViewModel com_sundaytools_app_viewmodel_HomeViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SundayToolsApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CloudRecordsViewModel> cloudRecordsViewModelProvider;

    private Provider<HistoryDetailViewModel> historyDetailViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.cloudRecordsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.historyDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(5).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_CloudRecordsViewModel, ((Provider) cloudRecordsViewModelProvider)).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HistoryDetailViewModel, ((Provider) historyDetailViewModelProvider)).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_sundaytools_app_viewmodel_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_sundaytools_app_viewmodel_HomeViewModel = "com.sundaytools.app.viewmodel.HomeViewModel";

      static String com_sundaytools_app_viewmodel_CloudRecordsViewModel = "com.sundaytools.app.viewmodel.CloudRecordsViewModel";

      static String com_sundaytools_app_viewmodel_HistoryDetailViewModel = "com.sundaytools.app.viewmodel.HistoryDetailViewModel";

      static String com_sundaytools_app_viewmodel_SettingsViewModel = "com.sundaytools.app.viewmodel.SettingsViewModel";

      static String com_sundaytools_app_viewmodel_HistoryViewModel = "com.sundaytools.app.viewmodel.HistoryViewModel";

      @KeepFieldType
      HomeViewModel com_sundaytools_app_viewmodel_HomeViewModel2;

      @KeepFieldType
      CloudRecordsViewModel com_sundaytools_app_viewmodel_CloudRecordsViewModel2;

      @KeepFieldType
      HistoryDetailViewModel com_sundaytools_app_viewmodel_HistoryDetailViewModel2;

      @KeepFieldType
      SettingsViewModel com_sundaytools_app_viewmodel_SettingsViewModel2;

      @KeepFieldType
      HistoryViewModel com_sundaytools_app_viewmodel_HistoryViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sundaytools.app.viewmodel.CloudRecordsViewModel 
          return (T) new CloudRecordsViewModel(singletonCImpl.blacklistApiServiceProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 1: // com.sundaytools.app.viewmodel.HistoryDetailViewModel 
          return (T) new HistoryDetailViewModel(singletonCImpl.playerTagRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.sundaytools.app.viewmodel.HistoryViewModel 
          return (T) new HistoryViewModel(singletonCImpl.playerTagRepositoryProvider.get());

          case 3: // com.sundaytools.app.viewmodel.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.playerTagRepositoryProvider.get(), singletonCImpl.blacklistApiServiceProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 4: // com.sundaytools.app.viewmodel.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SundayToolsApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SundayToolsApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectOverlayBubbleService(OverlayBubbleService overlayBubbleService) {
      injectOverlayBubbleService2(overlayBubbleService);
    }

    private OverlayBubbleService injectOverlayBubbleService2(OverlayBubbleService instance) {
      OverlayBubbleService_MembersInjector.injectTagRepository(instance, singletonCImpl.playerTagRepositoryProvider.get());
      OverlayBubbleService_MembersInjector.injectSettingsRepository(instance, singletonCImpl.settingsRepositoryProvider.get());
      OverlayBubbleService_MembersInjector.injectOcrProcessor(instance, singletonCImpl.tagOcrProcessorProvider.get());
      OverlayBubbleService_MembersInjector.injectBlacklistApiService(instance, singletonCImpl.blacklistApiServiceProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends SundayToolsApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<PlayerTagRepository> playerTagRepositoryProvider;

    private Provider<ArchiveWorker_AssistedFactory> archiveWorker_AssistedFactoryProvider;

    private Provider<SettingsRepository> settingsRepositoryProvider;

    private Provider<BlacklistApiService> blacklistApiServiceProvider;

    private Provider<TagOcrProcessor> tagOcrProcessorProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private PlayerTagDao playerTagDao() {
      return AppModule_ProvidePlayerTagDaoFactory.providePlayerTagDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.sundaytools.app.worker.ArchiveWorker", ((Provider) archiveWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.playerTagRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PlayerTagRepository>(singletonCImpl, 1));
      this.archiveWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ArchiveWorker_AssistedFactory>(singletonCImpl, 0));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 4));
      this.blacklistApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<BlacklistApiService>(singletonCImpl, 3));
      this.tagOcrProcessorProvider = DoubleCheck.provider(new SwitchingProvider<TagOcrProcessor>(singletonCImpl, 5));
    }

    @Override
    public void injectSundayToolsApp(SundayToolsApp sundayToolsApp) {
      injectSundayToolsApp2(sundayToolsApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private SundayToolsApp injectSundayToolsApp2(SundayToolsApp instance) {
      SundayToolsApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.sundaytools.app.worker.ArchiveWorker_AssistedFactory 
          return (T) new ArchiveWorker_AssistedFactory() {
            @Override
            public ArchiveWorker create(Context context, WorkerParameters params) {
              return new ArchiveWorker(context, params, singletonCImpl.playerTagRepositoryProvider.get());
            }
          };

          case 1: // com.sundaytools.app.repository.PlayerTagRepository 
          return (T) new PlayerTagRepository(singletonCImpl.playerTagDao());

          case 2: // com.sundaytools.app.database.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.sundaytools.app.network.BlacklistApiService 
          return (T) new BlacklistApiService(singletonCImpl.settingsRepositoryProvider.get());

          case 4: // com.sundaytools.app.repository.SettingsRepository 
          return (T) new SettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.sundaytools.app.ocr.TagOcrProcessor 
          return (T) new TagOcrProcessor();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
