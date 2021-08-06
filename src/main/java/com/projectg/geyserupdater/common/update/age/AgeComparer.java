package com.projectg.geyserupdater.common.update.age;

import com.projectg.geyserupdater.common.update.age.provider.AgeProvider;
import com.projectg.geyserupdater.common.update.age.type.Age;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @param <T> The external {@link AgeProvider} implementation
 * @param <S> The {@link Age} implementation
 */
public class AgeComparer<T extends AgeProvider<S>, S extends Age<?>> {

    S localAge;
    T externalAgeProvider;

    /**
     * Create an age comparer, which stores a local age and way to check an external age. Both params should provide the same {@link Age} implementation.
     * @param localAgeProvider The local age provider. {@link AgeProvider#getAge()} will be called once and stored forever.
     * @param externalAgeProvider The external age provider. {@link AgeProvider#getAge()} will be called every time {@link AgeComparer#checkIfEquals()} is called.
     */
    public <U extends AgeProvider<S>> AgeComparer(@Nonnull U localAgeProvider, @Nonnull T externalAgeProvider) {
        Objects.requireNonNull(localAgeProvider);
        Objects.requireNonNull(externalAgeProvider);

        this.localAge = localAgeProvider.getAge();
        this.externalAgeProvider = externalAgeProvider;
    }

    /**
     * Create an age comparer, which stores a local age and way to check an external age
     * @param localAge The local age.
     * @param externalAgeProvider The external age provider. {@link AgeProvider#getAge()} will be called every time {@link AgeComparer#checkIfEquals()} is called.
     */
    public AgeComparer(@Nonnull S localAge, @Nonnull T externalAgeProvider) {
        Objects.requireNonNull(localAge);
        Objects.requireNonNull(externalAgeProvider);

        this.localAge = localAge;
        this.externalAgeProvider = externalAgeProvider;
    }

    /**
     * Request the {@link Age} from the external {@link AgeProvider} and compare it to the stored local {@link Age}.
     * @return True if the local age {@link Object#equals(Object)} the external age
     */
    public boolean checkIfEquals() {
        return localAge.value().equals(externalAgeProvider.getAge());
    }
}
