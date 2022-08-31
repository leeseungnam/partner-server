package kr.wrightbrothers.framework.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();

	public JsonUtil() {
		mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T> T getObject(String jsonStr, Class<?> clazz) throws Exception {
		return (T) mapper.readValue(jsonStr, clazz);
	}

	public static <T> T getObject(String jsonStr, TypeReference<T> clazz) throws Exception {
		return (T) mapper.readValue(jsonStr, clazz);
	}

	public static <T> List<T> getObjectList(String jsonStr, Class<?> clazz) throws Exception {
		List<LinkedHashMap<String, String>> listMap = (List<LinkedHashMap<String, String>>) mapper.readValue(jsonStr,
				new TypeReference<List<LinkedHashMap<String, String>>>() {

				});
		List<T> list = new ArrayList<>();
		for (int i = 0; i < listMap.size(); i++)
			list.add((T) mapper.convertValue(listMap.get(i), clazz));
		return list;
	}

	public static <T> List<T> getObjectList(List<LinkedHashMap<String, String>> listMap, Class<?> clazz)
			throws Exception {
		List<T> list = new ArrayList<>();
		if (listMap != null)
			for (int i = 0; i < listMap.size(); i++)
				list.add((T) mapper.convertValue(listMap.get(i), clazz));
		return list;
	}

	public static String ToString(Object obj) throws Exception {
		return ToString(obj, false);
	}

	public static String ToString(Object obj, boolean nonNull) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (nonNull) {
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		}
		return mapper.writeValueAsString(obj);
	}
}