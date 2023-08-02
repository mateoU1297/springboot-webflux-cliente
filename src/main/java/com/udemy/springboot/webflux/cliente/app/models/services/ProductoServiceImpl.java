package com.udemy.springboot.webflux.cliente.app.models.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.udemy.springboot.webflux.cliente.app.models.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

	@Autowired
	private WebClient client;

	@Override
	public Flux<Producto> findAll() {
		return client.get().exchange().flatMapMany(response -> response.bodyToFlux(Producto.class));
	}

	@Override
	public Mono<Producto> findById(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return client.get().uri("/{id}", params).retrieve().bodyToMono(Producto.class);
		// .exchange()
		// .flatMap(response -> response.bodyToMono(Producto.class));
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		return client.post().syncBody(producto).retrieve().bodyToMono(Producto.class);
	}

	@Override
	public Mono<Producto> update(Producto producto, String id) {
		return client.put().uri("/{id}", Collections.singletonMap("id", id)).syncBody(producto).retrieve()
				.bodyToMono(Producto.class);
	}

	@Override
	public Mono<Void> delete(String id) {
		return client.delete().uri("/{id}", Collections.singletonMap("id", id)).retrieve().bodyToMono(Void.class);
	}

	@Override
	public Mono<Producto> upload(FilePart file, String id) {
		MultipartBodyBuilder parts = new MultipartBodyBuilder();
		parts.asyncPart("file", file.content(), DataBuffer.class).headers(h -> {
			h.setContentDispositionFormData("file", file.filename());
		});

		return client.post().uri("/upload/{id}", Collections.singletonMap("id", id))
				.contentType(MediaType.MULTIPART_FORM_DATA).syncBody(parts.build()).retrieve()
				.bodyToMono(Producto.class);
	}

}
