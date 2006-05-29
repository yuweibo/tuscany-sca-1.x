package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.wire.ReferenceAutowire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ServiceWire;
import org.apache.tuscany.spi.wire.RuntimeWire;

/**
 * The source side of an wire configured to autowire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemReferenceAutowire<T> implements ReferenceAutowire<T> {
    private String referenceName;
    private Class<T> businessInterface;
    private AutowireContext<?> context;

    public SystemReferenceAutowire(String referenceName, Class<T> businessInterface, AutowireContext<?> context) {
        this.referenceName = referenceName;
        this.businessInterface = businessInterface;
        this.context = context;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return null;
    }

    public void setTargetName(QualifiedName targetName) {
    }

    public T getTargetService() throws TargetException {
        return context.resolveInstance(businessInterface);
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public void setTargetWire(RuntimeWire<T> wire) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, ReferenceInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, ReferenceInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public void setTargetWire(ServiceWire<T> wire) {
    }

    public boolean isOptimizable() {
        return true;
    }
}
