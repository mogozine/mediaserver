package com.mgz.mediaserver.ebml;

import java.util.HashMap;
import java.util.Map;
import static com.mgz.mediaserver.ebml.EBMLDataType.*;

public enum EBMLElementType {
	UNDEFINED(0, new short[0],EBMLDataType.Undefined),
	EBML(0, new short[]{0x1A, 0x45, 0xDF, 0xA3}, EBMLMasterElement),
	EBMLVersion(1, new short[]{0x42, 0x86}, EBMLUnsignedInteger),
	EBMLReadVersion(1, new short[]{0x42, 0xF7}, EBMLUnsignedInteger),
	EBMLMaxIDLength(1, new short[]{0x42, 0xF2}, EBMLUnsignedInteger),
	EBMLMaxSizeLength(1, new short[]{0x42, 0xF3}, EBMLUnsignedInteger),
	DocType(1, new short[]{0x42, 0x82},EBMLString),
	DocTypeVersion(1, new short[]{0x42, 0x87},EBMLUnsignedInteger),
	DocTypeReadVersion(1, new short[]{0x42, 0x85},EBMLUnsignedInteger),
	Void(-1, new short[]{0xEC},EBMLBinary),
	CRC32(-1, new short[]{0xBF},EBMLBinary),
	SignatureSlot(-1, new short[]{0x1B, 0x53, 0x86, 0x67},EBMLMasterElement),
	SignatureAlgo(1, new short[]{0x7E, 0x8A},EBMLUnsignedInteger),
	SignatureHash(1, new short[]{0x7E, 0x9A},EBMLUnsignedInteger),
	SignaturePublicKey(1, new short[]{0x7E, 0xA5},EBMLBinary),
	Signature(1, new short[]{0x7E, 0xB5},EBMLBinary),
	SignatureElements(1, new short[]{0x7E, 0x5B},EBMLMasterElement),
	SignatureElementList(2, new short[]{0x7E, 0x7B},EBMLMasterElement),
	SignedElement(3, new short[]{0x65, 0x32},EBMLBinary),
	Segment(0, new short[]{0x18, 0x53, 0x80, 0x67},EBMLMasterElement),
	SeekHead(1, new short[]{0x11, 0x4D, 0x9B, 0x74},EBMLMasterElement),
	Seek(2, new short[]{0x4D, 0xBB},EBMLMasterElement),
	SeekID(3, new short[]{0x53, 0xAB},EBMLBinary),
	SeekPosition(3, new short[]{0x53, 0xAC},EBMLUnsignedInteger),
	Info(1, new short[]{0x15, 0x49, 0xA9, 0x66},EBMLMasterElement),
	SegmentUID(2, new short[]{0x73, 0xA4},EBMLBinary),
	SegmentFilename(2, new short[]{0x73, 0x84},EBMLUTF8),
	PrevUID(2, new short[]{0x3C, 0xB9, 0x23},EBMLBinary),
	PrevFilename(2, new short[]{0x3C, 0x83, 0xAB},EBMLUTF8),
	NextUID(2, new short[]{0x3E, 0xB9, 0x23},EBMLBinary),
	NextFilename(2, new short[]{0x3E, 0x83, 0xBB},EBMLUTF8),
	SegmentFamily(2, new short[]{0x44, 0x44},EBMLBinary),
	ChapterTranslate(2, new short[]{0x69, 0x24},EBMLMasterElement),
	ChapterTranslateEditionUID(3, new short[]{0x69, 0xFC},EBMLUnsignedInteger),
	ChapterTranslateCodec(3, new short[]{0x69, 0xBF},EBMLUnsignedInteger),
	ChapterTranslateID(3, new short[]{0x69, 0xA5},EBMLBinary),
	TimecodeScale(2, new short[]{0x2A, 0xD7, 0xB1},EBMLUnsignedInteger),
	Duration(2, new short[]{0x44, 0x89},EBMLFloat),
	DateUTC(2, new short[]{0x44, 0x61},EBMLDate),
	Title(2, new short[]{0x7B, 0xA9},EBMLUTF8),
	MuxingApp(2, new short[]{0x4D, 0x80},EBMLUTF8),
	WritingApp(2, new short[]{0x57, 0x41},EBMLUTF8),
	Cluster(1, new short[]{0x1F, 0x43, 0xB6, 0x75},EBMLMasterElement),
	Timecode(2, new short[]{0xE7},EBMLUnsignedInteger),
	SilentTracks(2, new short[]{0x58, 0x54},EBMLMasterElement),
	SilentTrackNumber(3, new short[]{0x58, 0xD7},EBMLUnsignedInteger),
	Position(2, new short[]{0xA7},EBMLUnsignedInteger),
	PrevSize(2, new short[]{0xAB},EBMLUnsignedInteger),
	SimpleBlock(2, new short[]{0xA3},EBMLBinary),
	BlockGroup(2, new short[]{0xA0},EBMLMasterElement),
	Block(3, new short[]{0xA1},EBMLBinary),
	BlockVirtual(3, new short[]{0xA2},EBMLBinary),
	BlockAdditions(3, new short[]{0x75, 0xA1},EBMLMasterElement),
	BlockMore(4, new short[]{0xA6},EBMLMasterElement),
	BlockAddID(5, new short[]{0xEE},EBMLUnsignedInteger),
	BlockAdditional(5, new short[]{0xA5},EBMLBinary),
	BlockDuration(3, new short[]{0x9B},EBMLUnsignedInteger),
	ReferencePriority(3, new short[]{0xFA},EBMLUnsignedInteger),
	ReferenceBlock(3, new short[]{0xFB},EBMLSignedInteger),
	ReferenceVirtual(3, new short[]{0xFD},EBMLSignedInteger),
	CodecState(3, new short[]{0xA4},EBMLBinary),
	DiscardPadding(3, new short[]{0x75, 0xA2},EBMLSignedInteger),
	Slices(3, new short[]{0x8E},EBMLMasterElement),
	TimeSlice(4, new short[]{0xE8},EBMLMasterElement),
	LaceNumber(5, new short[]{0xCC},EBMLUnsignedInteger),
	FrameNumber(5, new short[]{0xCD},EBMLUnsignedInteger),
	BlockAdditionID(5, new short[]{0xCB},EBMLUnsignedInteger),
	Delay(5, new short[]{0xCE},EBMLUnsignedInteger),
	SliceDuration(5, new short[]{0xCF},EBMLUnsignedInteger),
	ReferenceFrame(3, new short[]{0xC8},EBMLMasterElement),
	ReferenceOffset(4, new short[]{0xC9},EBMLUnsignedInteger),
	ReferenceTimeCode(4, new short[]{0xCA},EBMLUnsignedInteger),
	EncryptedBlock(2, new short[]{0xAF},EBMLBinary),
	Tracks(1, new short[]{0x16, 0x54, 0xAE, 0x6B},EBMLMasterElement),
	TrackEntry(2, new short[]{0xAE},EBMLMasterElement),
	TrackNumber(3, new short[]{0xD7},EBMLUnsignedInteger),
	TrackUID(3, new short[]{0x73, 0xC5},EBMLUnsignedInteger),
	TrackType(3, new short[]{0x83},EBMLUnsignedInteger),
	FlagEnabled(3, new short[]{0xB9},EBMLUnsignedInteger),
	FlagDefault(3, new short[]{0x88},EBMLUnsignedInteger),
	FlagForced(3, new short[]{0x55, 0xAA},EBMLUnsignedInteger),
	FlagLacing(3, new short[]{0x9C},EBMLUnsignedInteger),
	MinCache(3, new short[]{0x6D, 0xE7},EBMLUnsignedInteger),
	MaxCache(3, new short[]{0x6D, 0xF8},EBMLUnsignedInteger),
	DefaultDuration(3, new short[]{0x23, 0xE3, 0x83},EBMLUnsignedInteger),
	DefaultDecodedFieldDuration(3, new short[]{0x23, 0x4E, 0x7A},EBMLUnsignedInteger),
	TrackTimecodeScale(3, new short[]{0x23, 0x31, 0x4F},EBMLFloat),
	TrackOffset(3, new short[]{0x53, 0x7F},EBMLSignedInteger),
	MaxBlockAdditionID(3, new short[]{0x55, 0xEE},EBMLUnsignedInteger),
	Name(3, new short[]{0x53, 0x6E},EBMLUTF8),
	Language(3, new short[]{0x22, 0xB5, 0x9C},EBMLString),
	CodecID(3, new short[]{0x86},EBMLString),
	CodecPrivate(3, new short[]{0x63, 0xA2},EBMLBinary),
	CodecName(3, new short[]{0x25, 0x86, 0x88},EBMLUTF8),
	AttachmentLink(3, new short[]{0x74, 0x46},EBMLUnsignedInteger),
	CodecSettings(3, new short[]{0x3A, 0x96, 0x97},EBMLUTF8),
	CodecInfoURL(3, new short[]{0x3B, 0x40, 0x40},EBMLString),
	CodecDownloadURL(3, new short[]{0x26, 0xB2, 0x40},EBMLString),
	CodecDecodeAll(3, new short[]{0xAA},EBMLUnsignedInteger),
	TrackOverlay(3, new short[]{0x6F, 0xAB},EBMLUnsignedInteger),
	CodecDelay(3, new short[]{0x56, 0xAA},EBMLUnsignedInteger),
	SeekPreRoll(3, new short[]{0x56, 0xBB},EBMLUnsignedInteger),
	TrackTranslate(3, new short[]{0x66, 0x24},EBMLMasterElement),
	TrackTranslateEditionUID(4, new short[]{0x66, 0xFC},EBMLUnsignedInteger),
	TrackTranslateCodec(4, new short[]{0x66, 0xBF},EBMLUnsignedInteger),
	TrackTranslateTrackID(4, new short[]{0x66, 0xA5},EBMLBinary),
	Video(3, new short[]{0xE0},EBMLMasterElement),
	FlagInterlaced(4, new short[]{0x9A},EBMLUnsignedInteger),
	StereoMode(4, new short[]{0x53, 0xB8},EBMLUnsignedInteger),
	AlphaMode(4, new short[]{0x53, 0xC0},EBMLUnsignedInteger),
	OldStereoMode(4, new short[]{0x53, 0xB9},EBMLUnsignedInteger),
	PixelWidth(4, new short[]{0xB0},EBMLUnsignedInteger),
	PixelHeight(4, new short[]{0xBA},EBMLUnsignedInteger),
	PixelCropBottom(4, new short[]{0x54, 0xAA},EBMLUnsignedInteger),
	PixelCropTop(4, new short[]{0x54, 0xBB},EBMLUnsignedInteger),
	PixelCropLeft(4, new short[]{0x54, 0xCC},EBMLUnsignedInteger),
	PixelCropRight(4, new short[]{0x54, 0xDD},EBMLUnsignedInteger),
	DisplayWidth(4, new short[]{0x54, 0xB0},EBMLUnsignedInteger),
	DisplayHeight(4, new short[]{0x54, 0xBA},EBMLUnsignedInteger),
	DisplayUnit(4, new short[]{0x54, 0xB2},EBMLUnsignedInteger),
	AspectRatioType(4, new short[]{0x54, 0xB3},EBMLUnsignedInteger),
	ColourSpace(4, new short[]{0x2E, 0xB5, 0x24},EBMLBinary),
	GammaValue(4, new short[]{0x2F, 0xB5, 0x23},EBMLFloat),
	FrameRate(4, new short[]{0x23, 0x83, 0xE3},EBMLFloat),
	Audio(3, new short[]{0xE1},EBMLMasterElement),
	SamplingFrequency(4, new short[]{0xB5},EBMLFloat),
	OutputSamplingFrequency(4, new short[]{0x78, 0xB5},EBMLFloat),
	Channels(4, new short[]{0x9F},EBMLUnsignedInteger),
	ChannelPositions(4, new short[]{0x7D, 0x7B},EBMLBinary),
	BitDepth(4, new short[]{0x62, 0x64},EBMLUnsignedInteger),
	TrackOperation(3, new short[]{0xE2},EBMLMasterElement),
	TrackCombinePlanes(4, new short[]{0xE3},EBMLMasterElement),
	TrackPlane(5, new short[]{0xE4},EBMLMasterElement),
	TrackPlaneUID(6, new short[]{0xE5},EBMLUnsignedInteger),
	TrackPlaneType(6, new short[]{0xE6},EBMLUnsignedInteger),
	TrackJoinBlocks(4, new short[]{0xE9},EBMLMasterElement),
	TrackJoinUID(5, new short[]{0xED},EBMLUnsignedInteger),
	TrickTrackUID(3, new short[]{0xC0},EBMLUnsignedInteger),
	TrickTrackSegmentUID(3, new short[]{0xC1},EBMLBinary),
	TrickTrackFlag(3, new short[]{0xC6},EBMLUnsignedInteger),
	TrickMasterTrackUID(3, new short[]{0xC7},EBMLUnsignedInteger),
	TrickMasterTrackSegmentUID(3, new short[]{0xC4},EBMLBinary),
	ContentEncodings(3, new short[]{0x6D, 0x80},EBMLMasterElement),
	ContentEncoding(4, new short[]{0x62, 0x40},EBMLMasterElement),
	ContentEncodingOrder(5, new short[]{0x50, 0x31},EBMLUnsignedInteger),
	ContentEncodingScope(5, new short[]{0x50, 0x32},EBMLUnsignedInteger),
	ContentEncodingType(5, new short[]{0x50, 0x33},EBMLUnsignedInteger),
	ContentCompression(5, new short[]{0x50, 0x34},EBMLMasterElement),
	ContentCompAlgo(6, new short[]{0x42, 0x54},EBMLUnsignedInteger),
	ContentCompSettings(6, new short[]{0x42, 0x55},EBMLBinary),
	ContentEncryption(5, new short[]{0x50, 0x35},EBMLBinary),
	ContentEncAlgo(6, new short[]{0x47, 0xE1},EBMLUnsignedInteger),
	ContentEncKeyID(6, new short[]{0x47, 0xE2},EBMLBinary),
	ContentSignature(6, new short[]{0x47, 0xE3},EBMLBinary),
	ContentSigKeyID(6, new short[]{0x47, 0xE4},EBMLBinary),
	ContentSigAlgo(6, new short[]{0x47, 0xE5},EBMLUnsignedInteger),
	ContentSigHashAlgo(6, new short[]{0x47, 0xE6},EBMLUnsignedInteger),
	
	Cues(1, new short[]{0x1C,0x53,0xBB,0x6B},EBMLMasterElement),// A top-level element to speed seeking access. All entries are local to the segment. Should be mandatory for non "live" streams.
	CuePoint(2, new short[]{0xBB},EBMLMasterElement),	// Contains all information relative to a seek point in the segment.
	CueTime(3, new short[]{0xB3},EBMLUnsignedInteger),	// Absolute timestamp according to the segment time base.
	CueTrackPositions(3, new short[]{0xB7},EBMLMasterElement), // Contain positions for different tracks corresponding to the timestamp.
	CueTrack(4, new short[]{0xF7}, EBMLUnsignedInteger),	// The track for which a position is given.
	CueClusterPosition(4, new short[]{0xF1},EBMLUnsignedInteger),	// The position of the Cluster containing the required Block.
	CueRelativePosition(4, new short[]{0xF0},EBMLUnsignedInteger),	// The relative position of the referenced block inside the cluster with 0 being the first possible position for an element inside that cluster.
	CueDuration(4, new short[]{0xB2},EBMLUnsignedInteger),	// The duration of the block according to the segment time base. If missing the track's DefaultDuration does not apply and no duration information is available in terms of the cues.
	CueBlockNumber(4, new short[]{0x53,0x78},EBMLUnsignedInteger), // Number of the Block in the specified Cluster.
	CueCodecState(4, new short[]{0xEA},EBMLUnsignedInteger), // The position of the Codec State corresponding to this Cue element. 0 means that the data is taken from the initial Track Entry.
	CueReference(4, new short[]{0xDB},EBMLMasterElement), // The Clusters containing the required referenced Blocks.
	CueRefTime(5, new short[]{0x96},EBMLUnsignedInteger), // Timestamp of the referenced Block.
	CueRefCluster(5, new short[]{0x97},EBMLUnsignedInteger), // The Position of the Cluster containing the referenced Block.
	CueRefNumber(5, new short[]{0x53,0x5F},EBMLUnsignedInteger), // Number of the referenced Block of Track X in the specified Cluster.
	CueRefCodecState(5, new short[]{0xEB},EBMLUnsignedInteger), // The position of the Codec State corresponding to this referenced element. 0 means that the data is taken from the initial Track Entry.
	
	Attachments(1, new short[]{0x19, 0x41, 0xA4, 0x69},EBMLMasterElement),
	AttachedFile(2, new short[]{0x61, 0xA7},EBMLMasterElement),
	FileDescription(3, new short[]{0x46, 0x7E},EBMLUTF8),
	FileName(3, new short[]{0x46, 0x6E},EBMLUTF8),
	FileMimeType(3, new short[]{0x46, 0x60},EBMLString),
	FileData(3, new short[]{0x46, 0x5C},EBMLBinary),
	FileUID(3, new short[]{0x46, 0xAE},EBMLUnsignedInteger),
	FileReferral(3, new short[]{0x46, 0x75},EBMLBinary),
	FileUsedStartTime(3, new short[]{0x46, 0x61},EBMLUnsignedInteger),
	FileUsedEndTime(3, new short[]{0x46, 0x62},EBMLUnsignedInteger),
	Chapters(1, new short[]{0x10, 0x43, 0xA7, 0x70},EBMLMasterElement),
	EditionEntry(2, new short[]{0x45, 0xB9},EBMLMasterElement),
	EditionUID(3, new short[]{0x45, 0xBC},EBMLUnsignedInteger),
	EditionFlagHidden(3, new short[]{0x45, 0xBD},EBMLUnsignedInteger),
	EditionFlagDefault(3, new short[]{0x45, 0xDB},EBMLUnsignedInteger),
	EditionFlagOrdered(3, new short[]{0x45, 0xDD},EBMLUnsignedInteger),
	ChapterAtom(-3, new short[]{0xB6},EBMLMasterElement),
	ChapterUID(4, new short[]{0x73, 0xC4},EBMLUnsignedInteger),
	ChapterStringUID(4, new short[]{0x56, 0x54},EBMLUTF8),
	ChapterTimeStart(4, new short[]{0x91},EBMLUnsignedInteger),
	ChapterTimeEnd(4, new short[]{0x92},EBMLUnsignedInteger),
	ChapterFlagHidden(4, new short[]{0x98},EBMLUnsignedInteger),
	ChapterFlagEnabled(4, new short[]{0x45, 0x98},EBMLUnsignedInteger),
	ChapterSegmentUID(4, new short[]{0x6E, 0x67},EBMLBinary),
	ChapterSegmentEditionUID(4, new short[]{0x6E, 0xBC},EBMLUnsignedInteger),
	ChapterPhysicalEquiv(4, new short[]{0x63, 0xC3},EBMLUnsignedInteger),
	ChapterTrack(4, new short[]{0x8F},EBMLMasterElement),
	ChapterTrackNumber(5, new short[]{0x89},EBMLUnsignedInteger),
	ChapterDisplay(4, new short[]{0x80},EBMLMasterElement),
	ChapString(5, new short[]{0x85},EBMLUTF8),
	ChapLanguage(5, new short[]{0x43, 0x7C},EBMLString),
	ChapCountry(5, new short[]{0x43, 0x7E},EBMLString),
	ChapProcess(4, new short[]{0x69, 0x44},EBMLMasterElement),
	ChapProcessCodecID(5, new short[]{0x69, 0x55},EBMLUnsignedInteger),
	ChapProcessPrivate(5, new short[]{0x45, 0x0D},EBMLBinary),
	ChapProcessCommand(5, new short[]{0x69, 0x11},EBMLMasterElement),
	ChapProcessTime(6, new short[]{0x69, 0x22},EBMLUnsignedInteger),
	ChapProcessData(6, new short[]{0x69, 0x33},EBMLBinary),
	Tags(1, new short[]{0x12, 0x54, 0xC3, 0x67},EBMLMasterElement),
	Tag(2, new short[]{0x73, 0x73},EBMLMasterElement),
	Targets(3, new short[]{0x63, 0xC0},EBMLMasterElement),
	TargetTypeValue(4, new short[]{0x68, 0xCA},EBMLUnsignedInteger),
	TargetType(4, new short[]{0x63, 0xCA},EBMLString),
	TagTrackUID(4, new short[]{0x63, 0xC5},EBMLUnsignedInteger),
	TagEditionUID(4, new short[]{0x63, 0xC9},EBMLUnsignedInteger),
	TagChapterUID(4, new short[]{0x63, 0xC4},EBMLUnsignedInteger),
	TagAttachmentUID(4, new short[]{0x63, 0xC6},EBMLUnsignedInteger),
	SimpleTag(-3, new short[]{0x67, 0xC8},EBMLMasterElement),
	TagName(4, new short[]{0x45, 0xA3},EBMLUTF8),
	TagLanguage(4, new short[]{0x44, 0x7A},EBMLString),
	TagDefault(4, new short[]{0x44, 0x84},EBMLUnsignedInteger),
	TagString(4, new short[]{0x44, 0x87},EBMLUTF8),
	TagBinary(4, new short[]{0x44, 0x85},EBMLBinary);
	

	private static Map<Long, EBMLElementType> elementTypCode2ElementTypeMap;
	EBMLDataType dataType;
	int level;
	byte[] newElementTypeCode;

	EBMLElementType(int level, short[] newElementTypeCode, EBMLDataType dataType){
		this.level = level;
		this.newElementTypeCode = new byte[newElementTypeCode.length];
		for(int i=0; i<newElementTypeCode.length; i++){
			this.newElementTypeCode[i] = (byte) newElementTypeCode[i];
		}
		this.dataType = dataType;
	}

	public static EBMLElementType valueOf(byte[] elementTypeCode){
		if(elementTypCode2ElementTypeMap==null){
			prepareElementTypCode2ElementTypeMap();
		}
		
		long key = 0;
		for(byte b : elementTypeCode) {
			key <<= 8;
			key += (b & 0xFF);
		}
		EBMLElementType result = elementTypCode2ElementTypeMap.get(Long.valueOf(key));
		if(result==null) 
			result = UNDEFINED;
		
		return result;
	}
	
	public static void prepareElementTypCode2ElementTypeMap(){
		if(elementTypCode2ElementTypeMap!=null) return;
		elementTypCode2ElementTypeMap = new HashMap<Long, EBMLElementType>();
		for(EBMLElementType elementType : EBMLElementType.values()){
			Long key = elementType.getElementTypeAsLong();
			elementTypCode2ElementTypeMap.put(key, elementType);
		}
	}
	
	private Long getElementTypeAsLong(){
		long key = 0;
		for(byte b : newElementTypeCode) {
			key <<= 8;
			key += (b & 0xFF);
		}
		return Long.valueOf(key);
	}
	
	public int getLevel(){ 
		return level;
	}
	
	public byte[] getElementTypeCode(){ 
		return newElementTypeCode; 
	}

	public EBMLDataType getDataType() {
		return dataType;
	}
}
