package com.api.sdk.okta.oktaSDK.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class ObjectMapperHelper {

	private static final ObjectMapper objectMapperWithIndentation = initWithIndentation();
	private static final ObjectMapper objectMapperWithoutIndentation = initWithoutIndentation();

	private ObjectMapperHelper() {
	}

	/**
	 * This method read value from input stream of url
	 *
	 * @param src
	 * @param valueType
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public static <T> T readValueFromURL(URL src, Class<T> valueType) throws IOException {
		try (InputStream inputStream = src.openStream()) {
			Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			return objectMapperWithoutIndentation.readValue(reader, valueType);
		}
	}

	/**
	 * This method read value from Json string, based on Class passed in
	 *
	 * @param value
	 * @param valueType
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public static <T> T readValueFromJSON(String value, Class<T> valueType) throws IOException {
		return objectMapperWithoutIndentation.readValue(value, valueType);
	}

	/**
	 * This method read value from json str, based on typereference
	 * <p>
	 * Typereference is jackson's class metadata placeholder, jackson can use this metadata to (de)serialization
	 * with json.......
	 * <p>
	 * One lazy way is to using a wrapper class to include any data structure you want, but remember to include
	 * Necessary Annotations / getter / setter of your class.
	 * Some times it require @JsonCreator.. @JsonProperty etc ... see google!
	 *
	 * @param value
	 * @param typeReference
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public static <T> T readValueFromJSON(String value, TypeReference<T> typeReference) throws IOException {
		return objectMapperWithoutIndentation.readValue(value, typeReference);
	}

	/**
	 * Convert an object to json, human readable
	 *
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T> String toJSON(T value) {
		return toJSON(objectMapperWithIndentation, value);
	}

	/**
	 * Convert an object to  min-size json, human hardly readable
	 *
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T> String toCompactJSON(T value) {
		return toJSON(objectMapperWithoutIndentation, value);
	}


	private static <T> String toJSON(ObjectMapper objectMapper, T valueType) {
		try {
			return objectMapper.writeValueAsString(valueType);
		} catch (JsonProcessingException e) {
			System.err.println("Problem serializing: "+e.getMessage());
			return "Problem serializing " + valueType.getClass();
		}
	}

	private static ObjectMapper initWithIndentation() {
		return new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT)
				.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
	}

	private static ObjectMapper initWithoutIndentation() {
		return new ObjectMapper()
				.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
	}


	public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	public static Map deSerializeMap(String jsonInput, Class clazz) {
		Map obj = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = getCollectionType(mapper, HashMap.class, String.class, clazz);
			obj = mapper.readValue(jsonInput, javaType);
			return obj;
		} catch (Exception e) {
			System.err.println("Problem deserializing "+e.getMessage());
		}
		return obj;
	}

}
