package com.okbasalman.product_service.adapter.input.grpc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.grpc.server.service.GrpcService;

import com.okbasalman.grpc.CreateProductRequest;
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
import com.okbasalman.product_service.domain.port.input.ProductUseCase;

import io.grpc.stub.StreamObserver;


@GrpcService
public class ProductGrpcController extends ProductServiceImplBase{
    private final ProductUseCase productUseCase;

    public ProductGrpcController(ProductUseCase productUseCase){
        this.productUseCase = productUseCase;
    }

    @Override
    public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responObserver){
        Product product = productUseCase.getProductById(request.getId());

        ProductResponse response = ProductResponse.newBuilder().setId(product.getId()).setName(product.getName()).setPrice(product.getPrice()).setStock(product.getStock()).build();

        responObserver.onNext(response);
        responObserver.onCompleted();
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ProductListResponse> responseObserver){  
     List<Product> products = productUseCase.getAllProducts();
     
     ProductListResponse response = ProductListResponse.newBuilder().addAllProducts(products.stream().map(p -> ProductResponse.newBuilder().setId(p.getId()).setName(p.getName()).setPrice(p.getPrice()).setStock(p.getStock()).build()).collect(Collectors.toList())).build();

     responseObserver.onNext(response);
     responseObserver.onCompleted();     
        
    }

    @Override
     public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver){
        ProductCreateDto productToBeCreated = new ProductCreateDto(request.getName(), request.getPrice(), request.getStock());

        Product createdProduct = productUseCase.createProduct(productToBeCreated);

        ProductResponse response =ProductResponse.newBuilder().setId(createdProduct.getId()).setName(createdProduct.getName()).setPrice(createdProduct.getPrice()).setStock(createdProduct.getStock()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
     }

     @Override
     public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver){
        Product productToBeUpdated = new Product(request.getId(), request.getName(), request.getPrice(), request.getStock());

        Product updatedProduct = productUseCase.updateProduct(productToBeUpdated);

        ProductResponse response = ProductResponse.newBuilder().setId(updatedProduct.getId()).setName(updatedProduct.getName()).setPrice(updatedProduct.getPrice()).setStock(updatedProduct.getStock()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
     }

     @Override
     public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver){
        DeleteProductResultDto deleteMessage = productUseCase.deleteProduct(request.getId());

        DeleteProductResponse response = DeleteProductResponse.newBuilder().setSuccess(deleteMessage.isSuccess()).setMessage(deleteMessage.getMessage()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
     }
}
