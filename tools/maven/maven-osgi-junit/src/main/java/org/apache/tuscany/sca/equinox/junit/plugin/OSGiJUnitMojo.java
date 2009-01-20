/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.equinox.junit.plugin;

import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import junit.framework.Assert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.tuscany.sca.node.equinox.launcher.EquinoxHost;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * @version $Rev$ $Date$
 * @goal test
 * @phase test
 * @requiresDependencyResolution test
 * @description Run the unit test with OSGi
 */
public class OSGiJUnitMojo extends AbstractMojo {
    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The basedir of the project.
     * 
     * @parameter expression="${basedir}"
     * @required @readonly
     */
    protected File basedir;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List remoteRepos;

    protected Artifact getArtifact(String groupId, String artifactId) throws MojoExecutionException {
        Artifact artifact;
        VersionRange vr;
        try {
            vr = VersionRange.createFromVersionSpec(project.getVersion());
        } catch (InvalidVersionSpecificationException e1) {
            vr = VersionRange.createFromVersion(project.getVersion());
        }
        artifact = factory.createDependencyArtifact(groupId, artifactId, vr, "jar", null, Artifact.SCOPE_TEST);

        try {
            resolver.resolve(artifact, remoteRepos, local);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact.", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact.", e);
        }

        return artifact;
    }

    /**
     * Returns a string representation of the given bundle.
     * 
     * @param b
     * @param verbose
     * @return
     */
    static String string(Bundle bundle, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(bundle.getBundleId()).append(" ").append(bundle.getSymbolicName());
        int s = bundle.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        if (verbose) {
            sb.append(" ").append(bundle.getLocation());
            sb.append(" ").append(bundle.getHeaders());
        }
        return sb.toString();
    }

    private void generateJar(File root, File jar, Manifest mf) throws IOException {
        getLog().info("Generating " + jar.toString());
        FileOutputStream fos = new FileOutputStream(jar);
        JarOutputStream jos = mf != null ? new JarOutputStream(fos, mf) : new JarOutputStream(fos);
        addDir(jos, root, root);
        jos.close();
    }

    /**
     * Convert the maven version into OSGi version 
     * @param mavenVersion
     * @return
     */
    static String osgiVersion(String mavenVersion) {
        ArtifactVersion ver = new DefaultArtifactVersion(mavenVersion);
        String qualifer = ver.getQualifier();
        if (qualifer != null) {
            StringBuffer buf = new StringBuffer(qualifer);
            for (int i = 0; i < buf.length(); i++) {
                char c = buf.charAt(i);
                if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                    // Keep as-is
                } else {
                    buf.setCharAt(i, '_');
                }
            }
            qualifer = buf.toString();
        }
        Version osgiVersion =
            new Version(ver.getMajorVersion(), ver.getMinorVersion(), ver.getIncrementalVersion(), qualifer);
        String version = osgiVersion.toString();
        return version;
    }

    private Manifest createMainBundle() throws IOException {
        File mf = new File(project.getBasedir(), "META-INF/MANIFEST.MF");
        Manifest manifest = null;
        if (mf.isFile()) {
            manifest = new Manifest(new FileInputStream(mf));
            String bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            if (bundleName != null) {
                return manifest;
            }
        }
        if (manifest == null) {
            manifest = new Manifest();
        }
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Manifest-Version", "1.0");
        attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
        attributes.putValue(BUNDLE_SYMBOLICNAME, project.getGroupId() + "." + project.getArtifactId());
        attributes.putValue(BUNDLE_NAME, project.getDescription());
        attributes.putValue(BUNDLE_VERSION, osgiVersion(project.getVersion()));
        attributes.putValue(Constants.DYNAMICIMPORT_PACKAGE, "*");
        return manifest;
    }

    private Manifest createTestFragment(Manifest mf) {
        // Create a manifest
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Manifest-Version", "1.0");
        attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
        String host = mf.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
        int sc = host.indexOf(';');
        if (sc != -1) {
            host = host.substring(0, sc);
        }
        attributes.putValue(BUNDLE_SYMBOLICNAME, host + ".tests");
        attributes.putValue(BUNDLE_NAME, mf.getMainAttributes().getValue(BUNDLE_NAME) + " Tests");
        attributes.putValue(BUNDLE_VERSION, mf.getMainAttributes().getValue(BUNDLE_VERSION));
        attributes.putValue(Constants.FRAGMENT_HOST, host + ";bundle-version=\""
            + mf.getMainAttributes().getValue(BUNDLE_VERSION)
            + "\"");
        // The main bundle may not have the dependency on JUNIT
        attributes.putValue(Constants.DYNAMICIMPORT_PACKAGE, "*");
        return manifest;
    }

    private void addDir(JarOutputStream jos, File root, File dir) throws IOException, FileNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addDir(jos, root, file);
            } else if (file.isFile()) {
                // getLog().info(file.toString());
                String uri = root.toURI().relativize(file.toURI()).toString();
                if ("META-INF/MANIFEST.MF".equals(uri)) {
                    continue;
                }
                ZipEntry entry = new ZipEntry(uri);
                jos.putNextEntry(entry);
                byte[] buf = new byte[4096];
                FileInputStream in = new FileInputStream(file);
                for (;;) {
                    int len = in.read(buf);
                    if (len > 0) {
                        jos.write(buf, 0, len);
                    } else {
                        break;
                    }
                }
                in.close();
                jos.closeEntry();
            }
        }
    }

    public void execute() throws MojoExecutionException {
        if (project.getPackaging().equals("pom")) {
            return;
        }

        Log log = getLog();
        Set<URL> jarFiles = new HashSet<URL>();
        for (Object o : project.getArtifacts()) {
            Artifact a = (Artifact)o;
            if ("pom".equals(a.getType())) {
                // Skip pom projects
                continue;
            }
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Adding: " + a);
                }
                jarFiles.add(a.getFile().toURI().toURL());
            } catch (MalformedURLException e) {
                getLog().error(e);
            }
        }

        /*
         * Add org.apache.tuscany.sca:tuscany-extensibility-osgi module
         */
        String aid = "tuscany-extensibility-equinox";
        Artifact ext = getArtifact("org.apache.tuscany.sca", aid);
        try {
            URL url = ext.getFile().toURI().toURL();
            if (log.isDebugEnabled()) {
                log.debug("Adding: " + ext);
            }
            jarFiles.add(url);
        } catch (MalformedURLException e) {
            getLog().error(e);
        }

        String name = project.getBuild().getFinalName();
        String mainBundleName = null;
        File mainJar = new File(project.getBuild().getDirectory(), name + "-osgi.jar");
        File testJar = new File(project.getBuild().getDirectory(), name + "-osgi-tests.jar");
        try {
            Manifest manifest = createMainBundle();
            mainBundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            int sc = mainBundleName.indexOf(';');
            if (sc != -1) {
                mainBundleName = mainBundleName.substring(0, sc);
            }
            generateJar(new File(project.getBuild().getOutputDirectory()), mainJar, manifest);
            Manifest testManifest = createTestFragment(manifest);
            generateJar(new File(project.getBuild().getTestOutputDirectory()), testJar, testManifest);
            jarFiles.add(mainJar.toURI().toURL());
            jarFiles.add(testJar.toURI().toURL());
        } catch (IOException e) {
            getLog().error(e);
        }

        if (log.isDebugEnabled()) {
            for (URL url : jarFiles) {
                getLog().debug(url.toString());
            }
        }

        try {
            EquinoxHost host = new EquinoxHost(jarFiles);
            BundleContext context = host.start();

            if (getLog().isDebugEnabled()) {
                for (Bundle b : context.getBundles()) {
                    getLog().debug(string(b, false));
                }
            }
            Bundle testBundle = null;
            for (Bundle bundle : context.getBundles()) {
                // Fragement bundle cannot be used to load class, use the main bundle
                if (mainBundleName.equals(bundle.getSymbolicName())) {
                    testBundle = bundle;
                    break;
                }
            }

            try {
                if (testBundle != null) {
                    runAllTestsFromDirs(testBundle, project.getBuild().getTestOutputDirectory());
                }
            } finally {
                host.stop();
            }
        } catch (Throwable e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public void getTestCases(File dir, String prefix, HashSet<String> testCaseSet) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String newPrefix = prefix == null ? file.getName() : prefix + "." + file.getName();
                getTestCases(file, newPrefix, testCaseSet);
            } else if (file.getName().endsWith("TestCase.class")) {
                String name = file.getName();
                name = name.substring(0, name.length() - 6); // remove .class
                name = (prefix == null) ? name : prefix + "." + name;

                testCaseSet.add(name);
            }
        }
    }

    public void runAllTestsFromDirs(Bundle testBundle, String testDir) throws Exception {

        int failures = 0;
        HashSet<String> testCaseSet = new HashSet<String>();
        getTestCases(new File(testDir), null, testCaseSet);
        for (String className : testCaseSet) {
            Class testClass = testBundle.loadClass(className);
            failures += runTestCase(testBundle, testClass);
        }

        Assert.assertEquals(0, failures);

    }

    /**
     * Use java reflection to call JUNIT as the JUNIT might be in the bundles
     * @param testBundle
     * @param testClass
     * @return
     * @throws Exception
     */
    public int runTestCase(Bundle testBundle, Class testClass) throws Exception {

        if (testClass.getName().endsWith("TestCase")) {
            getLog().info("Running: " + testClass.getName());
            Class coreClass = testBundle.loadClass("org.junit.runner.JUnitCore");
            Object core = coreClass.newInstance();
            Class reqClass = testBundle.loadClass("org.junit.runner.Request");
            Method aClass = reqClass.getMethod("aClass", Class.class);
            Object req = aClass.invoke(null, testClass);
            Method run = coreClass.getMethod("run", reqClass);
            Object result = run.invoke(core, req);
            Object runs = result.getClass().getMethod("getRunCount").invoke(result);
            Object ignores = result.getClass().getMethod("getIgnoreCount").invoke(result);
            List failureList = (List)result.getClass().getMethod("getFailures").invoke(result);

            int failures = 0, errors = 0;
            Class errorClass = testBundle.loadClass("junit.framework.AssertionFailedError");
            for (Object f : failureList) {
                Object ex = f.getClass().getMethod("getException").invoke(f);
                if (errorClass.isInstance(ex)) {
                    failures++;
                } else {
                    errors++;
                }
                getLog().error((Throwable)ex);
            }

            getLog().info("Test Runs: " + runs
                + ", Failures: "
                + failures
                + ", Errors: "
                + errors
                + ", Ignores: "
                + ignores);

            return failureList.size();

        }
        return 0;

    }

}