package dev.projectg.geyserupdater.common.update.identity;

import dev.projectg.geyserupdater.common.update.identity.provider.IdentityProvider;
import dev.projectg.geyserupdater.common.update.identity.type.Identity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @param <T> The external {@link IdentityProvider} implementation
 * @param <S> The {@link Identity} implementation
 */
public class IdentityComparer<U extends IdentityProvider<S>, T extends IdentityProvider<S>, S extends Identity<?>> {

    private final @Nullable S localIdentity;
    private final @Nullable U localIdentityProvider;
    private final @Nonnull T externalIdentityProvider;

    /**
     * Create an identity comparer, which stores a way to check a local identity and way to check an external identity. Both params should provide the same {@link Identity} implementation.
     * @param localIdentityProvider The local identity provider. {@link IdentityProvider#getValue()}
     * @param externalIdentityProvider The external identity provider. {@link IdentityProvider#getValue()}
     */
    public IdentityComparer(@Nonnull U localIdentityProvider, @Nonnull T externalIdentityProvider) {
        Objects.requireNonNull(localIdentityProvider);
        Objects.requireNonNull(externalIdentityProvider);

        this.localIdentity = null;
        this.localIdentityProvider = localIdentityProvider;
        this.externalIdentityProvider = externalIdentityProvider;
    }

    /**
     * Create an identity comparer, which stores a local identity and way to check an external identity.
     * @param localIdentity The local identity.
     * @param externalIdentityProvider The external identity provider. {@link IdentityProvider#getValue()} will be called every time {@link IdentityComparer#checkIfEquals()} is called.
     */
    public IdentityComparer(@Nonnull S localIdentity, @Nonnull T externalIdentityProvider) {
        Objects.requireNonNull(localIdentity);
        Objects.requireNonNull(externalIdentityProvider);

        this.localIdentity = localIdentity;
        this.localIdentityProvider = null;
        this.externalIdentityProvider = externalIdentityProvider;
    }

    /**
     * Request the {@link Identity} from the external {@link IdentityProvider} and compare it to the stored local {@link Identity}. Should be regarded as a blocking operation.
     * @return True if the local identity {@link Object#equals(Object)} the external identity. Also returns true if either are null.
     */
    public boolean checkIfEquals() {
        // todo: return an enum or wrapper, to better deal with null values
        Object localValue = callLocalValue();
        Object externalValue = callExternalValue();
        if (localValue == null || externalValue == null) {
            return true;
        }

        return localValue.equals(externalValue);
    }

    @Nullable
    public Object callLocalValue() {
        if (localIdentity == null) {
            S localId = Objects.requireNonNull(localIdentityProvider).getValue();
            if (localId == null) {
                return null;
            } else {
                return localId.value();
            }
        } else {
            return localIdentity.value();
        }
    }

    @Nullable
    public Object callExternalValue() {
        S externalId = externalIdentityProvider.getValue();
        if (externalId == null) {
            return null;
        } else {
            return externalId.value();
        }
    }
}
