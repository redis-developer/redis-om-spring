
package com.redis.om.spring.tuple;

import com.redis.om.spring.tuple.getter.TupleGetter;
import com.redis.om.spring.tuple.getter.TupleGetter0;
import com.redis.om.spring.tuple.getter.TupleGetter1;
import com.redis.om.spring.tuple.getter.TupleGetter10;
import com.redis.om.spring.tuple.getter.TupleGetter11;
import com.redis.om.spring.tuple.getter.TupleGetter12;
import com.redis.om.spring.tuple.getter.TupleGetter13;
import com.redis.om.spring.tuple.getter.TupleGetter2;
import com.redis.om.spring.tuple.getter.TupleGetter3;
import com.redis.om.spring.tuple.getter.TupleGetter4;
import com.redis.om.spring.tuple.getter.TupleGetter5;
import com.redis.om.spring.tuple.getter.TupleGetter6;
import com.redis.om.spring.tuple.getter.TupleGetter7;
import com.redis.om.spring.tuple.getter.TupleGetter8;
import com.redis.om.spring.tuple.getter.TupleGetter9;

/**
 * This interface defines a generic {@link Tuple} of degree 14 that can hold
 * non-null values. A Tuple is type safe, immutable and thread safe. For tuples
 * that can hold null elements see {@link OptionalTuple}. For mutable tuples see
 * {@link MutableTuple}
 *
 * This {@link Tuple} has a degree of 14
 * <p>
 *
 *
 * @param <T0>  type of element 0
 * @param <T1>  type of element 1
 * @param <T2>  type of element 2
 * @param <T3>  type of element 3
 * @param <T4>  type of element 4
 * @param <T5>  type of element 5
 * @param <T6>  type of element 6
 * @param <T7>  type of element 7
 * @param <T8>  type of element 8
 * @param <T9>  type of element 9
 * @param <T10> type of element 10
 * @param <T11> type of element 11
 * @param <T12> type of element 12
 * @param <T13> type of element 13
 *
 * @see Tuple
 * @see OptionalTuple
 * @see MutableTuple
 */
public interface Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> extends Tuple {

  /**
   * Returns the 0th element from this tuple.
   *
   * @return the 0th element from this tuple.
   */
  T0 get0();

  /**
   * Returns the 1st element from this tuple.
   *
   * @return the 1st element from this tuple.
   */
  T1 get1();

  /**
   * Returns the 2nd element from this tuple.
   *
   * @return the 2nd element from this tuple.
   */
  T2 get2();

  /**
   * Returns the 3rd element from this tuple.
   *
   * @return the 3rd element from this tuple.
   */
  T3 get3();

  /**
   * Returns the 4th element from this tuple.
   *
   * @return the 4th element from this tuple.
   */
  T4 get4();

  /**
   * Returns the 5th element from this tuple.
   *
   * @return the 5th element from this tuple.
   */
  T5 get5();

  /**
   * Returns the 6th element from this tuple.
   *
   * @return the 6th element from this tuple.
   */
  T6 get6();

  /**
   * Returns the 7th element from this tuple.
   *
   * @return the 7th element from this tuple.
   */
  T7 get7();

  /**
   * Returns the 8th element from this tuple.
   *
   * @return the 8th element from this tuple.
   */
  T8 get8();

  /**
   * Returns the 9th element from this tuple.
   *
   * @return the 9th element from this tuple.
   */
  T9 get9();

  /**
   * Returns the 10th element from this tuple.
   *
   * @return the 10th element from this tuple.
   */
  T10 get10();

  /**
   * Returns the 11st element from this tuple.
   *
   * @return the 11st element from this tuple.
   */
  T11 get11();

  /**
   * Returns the 12nd element from this tuple.
   *
   * @return the 12nd element from this tuple.
   */
  T12 get12();

  /**
   * Returns the 13rd element from this tuple.
   *
   * @return the 13rd element from this tuple.
   */
  T13 get13();

  @Override
  default int size() {
    return 14;
  }

  default Object get(int index) {
    switch (index) {
      case 0:
        return get0();
      case 1:
        return get1();
      case 2:
        return get2();
      case 3:
        return get3();
      case 4:
        return get4();
      case 5:
        return get5();
      case 6:
        return get6();
      case 7:
        return get7();
      case 8:
        return get8();
      case 9:
        return get9();
      case 10:
        return get10();
      case 11:
        return get11();
      case 12:
        return get12();
      case 13:
        return get13();
      default:
        throw new IndexOutOfBoundsException(
            String.format("Index %d is outside bounds of tuple of degree %s", index, size()));
    }
  }

  /**
   * Returns a {@link TupleGetter getter} for the 0th element in the {@code
   * Tuple}.
   *
   * @return the element at the 0th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter0<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T0> getter0() {
    return Quattuordecuple::get0;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 1st element in the {@code
   * Tuple}.
   *
   * @return the element at the 1st position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter1<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T1> getter1() {
    return Quattuordecuple::get1;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 2nd element in the {@code
   * Tuple}.
   *
   * @return the element at the 2nd position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter2<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T2> getter2() {
    return Quattuordecuple::get2;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 3rd element in the {@code
   * Tuple}.
   *
   * @return the element at the 3rd position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter3<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T3> getter3() {
    return Quattuordecuple::get3;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 4th element in the {@code
   * Tuple}.
   *
   * @return the element at the 4th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter4<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T4> getter4() {
    return Quattuordecuple::get4;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 5th element in the {@code
   * Tuple}.
   *
   * @return the element at the 5th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter5<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T5> getter5() {
    return Quattuordecuple::get5;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 6th element in the {@code
   * Tuple}.
   *
   * @return the element at the 6th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter6<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T6> getter6() {
    return Quattuordecuple::get6;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 7th element in the {@code
   * Tuple}.
   *
   * @return the element at the 7th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter7<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T7> getter7() {
    return Quattuordecuple::get7;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 8th element in the {@code
   * Tuple}.
   *
   * @return the element at the 8th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter8<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T8> getter8() {
    return Quattuordecuple::get8;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 9th element in the {@code
   * Tuple}.
   *
   * @return the element at the 9th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter9<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T9> getter9() {
    return Quattuordecuple::get9;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 10th element in the {@code
   * Tuple}.
   *
   * @return the element at the 10th position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter10<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T10> getter10() {
    return Quattuordecuple::get10;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 11st element in the {@code
   * Tuple}.
   *
   * @return the element at the 11st position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter11<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T11> getter11() {
    return Quattuordecuple::get11;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 12nd element in the {@code
   * Tuple}.
   *
   * @return the element at the 12nd position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter12<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T12> getter12() {
    return Quattuordecuple::get12;
  }

  /**
   * Returns a {@link TupleGetter getter} for the 13rd element in the {@code
   * Tuple}.
   *
   * @return the element at the 13rd position
   * @param <T0>  the 0th element type
   * @param <T1>  the 1st element type
   * @param <T2>  the 2nd element type
   * @param <T3>  the 3rd element type
   * @param <T4>  the 4th element type
   * @param <T5>  the 5th element type
   * @param <T6>  the 6th element type
   * @param <T7>  the 7th element type
   * @param <T8>  the 8th element type
   * @param <T9>  the 9th element type
   * @param <T10> the 10th element type
   * @param <T11> the 11st element type
   * @param <T12> the 12nd element type
   * @param <T13> the 13rd element type
   */
  static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> TupleGetter13<Quattuordecuple<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, T13> getter13() {
    return Quattuordecuple::get13;
  }
}