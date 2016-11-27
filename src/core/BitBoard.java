package core;

/**
 * Created by yousuf on 11/22/16.
 */
public class BitBoard {
    //constants
    private final byte WHITE_PAWN = 2;
    private final byte BLACK_PAWN = 3;
    private final byte WHITE_KNIGHT = 4;
    private final byte BLACK_KNIGHT = 5;
    private final byte WHITE_ROOK = 6;
    private final byte BLACK_ROOK = 7;
    private final byte WHITE_BISHOP = 8;
    private final byte BLACK_BISHOP = 9;
    private final byte WHITE_QUEEN = 10;
    private final byte BLACK_QUEEN = 11;
    private final byte WHITE_KING = 12;
    private final byte BLACK_KING = 13;
    private final byte EMPTY = 0;
    private final byte WHITE = 0;
    private final byte BLACK = 1;
    private final byte PAWN = 2;
    private final byte KNIGHT = 4;
    private final byte ROOK = 6;
    private final byte BISHOP = 8;
    private final byte QUEEN = 10;
    private final byte KING = 12;
    private final int NULL_SQUARE = 64;
    private final long ROW_1 = 0xFF << 0;


    //Castling
    private final byte WHITE_KINGSIDE = 1;
    private final byte WHITE_QUEENSIDE = 2;
    private final byte BLACK_KINGSIDE = 4;
    private final byte BLACK_QUEENSIDE = 8;
    private final byte FULL_CASTLING_RIGHTS = 1 | 2 | 4 | 8;


    public long[] bitboards = new long[14];
    public byte[] board = new byte[64];
    Flags flags = new Flags();

    boolean isEmpty() {
        return 0 == ~(bitboards[WHITE] | bitboards[BLACK]);
    }

    void addPiece(byte piece, int square) {
        board[square] = piece;
        long bitboard = 1 << square;
        bitboards[piece & 1] |= bitboard;
        bitboards[piece] |= bitboard;
    }

    void removePiece(int square) {
        byte piece = board[square];
        board[square] = EMPTY;
        long bitboard = ~(1 << square);
        bitboards[piece & 1] &= bitboard;
        bitboards[piece] &= bitboard;
    }

    void reset() {
        for (int i = 0; i < 64; i++) {
            board[i] = EMPTY;
        }
        for (int i = 0; i < 14; i++) {
            bitboards[i] = 0;
        }
        flags.castlingRights = FULL_CASTLING_RIGHTS;
        flags.enPassantSquare = NULL_SQUARE;
        flags.sideToMove = WHITE;
    }

    byte getType(int square) {
        return board[square];
    }

    long getBitboard(int type) {
        return bitboards[type];
    }

    void moveGenerator() {
        long pushes = bitboards[WHITE_PAWN] << 8;
        long occupied = bitboards[WHITE] | bitboards[BLACK];
        pushes &= ~occupied;
        long row3 = 0x0000000000FF0000;
        long doublePushes = (pushes & row3) << 8;
        doublePushes &= ~occupied;
        long enemyPieces = bitboards[BLACK];
        long leftAttacks = bitboards[WHITE_PAWN] << 7;
        leftAttacks &= enemyPieces;
        long enPassantTargets = 1 << flags.enPassantSquare;
        long leftEnPassantAttacks = bitboards[WHITE_PAWN] << 7;
        leftEnPassantAttacks &= enPassantTargets;
        long fileH = 0x0101010101010101L;
        leftAttacks &= ~fileH;
        long row8 = 0xFF00000000000000L;
        long promotions = pushes & row8;
        pushes &= ~row8;
    }

    void addPawnPushes() {
        int[] diffs = {8, 64 - 8};
        long[] promotions_mask[ 2] ={
        }
    }

    void circularLeftShift(long target, int shift) {
        return target << shift | target >> (64 - shift);
    }
    class Flags {
        byte castlingRights;
        byte enPassantSquare;
        byte sideToMove;
    }
}
