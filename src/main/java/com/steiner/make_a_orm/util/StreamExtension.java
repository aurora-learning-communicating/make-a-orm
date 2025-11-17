package com.steiner.make_a_orm.util;

import jakarta.annotation.Nonnull;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamExtension {
    public static <A, B, C> Stream<C> zip(
            @Nonnull Stream<? extends A> streamLeft,
            @Nonnull Stream<? extends B> streamRight,
            @Nonnull BiFunction<? super A, ? super B, ? extends C> zipper) {
        Spliterator<? extends A> spliteratorLeft = streamLeft.spliterator();
        Spliterator<? extends B> spliteratorRight = streamRight.spliterator();

        int characteristics = spliteratorLeft.characteristics() & spliteratorRight.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED);


        long zipSize = -1;
        if ((characteristics & Spliterator.SIZED) != 0) {
            zipSize = Math.min(spliteratorLeft.getExactSizeIfKnown(), spliteratorRight.getExactSizeIfKnown());
        }

        Iterator<A> iteratorLeft = Spliterators.iterator(spliteratorLeft);
        Iterator<B> iteratorRight = Spliterators.iterator(spliteratorRight);
        Iterator<C> iteratorResult = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return iteratorLeft.hasNext() && iteratorRight.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(iteratorLeft.next(), iteratorRight.next());
            }
        };

        Spliterator<C> spliterator = Spliterators.spliterator(iteratorResult, zipSize, characteristics);
        if (streamLeft.isParallel() || streamRight.isParallel()) {
            return StreamSupport.stream(spliterator, true);
        } else {
            return StreamSupport.stream(spliterator, false);
        }
    }

    public static <T> void forEachIndexed(@Nonnull List<T> list, @Nonnull BiConsumer<? super T, Integer> biConsumer) {
        Iterator<T> iterator = list.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            biConsumer.accept(iterator.next(), index);
            index += 1;
        }
    }

    public static <T> void forEachIndexedThrows(@Nonnull List<T> list, @Nonnull BiConsumerThrows<T, Integer> biConsumerThrows) throws SQLException {
        Iterator<T> iterator = list.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            biConsumerThrows.accept(iterator.next(), index);
            index += 1;
        }
    }
}
