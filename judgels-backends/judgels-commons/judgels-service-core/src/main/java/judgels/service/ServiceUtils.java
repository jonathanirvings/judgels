package judgels.service;

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

public class ServiceUtils {
    private ServiceUtils() {}

    public static <T> T checkFound(Optional<T> obj) {
        return obj.orElseThrow(NotFoundException::new);
    }

    public static void checkAllowed(boolean allowed) {
        if (!allowed) {
            throw new ForbiddenException();
        }
    }

    public static void checkAllowed(Optional<String> reasonNotAllowed) {
        if (reasonNotAllowed.isPresent()) {
            throw new ForbiddenException(reasonNotAllowed.get());
        }
    }

    public static Response buildDownloadResponse(String fileUrl) {
        try {
            new URL(fileUrl);
            return Response.temporaryRedirect(URI.create(fileUrl)).build();
        } catch (MalformedURLException e) {
            File file = new File(fileUrl);
            if (!file.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(file)
                    .header(CONTENT_TYPE, "application/x-download")
                    .header(CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .build();
        }
    }

    public static Response buildImageResponse(String imageUrl, Optional<String> ifModifiedSince) {
        try {
            new URL(imageUrl);
            return Response.temporaryRedirect(URI.create(imageUrl)).build();
        } catch (MalformedURLException e) {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return buildImageResponse(imageFile, ifModifiedSince);
        }
    }

    public static Response buildImageResponse(File imageFile, Optional<String> ifModifiedSince) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (imageFile.lastModified() <= lastModified.getTime()) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, sdf.format(new Date(imageFile.lastModified())));

        try {
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                response.header(CONTENT_TYPE, URLConnection.guessContentTypeFromName(imageFile.getName()));
                response.entity(Files.toByteArray(imageFile));
                return response.build();
            }

            String type = Files.getFileExtension(imageFile.getAbsolutePath());
            return buildImageResponse(response, img, type);
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }

    public static Response buildImageResponse(
            InputStream stream,
            String type,
            Date lastModifiedStream,
            Optional<String> ifModifiedSince) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (ifModifiedSince.isPresent()) {
            try {
                Date lastModified = sdf.parse(ifModifiedSince.get());
                if (lastModifiedStream.getTime() <= lastModified.getTime()) {
                    return Response.notModified().build();
                }
            } catch (ParseException e2) {
                // nothing
            }
        }

        Response.ResponseBuilder response = Response.ok();
        response.header(CACHE_CONTROL, "no-transform,public,max-age=300,s-maxage=900");
        response.header(LAST_MODIFIED, lastModifiedStream);

        try {
            BufferedImage img = ImageIO.read(stream);
            if (img == null) {
                response.header(CONTENT_TYPE, "image/" + type);
                response.entity(ByteStreams.toByteArray(stream));
                return response.build();
            }

            return buildImageResponse(response, img, type);
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }

    private static Response buildImageResponse(Response.ResponseBuilder response, BufferedImage img, String type) {
        try {
            response.header(CONTENT_TYPE, "image/" + type);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, type, baos);
            response.entity(baos.toByteArray());

            return response.build();
        } catch (IOException e2) {
            return Response.serverError().build();
        }
    }
}
