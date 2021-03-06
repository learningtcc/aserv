package com.absir.thrift;

import com.absir.server.in.IFaceProxy;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 2016/12/20.
 */
public class TMultiplexedProcessorProxy implements TProcessor {

    protected Map<String, IFaceProcessProxy> nameMapIFaceProcessFunction = new HashMap<String, IFaceProcessProxy>();

    public Map<String, IFaceProcessProxy> getNameMapIFaceProcessFunction() {
        return nameMapIFaceProcessFunction;
    }

    public <T> void registerTypeProcessor(Class<T> ifaceType, T iface, TBaseProcessor<T> baseProcessor) {
        registerBaseProcessor(TSocketAdapterReceiver.getServiceName(ifaceType), iface, baseProcessor);
    }

    public void registerBaseProcessor(String serviceName, Object iface, TBaseProcessor baseProcessor) {
        for (Map.Entry<String, ProcessFunction> entry : ((Map<String, ProcessFunction>) (Object) baseProcessor.getProcessMapView()).entrySet()) {
            IFaceProcessProxy processProxy = new IFaceProcessProxy();
            processProxy.iface = iface;
            processProxy.processFunction = entry.getValue();
            processProxy.faceProxy = iface == null || !(iface instanceof IFaceProxy) ? null : (IFaceProxy) iface;
            nameMapIFaceProcessFunction.put(TSocketAdapterReceiver.getServiceUri(serviceName, entry.getKey()), processProxy);
        }
    }

    @Override
    public boolean process(TProtocol iprot, TProtocol oprot) throws TException {
        TMessage message = iprot.readMessageBegin();
        if (message.type != 1 && message.type != 4) {
            throw new TException("This should not have happened!?");

        } else {
            IFaceProcessProxy faceProcessProxy = nameMapIFaceProcessFunction.get(message.name);
            if (faceProcessProxy == null) {
                throw new TException("Service name not found in message name: " + message.name + ".  Did you " + "forget to use a TMultiplexedProcessorProxy in your client?");

            } else {
                IFaceProxy faceProxy = faceProcessProxy.faceProxy;
                OnPut onPut = faceProxy == null ? null : OnPut.get();
                Object iface = onPut == null ? faceProcessProxy.iface : faceProxy.getServer(onPut);
                if (iface == null) {
                    throw new TException("IFace is null!? " + message.name);
                }

                faceProcessProxy.processFunction.process(message.seqid, iprot, oprot, iface);
                return true;
            }
        }
    }

    public void process(TMultiplexedProcessorProxy.IFaceProcessProxy faceProcessProxy, Input input, ThriftService service) throws IOException, TException {
        IFaceProxy faceProxy = faceProcessProxy.faceProxy;
        OnPut onPut = faceProxy == null ? null : OnPut.get();
        Object iface = onPut == null ? faceProcessProxy.iface : faceProxy.getServer(onPut);
        if (iface == null) {
            throw new TException("IFace is null!? " + input.getUri());
        }

        InputStream inputStream = input.getInputStream();
        if (service != null) {
            inputStream = service.decrypt(input.getId(), inputStream);
        }

        TTransport inTransport = new TIOStreamTransport(input.getInputStream());
        OutputStream outputStream = service != null ? null : input.getOutputStream();
        ByteArrayOutputStream byteArrayOutputStream = null;
        if (outputStream == null) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            outputStream = byteArrayOutputStream;
        }

        TTransport outTransport = new TIOStreamTransport(outputStream);
        faceProcessProxy.processFunction.process(0, new TCompactProtocol(inTransport), new TInputOutProtocol(outTransport), iface);
        if (byteArrayOutputStream != null) {
            input.write(service.encrypt(input.getId(), 1, byteArrayOutputStream));
        }
    }

    public static class IFaceProcessProxy {

        public Object iface;

        public ProcessFunction processFunction;

        public IFaceProxy faceProxy;

    }

    public static class TInputOutProtocol extends TCompactProtocol {

        public TInputOutProtocol(TTransport transport) {
            super(transport);
        }

        @Override
        public void writeMessageBegin(TMessage message) throws TException {
            writeByte(message.type);
        }
    }

}
