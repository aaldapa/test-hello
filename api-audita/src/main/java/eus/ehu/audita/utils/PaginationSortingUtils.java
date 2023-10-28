package eus.ehu.audita.utils;

import java.lang.reflect.Field;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import eus.ehu.audita.exceptions.NoValidSortByFieldException;

public abstract class PaginationSortingUtils {
	
	private PaginationSortingUtils() {}

	public static PageRequest createPageRequest(Class<?> c, int numPage, int pageSize, String sortBy, Boolean asc) {
		
		checkSortByAplicable(c, sortBy);
		
		PageRequest pageable = PageRequest.of(numPage, pageSize);
		if (sortBy != null) {
			String[] sortBys = sortBy.split(",");
			Sort sort = Sort.by(sortBys);
			if (asc != null && !asc) {
				sort = sort.descending();
			}
			pageable = pageable.withSort(sort);
		}
		return pageable;
	}
	
	/**
	 * Comprueba si el atributo pasado como parametro existe en la clase
	 * @param c Clase sobre la que se quiere comprobar si tiene los atributos
	 * @param sortBy String que representa el atributo. Puede referenciar a una nested property. Ejemplo: "atributo.subatributo.subsubatributo"
	 */
	public static void checkSortByAplicable(Class<?> c, String sortBy) {
		if (sortBy != null) {
			String[] sortBys = sortBy.split(",");
			for (String eachSortBy : sortBys) {
				int pointPosition = eachSortBy.indexOf('.');
				String nextAttributeName = pointPosition >= 0 ? eachSortBy.substring(0, pointPosition) : eachSortBy;
				try {
					Field atributo = c.getDeclaredField(nextAttributeName);
	
					if (pointPosition > 0 ) { // condicion de seguir: si hay más '.', no hay que comprobar más atributos
						checkSortByAplicable(atributo.getType(), eachSortBy.substring(pointPosition + 1));
					}
				} catch (NoSuchFieldException | SecurityException e) {
					throw new NoValidSortByFieldException(sortBy);
				}
			}
		}
			
	}
	
	
}
