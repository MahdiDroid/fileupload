package com.mehdi.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import com.mehdi.demo.conf.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageImpl implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
               // throw new StorageException("Failed to store empty file " + filename);
                System.out.println("file is empy");
            }
            if (filename.contains("..")) {
                // This is a security check
//                throw new StorageException(
//                        "Cannot store file with relative path outside current directory "
//                                + filename);
                System.out.println("cannot store");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            //throw new StorageException("Failed to store file " + filename, e);
            System.out.println("failed to store file" + filename);
        }
    }

    @Override
    public Stream<Path> loadAll() throws IOException {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new IOException("Failed to read stored files", e);
            //System.out.println("fail to read");
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) throws Exception {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new Exception();
//                        "Could not read file: " + filename);
               // System.out.println("could not read" + filename);

            }
        }
        catch (MalformedURLException e) {
           throw new Exception("malformed");
            //System.out.println("could not read");
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() throws Exception {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new Exception("Could not initialize storage", e);
           // System.out.println("could not initilize ");
        }
    }
}