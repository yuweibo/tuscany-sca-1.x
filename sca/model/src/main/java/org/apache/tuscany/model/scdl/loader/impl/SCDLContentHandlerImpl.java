/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.scdl.loader.impl;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.scdl.Binding;
import org.apache.tuscany.model.scdl.Component;
import org.apache.tuscany.model.scdl.ComponentType;
import org.apache.tuscany.model.scdl.EntryPoint;
import org.apache.tuscany.model.scdl.ExternalService;
import org.apache.tuscany.model.scdl.Implementation;
import org.apache.tuscany.model.scdl.JavaInterface;
import org.apache.tuscany.model.scdl.Module;
import org.apache.tuscany.model.scdl.ModuleComponent;
import org.apache.tuscany.model.scdl.ModuleFragment;
import org.apache.tuscany.model.scdl.ModuleWire;
import org.apache.tuscany.model.scdl.Multiplicity;
import org.apache.tuscany.model.scdl.OverrideOptions;
import org.apache.tuscany.model.scdl.Property;
import org.apache.tuscany.model.scdl.PropertyValues;
import org.apache.tuscany.model.scdl.Reference;
import org.apache.tuscany.model.scdl.Service;
import org.apache.tuscany.model.scdl.Subsystem;
import org.apache.tuscany.model.scdl.SystemWire;
import org.apache.tuscany.model.scdl.WSDLPortType;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.util.ScdlSwitch;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;
import org.apache.tuscany.model.util.ModelContentHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;

/**
 * A model content handler that transforms an SCDL model into an assembly model.
 */
public class SCDLContentHandlerImpl extends ScdlSwitch implements ModelContentHandler {

    private List contents;
    private List linkers;
    private Map targets;
    
    private List<SCDLModelLoader> scdlModelLoaders;

    private AssemblyModelContext modelContext;
    private AssemblyFactory factory;
    private org.apache.tuscany.model.assembly.ComponentType currentComponentType;
    private org.apache.tuscany.model.assembly.Service currentService;
    private org.apache.tuscany.model.assembly.Reference currentReference;
    private org.apache.tuscany.model.assembly.Module currentModule;
    private Aggregate currentAggregate;
    private SimpleComponent currentComponent;
    private org.apache.tuscany.model.assembly.ExternalService currentExternalService;
    private org.apache.tuscany.model.assembly.EntryPoint currentEntryPoint;
    private org.apache.tuscany.model.assembly.ModuleFragment currentModuleFragment;
    private org.apache.tuscany.model.assembly.Subsystem currentSubsystem;
    private org.apache.tuscany.model.assembly.ModuleComponent currentModuleComponent;
    
    /**
     * Constructor
     */
    public SCDLContentHandlerImpl(AssemblyModelContext modelContext, List<SCDLModelLoader> scdlModelLoaders) {
        this.modelContext=modelContext;
        this.factory=modelContext.getAssemblyFactory();
        this.scdlModelLoaders=scdlModelLoaders;
    }
    
    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#doSwitch(java.lang.Object)
     */
    public Object doSwitch(Object object) {
        return super.doSwitch((EObject)object);
    }

    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#startModel()
     */
    public void startModel() {
    }
    
    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#endModel()
     */
    public void endModel() {
    }

    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#setContents(java.util.List)
     */
    public void setContents(List contents) {
        this.contents=contents;
    }

    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#setLinkers(java.util.List)
     */
    public void setLinkers(List linkers) {
        this.linkers=linkers;
    }

    /**
     * @see org.apache.tuscany.model.util.ModelContentHandler#setTargets(java.util.Map)
     */
    public void setTargets(Map targets) {
        this.targets=targets;
    }
    
    /**
     * @return Returns the componentType.
     */
    public org.apache.tuscany.model.assembly.ComponentType getComponentType() {
        return currentComponentType;
    }
    
    /**
     * @return Returns the module.
     */
    public org.apache.tuscany.model.assembly.Module getModule() {
        return currentModule;
    }
    
    /**
     * @return Returns the subsystem.
     */
    public org.apache.tuscany.model.assembly.Subsystem getSubsystem() {
        return currentSubsystem;
    }
    
    /**
     * @return Returns the moduleFragment.
     */
    public org.apache.tuscany.model.assembly.ModuleFragment getModuleFragment() {
        return currentModuleFragment;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseComponentType(org.apache.tuscany.model.scdl.ComponentType)
     */
    public Object caseComponentType(ComponentType object) {
        org.apache.tuscany.model.assembly.ComponentType componentType=factory.createComponentType();
        contents.add(componentType);
        currentComponentType=componentType;
        return componentType;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseService(org.apache.tuscany.model.scdl.Service)
     */
    public Object caseService(Service object) {
        final org.apache.tuscany.model.assembly.Service service=factory.createService();
        service.setName(object.getName());
        
        linkers.add(new Runnable() {
            public void run() {
                currentComponentType.getServices().add(service);
            };
        });
        
        currentService=service;
        return service;
    }

    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseReference(org.apache.tuscany.model.scdl.Reference)
     */
    public Object caseReference(Reference object) {
        final org.apache.tuscany.model.assembly.Reference reference=factory.createReference();
        reference.setName(object.getName());
        reference.setMultiplicity(transformMultiplicity(object.getMultiplicity()));
        
        linkers.add(new Runnable() {
            public void run() {
                currentComponentType.getReferences().add(reference);
            };
        });
        
        currentReference=reference;
        return reference;
    }
    
    /**
     * Transforms an SCDL multiplicity into an assembly model multiplicity. 
     * @param multiplicity
     * @return
     */
    private org.apache.tuscany.model.assembly.Multiplicity transformMultiplicity(Multiplicity multiplicity) {
        if (multiplicity==Multiplicity._01_LITERAL)
            return org.apache.tuscany.model.assembly.Multiplicity.ZERO_ONE;
        else if (multiplicity==Multiplicity._0N_LITERAL)
            return org.apache.tuscany.model.assembly.Multiplicity.ZERO_N;
        else if (multiplicity==Multiplicity._11_LITERAL)
            return org.apache.tuscany.model.assembly.Multiplicity.ONE_ONE;
        else if (multiplicity==Multiplicity._1N_LITERAL)
            return org.apache.tuscany.model.assembly.Multiplicity.ONE_N;
        else
            return org.apache.tuscany.model.assembly.Multiplicity.ONE_ONE;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseJavaInterface(org.apache.tuscany.model.scdl.JavaInterface)
     */
    public Object caseJavaInterface(JavaInterface object) {
        final JavaServiceContract serviceContract=factory.createJavaServiceContract();
        serviceContract.setInterface(null);
        serviceContract.setCallbackInterface(null);
        serviceContract.setScope(Scope.INSTANCE);
        
        linkServiceContract(object, serviceContract);
        
        return serviceContract;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseWSDLPortType(org.apache.tuscany.model.scdl.WSDLPortType)
     */
    public Object caseWSDLPortType(WSDLPortType object) {
        final WSDLServiceContract serviceContract=factory.createWSDLServiceContract();
        serviceContract.setInterface(null);
        serviceContract.setCallbackInterface(null);
        serviceContract.setScope(Scope.INSTANCE);
        
        linkServiceContract(object, serviceContract);

        return serviceContract;
    }

    /**
     * Link a service contract with the correct owner.
     * @param object
     * @param serviceContract
     */
    private void linkServiceContract(Object object, final ServiceContract serviceContract) {
        Object container=((DataObject)object).getContainer();
        if (container instanceof Service) {
            
            // Set a service contract on a service
            final org.apache.tuscany.model.assembly.Service service=currentService;
            linkers.add(new Runnable() {
                public void run() {
                    service.setServiceContract(serviceContract);
                }
            });
        }
        else if (container instanceof Reference) {

            // Set a service contract on a reference
            final org.apache.tuscany.model.assembly.Reference reference=currentReference;
            linkers.add(new Runnable() {
                public void run() {
                    reference.setServiceContract(serviceContract);
                }
            });
        } else if (container instanceof ExternalService) {
            
            // Set a service contract on an external service
            final org.apache.tuscany.model.assembly.ExternalService externalService=currentExternalService;
            linkers.add(new Runnable() {
                public void run() {
                    externalService.getConfiguredService().getService().setServiceContract(serviceContract);
                }
            });
        } else if (container instanceof EntryPoint) {

            // Set a service contract on an entry point
            final org.apache.tuscany.model.assembly.EntryPoint entryPoint=currentEntryPoint;
            linkers.add(new Runnable() {
                public void run() {
                    entryPoint.getConfiguredService().getService().setServiceContract(serviceContract);
                    entryPoint.getConfiguredReference().getReference().setServiceContract(serviceContract);
                }
            });
        }
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseProperty(org.apache.tuscany.model.scdl.Property)
     */
    public Object caseProperty(Property object) {
        final org.apache.tuscany.model.assembly.Property property=factory.createProperty();
        property.setName(object.getName());
        property.setDefaultValue(object.getDefault());
        property.setMany(object.isMany());
        property.setRequired(object.isRequired());
        //FIXME derive the Java type from the XSD type name
        property.setType(String.class);
        
        linkers.add(new Runnable() {
            public void run() {
                currentComponentType.getProperties().add(property);
            };
        });
        
        return property;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseModule(org.apache.tuscany.model.scdl.Module)
     */
    public Object caseModule(Module object) {
        org.apache.tuscany.model.assembly.Module module=factory.createModule();
        module.setName(object.getName());
        contents.add(module);
        currentModule=module;
        currentAggregate=module;
        return module;
    }

    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseModuleFragment(org.apache.tuscany.model.scdl.ModuleFragment)
     */
    public Object caseModuleFragment(ModuleFragment object) {
        org.apache.tuscany.model.assembly.ModuleFragment moduleFragment=factory.createModuleFragment();
        moduleFragment.setName(object.getName());
        contents.add(moduleFragment);
        currentModuleFragment=moduleFragment;
        currentAggregate=moduleFragment;
        return moduleFragment;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseComponent(org.apache.tuscany.model.scdl.Component)
     */
    public Object caseComponent(Component object) {
        final SimpleComponent component=factory.createSimpleComponent();
        component.setName(object.getName());
        linkers.add(new Runnable() {
            public void run() {
                currentAggregate.getComponents().add(component);
                component.initialize(modelContext);
            };
        });
        currentComponent=component;
        return component;
    }

    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#casePropertyValues(org.apache.tuscany.model.scdl.PropertyValues)
     */
    public Object casePropertyValues(final PropertyValues object) {

        // Grab the current component
        final SimpleComponent component=currentComponent;
        linkers.add(new Runnable() {
            public void run() {
                
                // Initialize the component's configured properties
                Sequence sequence = object.getAny();
                for (int p = 0, n = sequence.size(); p < n; p++) {

                    // Get each property value element
                    commonj.sdo.Property propertyElementDef = sequence.getProperty(p);
                    DataObject propertyElement = (DataObject) sequence.getValue(p);

                    // Get the corresponding property definition
                    String propertyName = propertyElementDef.getName();
                    ConfiguredProperty configuredProperty=component.getConfiguredProperty(propertyName);
                    if (configuredProperty == null) {
                        throw new IllegalArgumentException("Undefined property " + propertyName);
                    }

                    // Get the property value text and convert to the expected java type
                    //FIXME just handle string for now
                    //FIXME SDO returns a featuremap instead of a sequence
                    //Sequence text = propertyElement.getSequence(0);
                    FeatureMap text = (FeatureMap)propertyElement.get(0);
                    if (text != null && text.size() != 0) {
                        Object rawValue = text.getValue(0);
                        configuredProperty.setValue(rawValue);
                    }
                }
            }
        });
        
        return object;
    }

    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseReferenceValues(org.apache.tuscany.model.scdl.ReferenceValues)
     */
    public Object caseReferenceValues(final org.apache.tuscany.model.scdl.ReferenceValues object) {

        // Grab the current component
        final SimpleComponent component=this.currentComponent;
        
        linkers.add(new Runnable() {
            public void run() {
                
                // Initialize the component's configured references
                Sequence sequence = object.getAny();
                for (int r = 0, n = sequence.size(); r < n; r++) {

                    // Get each reference value element
                    commonj.sdo.Property referenceElementDef = sequence.getProperty(r);
                    DataObject referenceElement = (DataObject) sequence.getValue(r);

                    // Get the corresponding reference definition
                    String referenceName = referenceElementDef.getName();
                    ConfiguredReference configuredReference=component.getConfiguredReference(referenceName);
                    if (configuredReference == null) {
                        throw new IllegalArgumentException("Undefined reference " + referenceName);
                    }

                    // Get the reference value text
                    //FIXME SDO returns a featuremap instead of a sequence
                    //Sequence text = propertyElement.getSequence(0);
                    FeatureMap text = (FeatureMap)referenceElement.get(0);
                    if (text != null && text.size() != 0) {
                        Object rawValue = text.getValue(0);
                        //FIXME
                        //configuredReference.setValue(rawValue);
                    }
                }
            }
        });
        
        return object;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseEntryPoint(org.apache.tuscany.model.scdl.EntryPoint)
     */
    public Object caseEntryPoint(EntryPoint object) {
        final org.apache.tuscany.model.assembly.EntryPoint entryPoint=factory.createEntryPoint();
        entryPoint.setName(object.getName());

        // Create a configured service and a configured reference for this entry point
        org.apache.tuscany.model.assembly.Service service=factory.createService();
        service.setName(entryPoint.getName());
        ConfiguredService configuredService=factory.createConfiguredService();
        configuredService.setService(service);
        entryPoint.setConfiguredService(configuredService);
        org.apache.tuscany.model.assembly.Reference reference=factory.createReference();
        reference.setName("");
        reference.setMultiplicity(transformMultiplicity(object.getMultiplicity()));
        ConfiguredReference configuredReference=factory.createConfiguredReference();
        configuredReference.setReference(reference);
        entryPoint.setConfiguredReference(configuredReference);
        
        linkers.add(new Runnable() {
            public void run() {
                currentAggregate.getEntryPoints().add(entryPoint);
            };
        });
        
        currentEntryPoint=entryPoint;
        return entryPoint;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseExternalService(org.apache.tuscany.model.scdl.ExternalService)
     */
    public Object caseExternalService(ExternalService object) {
        final org.apache.tuscany.model.assembly.ExternalService externalService=factory.createExternalService();
        externalService.setName(object.getName());
        
        OverrideOptions overrideOptions=object.getOverridable();
        if (overrideOptions==OverrideOptions.MAY_LITERAL)
            externalService.setOverrideOption(OverrideOption.MAY);
        else if (overrideOptions==OverrideOptions.MUST_LITERAL)
            externalService.setOverrideOption(OverrideOption.MUST);
        else if (overrideOptions==OverrideOptions.NO_LITERAL)
            externalService.setOverrideOption(OverrideOption.NO);
        else
            externalService.setOverrideOption(OverrideOption.NO);
        
        // Create a configured service for this external service
        org.apache.tuscany.model.assembly.Service service=factory.createService();
        service.setName(externalService.getName());
        ConfiguredService configuredService=factory.createConfiguredService();
        configuredService.setService(service);
        externalService.setConfiguredService(configuredService);
        
        linkers.add(new Runnable() {
            public void run() {
                currentAggregate.getExternalServices().add(externalService);
            };
        });
        currentExternalService=externalService;
        return externalService;
    }

    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseSubsystem(org.apache.tuscany.model.scdl.Subsystem)
     */
    public Object caseSubsystem(Subsystem object) {
        org.apache.tuscany.model.assembly.Subsystem subsystem=factory.createSubsystem();
        subsystem.setName(object.getName());
        subsystem.setURI(object.getUri());
        currentSubsystem=subsystem;
        currentAggregate=subsystem;
        return subsystem;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseModuleComponent(org.apache.tuscany.model.scdl.ModuleComponent)
     */
    public Object caseModuleComponent(ModuleComponent object) {
        final org.apache.tuscany.model.assembly.ModuleComponent moduleComponent=factory.createModuleComponent();
        moduleComponent.setName(object.getName());
        moduleComponent.setURI(object.getUri());

        linkers.add(new Runnable() {
            public void run() {
                currentAggregate.getComponents().add(moduleComponent);
            };
        });
        
        currentModuleComponent=moduleComponent;
        return moduleComponent;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseModuleWire(org.apache.tuscany.model.scdl.ModuleWire)
     */
    public Object caseModuleWire(ModuleWire object) {
        // TODO Auto-generated method stub
        return super.caseModuleWire(object);
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseSystemWire(org.apache.tuscany.model.scdl.SystemWire)
     */
    public Object caseSystemWire(SystemWire object) {
        // TODO Auto-generated method stub
        return super.caseSystemWire(object);
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseImplementation(org.apache.tuscany.model.scdl.Implementation)
     */
    public Object caseImplementation(Implementation object) {
        final SimpleComponent component=currentComponent;
        
        for (SCDLModelLoader scdlModelLoader : scdlModelLoaders) {
                
            // Invoke an SCDL loader to handle the specific implementation type
            final ComponentImplementation implementation=(ComponentImplementation)scdlModelLoader.load(object);
            if (implementation!=null) {
                component.setComponentImplementation(implementation);
                return implementation;
            }
            
        }
        return null;
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.util.ScdlSwitch#caseBinding(org.apache.tuscany.model.scdl.Binding)
     */
    public Object caseBinding(Binding object) {
        
        final List<org.apache.tuscany.model.assembly.Binding> bindings;
        Object container=((DataObject)object).getContainer();
        if (container instanceof EntryPoint) {
            bindings=currentEntryPoint.getBindings();
        } else if (container instanceof ExternalService) {
            bindings=currentExternalService.getBindings();
        } else
            bindings=null;

        for (SCDLModelLoader scdlModelLoader : scdlModelLoaders) {
                
            // Invoke an SCDL loader to handle the specific binding type
            final org.apache.tuscany.model.assembly.Binding binding=(org.apache.tuscany.model.assembly.Binding)scdlModelLoader.load(object);
            if (binding!=null) {
                bindings.add(binding);
                return binding;
            }
            
        }
        return null;
    }
}
