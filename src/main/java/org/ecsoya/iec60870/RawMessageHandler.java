/**
 * 
 */
package org.ecsoya.iec60870;

/**
 * @author Jin Liu (jin.liu@soyatec.com)
 */
@FunctionalInterface
public interface RawMessageHandler {
	boolean invoke(Object parameter, byte[] message, int messageSize);
}
