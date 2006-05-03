/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.extension;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.osoa.sca.annotations.Init;

import java.lang.reflect.Method;

/**
 * A base class for {@link WireBuilder} implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class WireBuilderSupport implements WireBuilder {

    protected RuntimeContext runtimeContext;

    @Autowire
    public void setRuntimeContext(RuntimeContext context) {
        runtimeContext = context;
    }

    public WireBuilderSupport() {
    }

    @Init(eager = true)
    public void init() throws Exception {
        runtimeContext.addBuilder(this);
    }

    public void connect(SourceWireFactory sourceFactory, TargetWireFactory targetFactory, Class targetType, boolean downScope,
                        ScopeContext targetScopeContext) throws BuilderConfigException {
        if (!handlesTargetType(targetType)) {
            return;
        }
        for (SourceInvocationConfiguration sourceInvocationConfig : sourceFactory.getConfiguration().getInvocationConfigurations()
                .values()) {
            TargetInvoker invoker = createInvoker(sourceFactory.getConfiguration()
                    .getTargetName(), sourceInvocationConfig.getMethod(), targetScopeContext, downScope);
            sourceInvocationConfig.setTargetInvoker(invoker);
        }
    }

    public void completeTargetChain(TargetWireFactory targetFactory, Class targetType, ScopeContext targetScopeContext)
            throws BuilderConfigException {
        
        if (!handlesTargetType(targetType)) {
            return;
        }
        for (TargetInvocationConfiguration targetInvocationConfig : targetFactory.getConfiguration().getInvocationConfigurations()
                .values()) {
            Method method = targetInvocationConfig.getMethod();
            TargetInvoker invoker = createInvoker(targetFactory.getConfiguration().getTargetName(), method, targetScopeContext,false);
            targetInvocationConfig.setTargetInvoker(invoker);
        }
    }

    /**
     * Returns true if an extending implementation can process the given target type
     */
    protected abstract boolean handlesTargetType(Class targetType);

    /**
     * Callback to create the specific <code>TargetInvoker</code> type for dispatching to the target type
     *
     * @param targetName the fully qualified name of the wire target
     * @param operation the operation the invoker will be associated with
     * @param context the scope context that manages the target context
     * @param downScope whether the wire source scope is "longer" than the target
     */
    protected abstract TargetInvoker createInvoker(QualifiedName targetName, Method operation, ScopeContext context, boolean downScope);

}
