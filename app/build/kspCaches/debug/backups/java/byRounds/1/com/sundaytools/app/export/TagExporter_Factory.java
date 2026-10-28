package com.sundaytools.app.export;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class TagExporter_Factory implements Factory<TagExporter> {
  @Override
  public TagExporter get() {
    return newInstance();
  }

  public static TagExporter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TagExporter newInstance() {
    return new TagExporter();
  }

  private static final class InstanceHolder {
    private static final TagExporter_Factory INSTANCE = new TagExporter_Factory();
  }
}
