package com.mehdi.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;


import javax.websocket.server.ServerEndpoint;
import java.nio.file.Path;
import java.util.stream.Stream;
@Service
public interface StorageService {


        void init() throws Exception;

        void store(MultipartFile file);

        Stream<Path> loadAll() throws IOException;

        Path load(String filename);

        Resource loadAsResource(String filename) throws Exception;

        void deleteAll();

    }

