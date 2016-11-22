package core;

/**
 * Created by yousuf on 11/22/16.
 */
public class BitBoard {
    public long whites = 0;
    public long blacks = 0;
    public long pawns = 0;
    public long rooks = 0;
    public long queens = 0;
    public long bishops = 0;
    public long knights = 0;
    public long kings = 0;

    //Last State
    private long lastWhites = 0;
    private long lastBlacks = 0;
    private long lastPawns = 0;
    private long lastKnights = 0;
    private long lastRooks = 0;
    private long lastQueens = 0;
    private long lastBishops = 0;
    private long lastKings = 0;

    public void setPositionPiece(long position, Piece piece) {
        pawns &= ~position;
        knights &= ~position;
        bishops &= ~position;
        rooks &= ~position;
        queens &= ~position;
        kings &= ~position;

        //Set square to empty
        if (piece == null) {
            whites &= ~position;
            blacks &= ~position;
            return;
        } else if (piece.getColor() == PieceColor.BLACK) {
            whites &= ~position;
            blacks |= position;
        } else {
            whites |= position;
            blacks &= ~position;
        }

        switch (piece.getPieceType()) {
            case PAWN:
                pawns |= position;
                break;
            case KNIGHT:
                knights |= position;
                break;
            case BISHOP:
                rooks |= position;
                break;
            case QUEEN:
                queens |= position;
                break;
            case KING:
                kings |= position;
                break;
        }

    }

    public void undoMove() {
        whites = lastWhites;
        blacks = lastBlacks;
        pawns = lastPawns;
        queens = lastQueens;
        bishops = lastBishops;
        knights = lastKnights;
        rooks = lastRooks;
        kings = lastKings;

    }
}
