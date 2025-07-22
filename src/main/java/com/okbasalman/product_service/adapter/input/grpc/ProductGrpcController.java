package com.okbasalman.product_service.adapter.input.grpc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.grpc.server.service.GrpcService;

import com.okbasalman.grpc.CreateProductRequest;
import com.okbasalman.grpc.DecreaseStockRequest;
import com.okbasalman.grpc.DeleteProductRequest;
import com.okbasalman.grpc.DeleteProductResponse;
import com.okbasalman.grpc.Empty;
import com.okbasalman.grpc.GetProductByIdRequest;
import com.okbasalman.grpc.ProductListResponse;
import com.okbasalman.grpc.ProductResponse;
import com.okbasalman.grpc.UpdateProductRequest;
import com.okbasalman.grpc.ProductServiceGrpc.ProductServiceImplBase;
import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.model.Season;
import com.okbasalman.product_service.domain.model.Size;
import com.okbasalman.product_service.domain.port.input.ProductUseCase;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;

@GrpcService
public class ProductGrpcController extends ProductServiceImplBase {
    private final ProductUseCase productUseCase;

    

    public ProductGrpcController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @Override
    public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            Product product = productUseCase.getProductById(request.getId());
            
            ProductResponse response = ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setStock(product.getStock())
                .addAllImagesUrls(List.of(product.getImagesUrls()))
                .setColor(product.getColor())
                .setSize(product.getSize().name())
                .setSeason(product.getSeason().name())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (RuntimeException e) {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ProductListResponse> responseObserver) {
        try {
            List<Product> products = productUseCase.getAllProducts();
            ProductListResponse response = ProductListResponse.newBuilder()
                .addAllProducts(products.stream()
                    .map(p -> ProductResponse.newBuilder()
                        .setId(p.getId())
                        .setName(p.getName())
                        .setPrice(p.getPrice())
                        .setStock(p.getStock())
                        .addAllImagesUrls(List.of(p.getImagesUrls()))
                        .setColor(p.getColor())
                        .setSize(p.getSize().name())
                        .setSeason(p.getSeason().name())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            if (!request.hasName() || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required.");
            }
            if (!request.hasPrice()) {
                throw new IllegalArgumentException("Product price is required.");
            }
            if (request.getPrice() <= 0) {
                throw new IllegalArgumentException("Product price must be greater than zero.");
            }
            if (!request.hasStock()) {
                throw new IllegalArgumentException("Product stock is required.");
            }
            if (request.getStock() < 0) {
                throw new IllegalArgumentException("Product stock cannot be negative.");
            }
            if (request.getImagesUrlsList() == null || request.getImagesUrlsList().isEmpty()) {
                throw new IllegalArgumentException("At least one image URL is required.");
            }
            if (!request.hasColor()) {
                throw new IllegalArgumentException("Product color is required.");
            }
            if (!request.hasSize()) {
                throw new IllegalArgumentException("Product size is required.");
            }
            if (!request.hasSeason()) {
                throw new IllegalArgumentException("Product season is required.");
            }
            ProductCreateDto productToBeCreated = new ProductCreateDto(
                request.getName(),
                request.getPrice(),
                request.getStock(),
                request.getImagesUrlsList().toArray(new String[0]),
                request.getColor(),
                Size.valueOf(request.getSize()),
                Season.valueOf(request.getSeason())
            );
            Product createdProduct = productUseCase.createProduct(productToBeCreated);
            ProductResponse response = ProductResponse.newBuilder()
                .setId(createdProduct.getId())
                .setName(createdProduct.getName())
                .setPrice(createdProduct.getPrice())
                .setStock(createdProduct.getStock())
                .addAllImagesUrls(List.of(createdProduct.getImagesUrls()))
                .setColor(createdProduct.getColor())
                .setSize(createdProduct.getSize().name())
                .setSeason(createdProduct.getSeason().name())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            if (!request.hasId()) {
                throw new IllegalArgumentException("Product ID is required for update.");
            }
            if (!request.hasName() || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required.");
            }
            if (!request.hasPrice()) {
                throw new IllegalArgumentException("Product price is required.");
            }
            if (request.getPrice() <= 0) {
                throw new IllegalArgumentException("Product price must be greater than zero.");
            }
            if (!request.hasStock()) {
                throw new IllegalArgumentException("Product stock is required.");
            }
            if (request.getStock() < 0) {
                throw new IllegalArgumentException("Product stock cannot be negative.");
            }
            if (request.getImagesUrlsList() == null || request.getImagesUrlsList().isEmpty()) {
                throw new IllegalArgumentException("At least one image URL is required.");
            }
            if (!request.hasColor()) {
                throw new IllegalArgumentException("Product color is required.");
            }
            if (!request.hasSize()) {
                throw new IllegalArgumentException("Product size is required.");
            }
            if (!request.hasSeason()) {
                throw new IllegalArgumentException("Product season is required.");
            }  
            Product productToBeUpdated = new Product(
                request.getId(),
                request.getName(),
                request.getPrice(),
                request.getStock(),
                request.getImagesUrlsList().toArray(new String[0]),
                request.getColor(),
                Size.valueOf(request.getSize()),
                Season.valueOf(request.getSeason())
            );
            Product updatedProduct = productUseCase.updateProduct(productToBeUpdated);
            ProductResponse response = ProductResponse.newBuilder()
                .setId(updatedProduct.getId())
                .setName(updatedProduct.getName())
                .setPrice(updatedProduct.getPrice())
                .setStock(updatedProduct.getStock())
                .addAllImagesUrls(List.of(updatedProduct.getImagesUrls()))
                .setColor(updatedProduct.getColor())
                .setSize(updatedProduct.getSize().name())
                .setSeason(updatedProduct.getSeason().name())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (RuntimeException e) {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        try {
            DeleteProductResultDto deleteMessage = productUseCase.deleteProduct(request.getId());
            if (!deleteMessage.isSuccess()) {
                responseObserver.onError(
                    Status.NOT_FOUND.withDescription(deleteMessage.getMessage()).asRuntimeException()
                );
                return;
            }
            DeleteProductResponse response = DeleteProductResponse.newBuilder()
                .setSuccess(true)
                .setMessage(deleteMessage.getMessage())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void decreaseStock(DecreaseStockRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            Product product = productUseCase.decreaseStock(request.getProductId(), request.getQuantity());
            ProductResponse response = ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setStock(product.getStock())
                .addAllImagesUrls(List.of(product.getImagesUrls()))
                .setColor(product.getColor())
                .setSize(product.getSize().name())
                .setSeason(product.getSeason().name())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (RuntimeException e) {
            responseObserver.onError(
                Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }
}
