// filename: CallbackBundle.java
package com.printer.util;

//简单的Bundle参数回调接口
public interface CallbackUSB {
	abstract void callback(final String str,boolean toShow);
	abstract void hasUSB(boolean hasUSB);
	
}
