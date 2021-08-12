package com.projectg.geyserupdater.common.update.age;

import com.projectg.geyserupdater.common.update.age.provider.IdentityProvider;
import com.projectg.geyserupdater.common.update.age.type.Identity;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @param <T> The external {@link IdentityProvider} implementation
 * @param <S> The {@link Identity} implementation
 */
public class IdentityComparer<T extends IdentityProvider<S>, S extends Identity<?>> {

    S localIdentity;
    T externalIdentityProvider;

    /**
     * Create an identity comparer, which stores a local identity and way to check an external identity. Both params should provide the same {@link Identity} implementation.
     * @param localIdentityProvider The local identity provider. {@link IdentityProvider#getValue()} will be called once and stored forever.
     * @param externalIdentityProvider The external identity provider. {@link IdentityProvider#getValue()} will be called every time {@link IdentityComparer#checkIfEquals()} is called.
     */
    public <U extends IdentityProvider<S>> IdentityComparer(@Nonnull U localIdentityProvider, @Nonnull T externalIdentityProvider) {
        Objects.requireNonNull(localIdentityProvider);
        Objects.requireNonNull(externalIdentityProvider);

        this.localIdentity = localIdentityProvider.getValue();
        this.externalIdentityProvider = externalIdentityProvider;
    }

    /**
     * Create an identity comparer, which stores a local identity and way to check an external identity
     * @param localIdentity The local identity.
     * @param externalIdentityProvider The external identity provider. {@link IdentityProvider#getValue()} will be called every time {@link IdentityComparer#checkIfEquals()} is called.
     */
    public IdentityComparer(@Nonnull S localIdentity, @Nonnull T externalIdentityProvider) {
        Objects.requireNonNull(localIdentity);
        Objects.requireNonNull(externalIdentityProvider);

        this.localIdentity = localIdentity;
        this.externalIdentityProvider = externalIdentityProvider;
    }

    /**
     * Request the {@link Identity} from the external {@link IdentityProvider} and compare it to the stored local {@link Identity}.
     * @return True if the local identity {@link Object#equals(Object)} the external identity
     */
    public boolean checkIfEquals() {
        return localIdentity.value().equals(externalIdentityProvider.getValue());
    }
}
