package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
@Component
public class FileStore implements BlobStore {


    @Override
    public void put(Blob blob) throws IOException {

        try {
            MinioClient minioClient = new MinioClient("http://127.0.0.1:9000", "QXO9LG35SVLGRXJNDZEW",
                    "OnMulqnxXrCumKQ8x6579aBGEXgdVsZ+N2Nc+tW+");
            minioClient.putObject("moviefun",blob.name,blob.inputStream,blob.contentType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Blob blob = null;
        Optional<Blob> blobOptional = null;
        try {
            MinioClient minioClient = new MinioClient("http://127.0.0.1:9000", "QXO9LG35SVLGRXJNDZEW",
                    "OnMulqnxXrCumKQ8x6579aBGEXgdVsZ+N2Nc+tW+");
            InputStream is = minioClient.getObject("moviefun",name);
            blob = new Blob(name,is,new Tika().detect(is));
            blobOptional = Optional.of(blob);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return blobOptional;
    }

    @Override
    public void deleteAll() {
        try {
            MinioClient minioClient = new MinioClient("http://127.0.0.1:9000", "QXO9LG35SVLGRXJNDZEW",
                    "OnMulqnxXrCumKQ8x6579aBGEXgdVsZ+N2Nc+tW+");
        minioClient.removeBucket("moviefun");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}