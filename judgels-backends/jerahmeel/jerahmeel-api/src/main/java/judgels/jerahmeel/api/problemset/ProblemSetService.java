package judgels.jerahmeel.api.problemset;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/problemsets")
public interface ProblemSetService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ProblemSetsResponse getProblemSets(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("archiveSlug") Optional<String> archiveSlug,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") Optional<Integer> page);

    @GET
    @Path("/{problemSetJid}/stats")
    @Produces(APPLICATION_JSON)
    ProblemSetStatsResponse getProblemSetStats(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetJid") String problemSetJid);

    @GET
    @Path("/slug/{problemSetSlug}")
    @Produces(APPLICATION_JSON)
    ProblemSet getProblemSetBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("problemSetSlug") String problemSetSlug);
}
