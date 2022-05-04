package software.amazon.amplifyuibuilder.common;

import java.util.*;
import java.util.stream.Collectors;

public class Transformer {
  /**
   * Transforms elements of collection with given function and returns list with transformed elements
   *
   * @param collection Collection to transform
   * @param function   function used to transformList elements of collection
   * @param <T>        Type of elements returned by the function
   * @param <K>        Type of the elements in the collection to be transformed
   * @return List of transformed elements
   */
  public static <T, K> List<T> transformList(Collection<K> collection, final java.util.function.Function<K, T> function) {
    return Optional.ofNullable(collection).orElse(Collections.emptyList()).stream().map(function).collect(Collectors.toList());
  }

  public static <T, K> Map<String, T> transformMap(Map<String, K> collection, final java.util.function.Function<K, T> function) {
    return Optional.ofNullable(collection).orElse(Collections.emptyMap()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> function.apply(e.getValue())));
  }

  public static <T, K> K transformObj(T obj, java.util.function.Function<T, K> function) {
    return obj == null ? null : function.apply(obj);
  }
}
