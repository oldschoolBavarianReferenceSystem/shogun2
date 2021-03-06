package de.terrestris.shogun2.util.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Nils Bühner
 * @author terrestris GmbH & Co. KG
 *
 */
public class EntityUtil {

	/**
	 * @param clazz
	 * @param fieldName
	 * @param fieldEntityType
	 * @param forceAccess
	 * @return
	 */
	public static boolean isField(Class<?> clazz, String fieldName, Class<?> fieldEntityType, boolean forceAccess) {
		Field field = FieldUtils.getField(clazz, fieldName, forceAccess);
		if (field == null) {
			return false;
		}

		final Class<?> fieldType = field.getType();

		// we'll also return true if the fieldEntityType is null, i.e. "unknown"
		if(fieldEntityType == null || fieldType.isAssignableFrom(fieldEntityType)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks whether the given <code>fieldName</code> in <code>clazz</code> is
	 * a collection field with elements of type
	 * <code>collectionElementType</code>.
	 *
	 * @param clazz
	 *            The class to check for the given collection field
	 * @param fieldName
	 *            The name of the collection field
	 * @param collectionElementType
	 *            The type of the concrete element in the collection
	 * @param forceAccess
	 *            whether to break scope restrictions using the
	 *            {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
	 *            method. {@code false} will only match {@code public} fields.
	 *
	 * @return Whether or not the given <code>fieldName</code> in
	 *         <code>clazz</code> is a collection field with elements of type
	 *         <code>collectionElementType</code>.
	 */
	public static boolean isCollectionField(Class<?> clazz, String fieldName, Class<?> collectionElementType,
			boolean forceAccess) {
		Field field = FieldUtils.getField(clazz, fieldName, forceAccess);
		if (field == null) {
			return false;
		}
		boolean isCollectionField = false;

		if (Collection.class.isAssignableFrom(field.getType())) {
			ParameterizedType collType = (ParameterizedType) field.getGenericType();
			Class<?> elementTypeOfCollection = (Class<?>) collType.getActualTypeArguments()[0];
			isCollectionField = elementTypeOfCollection.isAssignableFrom(collectionElementType);
		}

		return isCollectionField;
	}

	/**
	 * This method returns a multi value map, where the keys are the
	 * intersection of (non-static private) field names of the given entity
	 * class and the given set of requested/input field names (which are the
	 * keys of the passed multi value map), i.e. fields in the input that are
	 * not present in the entity model definition will be removed/ignored in
	 * this method.
	 *
	 * Regarding case sensitivity, the field names that are building the keys of
	 * the result map are used in their original representation/definition from
	 * the entity model, but the keys/fieldnames in the input may be
	 * case-insensitive.
	 *
	 * The value for each key is a list of casted (!) values of the string
	 * values of the given input map. The type of the field in the entity is
	 * used to determine the correct casting.
	 *
	 *
	 * @param requestedFilter
	 * @param entityClass
	 * @return
	 */
	public static MultiValueMap<String, Object> validFieldNamesWithCastedValues(MultiValueMap<String, String> requestedFilter, Class<?> entityClass) {

		Set<String> inputFieldNames = requestedFilter.keySet();

		List<Field> allFields = FieldUtils.getAllFieldsList(entityClass);

		// Regarding case insensitivity: build a map that maps from the input field name to its original field name,
		// but add only those fields to the map that exist in the entity
		Map<String, String> validInputFieldNameToOrigFieldName = new HashMap<>();

		for (Field field : allFields) {
			final Class<?> fieldType = field.getType();
			final String fieldName = field.getName();
			final int fieldModifiers = field.getModifiers();

			final boolean isPrimitiveOrWrapper = ClassUtils.isPrimitiveOrWrapper(fieldType);
			final boolean isString = fieldType.equals(String.class);
			final boolean isStatic = Modifier.isStatic(fieldModifiers);
			final boolean isPrivate = Modifier.isPrivate(fieldModifiers);

			// extract only non-static private fields that are primitive or
			// primitive wrapper types or String
			if((isPrimitiveOrWrapper || isString) && isPrivate && !isStatic) {

				// find the corresponding field in the input
				for(String inputFieldName : inputFieldNames) {
					if(fieldName.toLowerCase().equals(inputFieldName.toLowerCase())) {
						validInputFieldNameToOrigFieldName.put(inputFieldName, fieldName);
						break;
					}
				}
			}
		}

		MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();

		Set<String> validInputFieldNames = validInputFieldNameToOrigFieldName.keySet();

		for (String validInputFieldName : validInputFieldNames) {
			String origInputFieldName = validInputFieldNameToOrigFieldName.get(validInputFieldName);

			List<String> stringValues = requestedFilter.get(validInputFieldName);

			// cast to the correct type to avoid hibernate exceptions when querying db or similar
			for (String fieldStringValue : stringValues) {
					Field f = FieldUtils.getField(entityClass, origInputFieldName, true);
					Class<?> fieldType = f.getType();
					Object castedValue = ConvertUtils.convert(fieldStringValue, fieldType);
					result.add(origInputFieldName, castedValue);
			}
		}

		return result ;
	}
}
