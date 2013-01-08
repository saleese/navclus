/*******************************************************************************
Copyright (c) 2010, 2012 Seonah Lee, SA Lab, KAIST
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html *
Contributors:
Seonah Lee - initial implementation
*******************************************************************************/

package navclus.userinterface.classdiagram.java.manager;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class TypeNode {
	
	private CompilationUnit cu = null;	
	private IType 		    type;
	public  Set<IField>     fields; 	
	public  Set<IMethod>    methods; 
	public  Set<IType>      embeddedTypes;   	
	STATE   state;

	public TypeNode(IType type) {
		this.type = type;
		this.fields = new LinkedHashSet<IField>();    
		this.methods = new LinkedHashSet<IMethod>();  
		this.embeddedTypes = new LinkedHashSet<IType>(); 
		this.state = STATE.toCreate;
	}	
	
	public boolean addField(IField inputfield) {
		if (this.contain(inputfield))
			return false;

		this.fields.add((IField) inputfield);
		this.setState(STATE.toUpdate);
		return true;
	}

	public boolean addMethod(IMethod inputmethod) {
		if (this.contain(inputmethod))
			return false;

		this.methods.add((IMethod) inputmethod);
		this.setState(STATE.toUpdate);
		return true;
	}

	public void clear() {		
		this.type = null;
		this.cu = null;
		this.fields.clear();    
		this.methods.clear();   
		this.embeddedTypes.clear();     		
	}

	public boolean contain(IField inputfield) {
		
		for (IField field: this.fields) {
			if (field.getHandleIdentifier().equals(inputfield.getHandleIdentifier())) {
				return true;
			}
		}		
		return false;
	}

	public boolean contain(IMethod inputmethod) {
		
		for (IMethod method: this.methods) {
			if (method.getHandleIdentifier().equals(inputmethod.getHandleIdentifier())) {
				return true;
			}
		}
		return false;
	}

	public boolean contain(IType inputtype) {
		
		for (IType type: this.embeddedTypes) {
			if (type.getHandleIdentifier().equals(inputtype.getHandleIdentifier())) {
				return true;
			}
		}
		return false;
	}
	
	public CompilationUnit getCu() {
		return cu;
	}
	
	public STATE getState() {
		return state;
	}

	public IType getType() {
		return type;
	}
	
	public boolean removeField(IField inputfield) {
		if (this.contain(inputfield)) {			
			this.fields.remove((IField) inputfield);
//			this.setDirty(true);
			this.setState(STATE.toUpdate);
			return true;
		}
		else
			return false;
	}

	public boolean removeMethod(IMethod inputmethod) {
		if (this.contain(inputmethod)) {		
			this.methods.remove((IMethod) inputmethod);
			this.setState(STATE.toUpdate);
			return true;
		}
		else
			return false;
	}
	
	public boolean removeType(IType inputtype) {
		if (this.contain(inputtype)) {
			this.embeddedTypes.remove((IType) inputtype);
			this.setState(STATE.toUpdate);
			return true;
		}
		else
			return false;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}
	
	public void setState(STATE nodestate) {
		this.state = nodestate;
	}
	
	
	public void setType(IType type) {
		this.type = type;
	}
}
