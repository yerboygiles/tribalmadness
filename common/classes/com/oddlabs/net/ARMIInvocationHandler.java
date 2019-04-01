package com.oddlabs.net;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

final strictfp class ARMIInvocationHandler implements InvocationHandler {
	private final ARMIInterfaceMethods armi_interface_methods;
	private final ARMIEventWriter broker;
	private final ARMIArgumentWriter writer;

	public ARMIInvocationHandler(ARMIEventWriter broker, ARMIArgumentWriter writer, ARMIInterfaceMethods armi_interface_methods) {
		this.broker = broker;
		this.writer = writer;
		this.armi_interface_methods = armi_interface_methods;
	}

        @Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		byte method_id = armi_interface_methods.getMethodIndex(method);
		broker.handle(new ARMIEvent(writer, method.getParameterTypes(), method_id, args));
		return null;
	}
}
