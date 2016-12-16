# Tarfly

Fast transfer for small files using on the fly tar archive and decompression on the client side.

Equivalent to the unix command: 

```
tar -cf - SOURCEFOLDER | ssh USERNAME@SERVER 'tar -xf -C TARGETFOLDER'
```

## Package

```
./mvnw package
```

## File transfer
### Start the server
```
java -jar target/tarfly.jar --server --port=<PORT> --sourceFolder=<SRC>
```

### Start the client

```
java -jar target/tarfly.jar --client --port=<PORT> --host=<SERVERHOST> --targetFolder=<TARGET>
```