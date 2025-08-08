package com.okbasalman.product_service.adapter.input.grpc;

import com.google.protobuf.Empty;
import com.okbasalman.grpc.CreateProductRequest;
import com.okbasalman.grpc.DecreaseStockRequest;
import com.okbasalman.grpc.DeleteProductRequest;
import com.okbasalman.grpc.DeleteProductResponse;
import com.okbasalman.grpc.GetProductByIdRequest;
import com.okbasalman.grpc.ProductImageResponse;
import com.okbasalman.grpc.ProductListResponse;
import com.okbasalman.grpc.ProductResponse;
import com.okbasalman.grpc.ProductServiceGrpc.ProductServiceImplBase;
import com.okbasalman.grpc.ProductVariantResponse;
import com.okbasalman.grpc.UpdateProductRequest;
import com.okbasalman.product_service.domain.dto.DeleteProductResultDto;
import com.okbasalman.product_service.domain.dto.ProductCreateDto;
import com.okbasalman.product_service.domain.dto.ProductVariantCreateDto;
import com.okbasalman.product_service.domain.model.Product;
import com.okbasalman.product_service.domain.model.ProductImage;
import com.okbasalman.product_service.domain.model.ProductVariant;
import com.okbasalman.product_service.domain.model.Season;
import com.okbasalman.product_service.domain.model.Size;
import com.okbasalman.product_service.domain.port.input.ProductUseCase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

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
            ProductResponse response = mapToProductResponse(product);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ProductListResponse> responseObserver) {
        try {
            List<Product> products = productUseCase.getAllProducts();
            ProductListResponse response = ProductListResponse.newBuilder()
                    .addAllProducts(products.stream().map(this::mapToProductResponse).collect(Collectors.toList()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            // Validation
            if (request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required.");
            }
            if (request.getVariantsList() == null || request.getVariantsList().isEmpty()) {
                throw new IllegalArgumentException("At least one product variant is required.");
            }
            // Map gRPC request to domain DTO
            ProductCreateDto dto = mapToProductCreateDto(request);

            // Call domain use case
            Product createdProduct = productUseCase.createProduct(dto);

            // Map domain model to gRPC response
            ProductResponse response = mapToProductResponse(createdProduct);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            // Validation
            if (request.getId() == 0) {
                throw new IllegalArgumentException("Product ID is required for update.");
            }
            if (request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required.");
            }
            if (request.getVariantsList() == null || request.getVariantsList().isEmpty()) {
                throw new IllegalArgumentException("At least one product variant is required.");
            }

            // Map gRPC request to domain model
            Product productToBeUpdated = mapToProduct(request);

            // Call domain use case
            Product updatedProduct = productUseCase.updateProduct(productToBeUpdated);

            ProductResponse response = mapToProductResponse(updatedProduct);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        try {
            DeleteProductResultDto deleteResult = productUseCase.deleteProduct(request.getId());
            if (!deleteResult.isSuccess()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription(deleteResult.getMessage()).asRuntimeException());
                return;
            }
            DeleteProductResponse response = DeleteProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage(deleteResult.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void decreaseStock(DecreaseStockRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            if (request.getProductVariantId() == 0) {
                throw new IllegalArgumentException("Product variant ID is required.");
            }
            if (request.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }

            Product updatedProduct = productUseCase.decreaseStock(request.getProductVariantId(), request.getQuantity());

            ProductResponse response = mapToProductResponse(updatedProduct);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    //region Mappers
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setSeason(product.getSeason().name())
                .addAllVariants(product.getVariants().stream()
                        .map(this::mapToProductVariantResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private ProductVariantResponse mapToProductVariantResponse(ProductVariant variant) {
        return ProductVariantResponse.newBuilder()
                .setId(variant.getId())
                .setPrice(variant.getPrice())
                .setStock(variant.getStock())
                .setColor(variant.getColor())
                .setSize(variant.getSize().name())
                .addAllImages(variant.getImages().stream()
                        .map(this::mapToProductImageResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private ProductImageResponse mapToProductImageResponse(ProductImage image) {
        return ProductImageResponse.newBuilder()
                .setId(image.getId())
                .setBase64Data(image.getBase64Data())
                .build();
    }

    private ProductCreateDto mapToProductCreateDto(CreateProductRequest request) {
        System.out.println("GRPC Controller: Mapping CreateProductRequest for product: " + request.getName());

        ProductCreateDto dto = new ProductCreateDto();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setSeason(Season.valueOf(request.getSeason()));
        dto.setVariants(request.getVariantsList().stream()
                .map(variantRequest -> {
                    ProductVariantCreateDto variantDto = new ProductVariantCreateDto();
                    variantDto.setPrice(variantRequest.getPrice());
                    variantDto.setStock(variantRequest.getStock());
                    variantDto.setColor(variantRequest.getColor());
                    variantDto.setSize(Size.valueOf(variantRequest.getSize()));
                    variantDto.setBase64Images(variantRequest.getBase64ImagesList());

                    System.out.println("GRPC Controller: Variant with color " + variantDto.getColor() + " has " + variantDto.getBase64Images().size() + " images.");

                    return variantDto;
                }).collect(Collectors.toList()));
        return dto;
    }

    private Product mapToProduct(UpdateProductRequest request) {
        Product product = new Product();
        product.setId(request.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSeason(Season.valueOf(request.getSeason()));
        product.setVariants(request.getVariantsList().stream()
                .map(variantRequest -> {
                    ProductVariant variant = new ProductVariant();
                    variant.setId(variantRequest.getId());
                    variant.setPrice(variantRequest.getPrice());
                    variant.setStock(variantRequest.getStock());
                    variant.setColor(variantRequest.getColor());
                    variant.setSize(Size.valueOf(variantRequest.getSize()));
                    variant.setImages(variantRequest.getImagesList().stream()
                            .map(imageRequest -> new ProductImage(imageRequest.getId(), imageRequest.getBase64Data()))
                            .collect(Collectors.toList()));
                    return variant;
                }).collect(Collectors.toList()));
        return product;
    }
    //endregion
}