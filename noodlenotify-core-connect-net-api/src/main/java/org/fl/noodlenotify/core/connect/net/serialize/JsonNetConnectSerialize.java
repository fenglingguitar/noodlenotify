package org.fl.noodlenotify.core.connect.net.serialize;

import org.fl.noodle.common.connect.serialize.ConnectSerialize;
import org.fl.noodle.common.util.json.JsonTranslator;

public class JsonNetConnectSerialize implements ConnectSerialize {

	@Override
	public String serializationToString(Object object) throws Exception {
		return JsonTranslator.toStringWithClassName(object);
	}

	@Override
	public byte[] serializationToByte(Object object) throws Exception {
		return JsonTranslator.toByteArray(object);
	}

	@Override
	public <T> T deserializationFromString(String string, Class<T> clazz) throws Exception {
		return JsonTranslator.fromString(string, clazz);
	}

	@Override
	public <T> T deserializationFromByte(byte[] byteArray, Class<T> clazz) throws Exception {
		return JsonTranslator.fromByteArray(byteArray, clazz);
	}
}
