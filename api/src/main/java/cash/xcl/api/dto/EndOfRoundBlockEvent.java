package cash.xcl.api.dto;

import cash.xcl.api.util.RegionIntConverter;
import cash.xcl.util.XCLLongLongMap;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.IntConversion;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

public class EndOfRoundBlockEvent extends SignedMessage {
    @IntConversion(RegionIntConverter.class)
    private int region;
    private int weekNumber;
    private long blockNumber;
    private XCLLongLongMap blockRecords = XCLLongLongMap.withExpectedSize(16);
    private LongU32Writer longU32Writer;

    public EndOfRoundBlockEvent(long sourceAddress, long eventTime, int region, int weekNumber, long blockNumber, XCLLongLongMap blockRecords) {
        super(sourceAddress, eventTime);
        this.region = region;
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
        this.blockRecords = blockRecords;
    }

    public EndOfRoundBlockEvent() {

    }

    @NotNull
    @Override
    public <T> T deepCopy() {
        EndOfRoundBlockEvent tbe = new EndOfRoundBlockEvent();
        this.copyTo(tbe);
        return (T) tbe;
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        EndOfRoundBlockEvent tbe = (EndOfRoundBlockEvent) t;
        super.copyTo(t);
        tbe.region(region);
        tbe.weekNumber(weekNumber);
        tbe.blockNumber(blockNumber);
        assert !blockRecords.containsKey(0L);
        tbe.blockRecords().putAll(blockRecords);
        return t;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readInt();
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        blockRecords.clear();
        int blocks = (int) bytes.readStopBit();
        for (int i = 0; i < blocks; i++)
            blockRecords.put(bytes.readLong(), bytes.readUnsignedInt());
//        System.out.println("Read "+this);
        assert !blockRecords.containsKey(0L);
    }


    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        assert !blockRecords.containsKey(0L);
//        System.out.println("Write "+this);
        bytes.writeInt(region);
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeStopBit(blockRecords.size());
        if (longU32Writer == null) longU32Writer = new LongU32Writer();
        longU32Writer.bytes = bytes;
        blockRecords.forEach(longU32Writer);
    }

    @Override
    public int messageType() {
        return MessageTypes.TREE_BLOCK_EVENT;
    }

    public int region() {
        return region;
    }

    public EndOfRoundBlockEvent region(int region) {
        this.region = region;
        return this;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public EndOfRoundBlockEvent weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public EndOfRoundBlockEvent blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }

    public XCLLongLongMap blockRecords() {
        if (blockRecords == null)
            blockRecords = XCLLongLongMap.withExpectedSize(16);
        return blockRecords;
    }
}

