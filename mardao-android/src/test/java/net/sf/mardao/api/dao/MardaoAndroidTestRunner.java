package net.sf.mardao.api.dao;

import java.io.File;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;

public class MardaoAndroidTestRunner extends RobolectricTestRunner {

    private static File testResources   = new File("src/test/resources/");
    private static File androidManifest = new File(testResources, "AndroidManifest.xml");
    private static File resources       = new File(testResources, "res/");

    public MardaoAndroidTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass, new RobolectricConfig(androidManifest, resources));
    }
}
