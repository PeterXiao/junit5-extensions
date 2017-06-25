package name.falgout.jeffrey.testing.junit5;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import name.falgout.jeffrey.testing.junit5.ExpectFailure.Cause;
import name.falgout.jeffrey.testing.junit5.TestPlanExecutionReport.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class UsageExampleTests {
  @Nested
  class PositiveExamples {
    @ParameterizedTest
    @ValueSource(strings = {
        "java.lang.Object",
        "name.falgout.jeffrey.testing.junit5.ClassArgumentConverter",
    })
    void anything(@ConvertWith(ClassArgumentConverter.class) Class<?> anything) {
      assertThat(anything).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "java.util.List",
        "java.util.Collection",
        "java.lang.Object",
    })
    void anySuperclass(
        @ConvertWith(ClassArgumentConverter.class) Class<? super List<?>> superclassOfList) {
      assertThat(superclassOfList).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "java.util.List",
        "java.util.ArrayList",
    })
    void anySubclass(@ConvertWith(ClassArgumentConverter.class) Class<? extends List<?>> anything) {
      assertThat(anything).isNotNull();
    }
  }

  @SuppressWarnings("unused")
  @Nested
  class NegativeExamples {
    @ExpectFailure({
        @Cause(type = ParameterResolutionException.class),
        @Cause(type = ArgumentConversionException.class),
        @Cause(type = ClassNotFoundException.class)
    })
    @ParameterizedTest
    @ValueSource(strings = "123ClassDoesNotExist")
    void classNotFound(@ConvertWith(ClassArgumentConverter.class) Class<?> clazz) {}

    @ExpectFailure({
        @Cause(type = ParameterResolutionException.class),
        @Cause(type = ArgumentConversionException.class, message = "Invalid parameter type")
    })
    @ParameterizedTest
    @ValueSource(strings = "java.lang.Object")
    void badParameterType(@ConvertWith(ClassArgumentConverter.class) String clazz) {}

    @ExpectFailure({
        @Cause(type = ParameterResolutionException.class),
        @Cause(
            type = ArgumentConversionException.class,
            message = "java.lang.Class<java.util.List> is not assignable to"
                + " java.lang.Class<java.util.Collection<?>>"
        )
    })
    @ParameterizedTest
    @ValueSource(strings = "java.util.List")
    void wrongClass(@ConvertWith(ClassArgumentConverter.class) Class<Collection<?>> clazz) {}

    @ExpectFailure({
        @Cause(type = ParameterResolutionException.class),
        @Cause(type = ArgumentConversionException.class, message = "is not assignable to")
    })
    @ParameterizedTest
    @ValueSource(strings = "java.util.List")
    void badLowerBound(
        @ConvertWith(ClassArgumentConverter.class) Class<? super Collection<?>> clazz) {}

    @ExpectFailure({
        @Cause(type = ParameterResolutionException.class),
        @Cause(type = ArgumentConversionException.class, message = "is not assignable to")
    })
    @ParameterizedTest
    @ValueSource(strings = "java.lang.Object")
    void badUpperBound(
        @ConvertWith(ClassArgumentConverter.class) Class<? extends Collection<?>> clazz) {}
  }
}
