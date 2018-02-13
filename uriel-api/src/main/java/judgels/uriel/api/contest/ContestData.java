package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestData.class)
public interface ContestData {
    String getName();
    String getDescription();
    ContestStyle getStyle();

    class Builder extends ImmutableContestData.Builder {}
}
