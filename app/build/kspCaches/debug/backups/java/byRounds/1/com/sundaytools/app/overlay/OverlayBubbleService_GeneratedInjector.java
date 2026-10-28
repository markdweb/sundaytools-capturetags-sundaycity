package com.sundaytools.app.overlay;

import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = OverlayBubbleService.class
)
@GeneratedEntryPoint
@InstallIn(ServiceComponent.class)
@Generated("dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator")
public interface OverlayBubbleService_GeneratedInjector {
  void injectOverlayBubbleService(OverlayBubbleService overlayBubbleService);
}
