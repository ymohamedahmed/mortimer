package dme;

import core.*;

import java.util.ArrayList;

public class Evaluation {
    private double matEval(ArrayList<Piece> pieceList, boolean sideNeutral, boolean maxMaterialValue) {
        double eval = 0;
        double queenConst = 900;
        double rookConst = 500;
        double knightConst = 330;
        double bishopConst = 320;
        double pawnConst = 100;
        double kingConst = 400;
        int queenW = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.WHITE);
        int queenB = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.BLACK);
        int rookW = Board.noOfPieces(pieceList, PieceType.ROOK, PieceColor.WHITE);
        int rookB = Board.noOfPieces(pieceList, PieceType.ROOK, PieceColor.BLACK);
        int knightW = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.WHITE);
        int knightB = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.BLACK);
        int bishopW = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.WHITE);
        int bishopB = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.BLACK);
        int pawnW = Board.noOfPieces(pieceList, PieceType.PAWN, PieceColor.WHITE);
        int pawnB = Board.noOfPieces(pieceList, PieceType.PAWN, PieceColor.BLACK);
        if (!sideNeutral) {
            eval = queenConst * (queenW - queenB) + rookConst * (rookW - rookB) + knightConst * (knightW - knightB)
                    + bishopConst * (bishopW - bishopB) + pawnConst * (pawnW - pawnB);
            //System.out.println("MAT EVAL : "  + eval);
        } else {
            eval = queenConst * (queenW + queenB) + rookConst * (rookW + rookB) + knightConst * (knightW + knightB)
                    + bishopConst * (bishopW + bishopB) + pawnConst * (pawnW + pawnB);
        }
        if (maxMaterialValue) {
            eval = queenConst * (2) + rookConst * (2) + knightConst * (4) + bishopConst * (4) + pawnConst * (16)
                    + kingConst * (2);
        }
        return eval;
    }

    private double mobEval(ArrayList<Piece> pieceList) {
        double mobilityFactor = 10;
        int whiteMoves = 0;
        int blackMoves = 0;
        double score = 0;
        for (Piece piece : pieceList) {
            int noOfMoves = piece.getMovesList().size();
            if (piece.getColor() == PieceColor.BLACK) {
                blackMoves += noOfMoves;
            } else if (piece.getColor() == PieceColor.WHITE) {
                whiteMoves += noOfMoves;
            }
        }
        score = mobilityFactor * (whiteMoves - blackMoves);
        //System.out.println("Mob EVAL : "  + score);
        return score;
    }

    private double developmentEval(ArrayList<Piece> pieceList) {
        double score = 0;
        double queenConst = 1000;
        double rookConst = 600;
        double knightConst = 350;
        double bishopConst = 400;
        double pawnConst = 70;
        for (Piece piece : pieceList) {
            int colorFactor = piece.getColor().getColorFactor();
            if (piece.getNumberOfMoves() != 0) {
                switch (piece.getPieceType()) {
                    case QUEEN:
                        score += (queenConst * colorFactor);
                        break;
                    case ROOK:
                        score += (rookConst * colorFactor);
                        break;
                    case KNIGHT:
                        score += (knightConst * colorFactor);
                        break;
                    case BISHOP:
                        score += (bishopConst * colorFactor);
                        break;
                    case PAWN:
                        score += (pawnConst * colorFactor);
                        break;
                    default:
                        //System.out.println("Evaluation.developmentEval");
                        score += 0;
                        break;
                }
            }
        }
        //System.out.println("DEV EVAL : "  + score);
        return score;
    }

    private double pawnAdvancementEval(ArrayList<Piece> pieceList) {
        double score = 0;
        double advancePawnConstant = 40;
        double promotionBonus = 350;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == PieceType.PAWN) {
                int row = piece.getPos().getRow();
                PieceColor color = piece.getColor();
                if ((color == PieceColor.WHITE && row >= 4) || (color == PieceColor.BLACK && row <= 3)) {
                    score += (color == PieceColor.WHITE) ? (row * advancePawnConstant)
                            : -1 * ((8 - row) * advancePawnConstant);
                }
                if (((Pawn) piece).isPawnPromotion(pieceList)) {
                    score += (color == PieceColor.WHITE) ? promotionBonus : -1 * (promotionBonus);
                }
            }
        }
        //System.out.println("PAWN ADV EVAL : "  + score);
        return score;
    }

    private double bishopPairEval(ArrayList<Piece> pieceList) {
        double bishopPairBonus = 50;
        int noOfBishopsWhite = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.WHITE);
        int noOfBishopsBlack = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.BLACK);
        double whiteScore = (noOfBishopsWhite == 2) ? bishopPairBonus : 0;
        double blackScore = (noOfBishopsBlack == 2) ? bishopPairBonus : 0;
        //System.out.println("BISHOP PAIR EVAL : "  + (whiteScore-blackScore));
        return whiteScore - blackScore;
    }

    private double knightOnEdgeEval(ArrayList<Piece> pieceList) {
        double knightOnEdgePenalty = -50;
        double score = 0;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == PieceType.KNIGHT) {
                if (isKnightOnEdge(piece)) {
                    score += piece.getColor().getColorFactor() * knightOnEdgePenalty;
                }
            }
        }
        //System.out.println("KNIGHT EDGE EVAL : "  + score);
        return score;
    }

    private double kingEndGameEval(ArrayList<Piece> pieceList) {
        double kingEndGameConst = 400;
        double score = 0;
        boolean endgame = (assessGamePhase(pieceList) == GamePhase.ENDGAME);
        int noOfKingWhite = Board.noOfPieces(pieceList, PieceType.KING, PieceColor.WHITE);
        int noOfKingBlack = Board.noOfPieces(pieceList, PieceType.KING, PieceColor.BLACK);
        if (endgame) {
            score = kingEndGameConst * (noOfKingWhite - noOfKingBlack);
        }
        //System.out.println("KING END EVAL : "  + score);
        return score;
    }

    private double castlingEval(ArrayList<Piece> pieceList) {
        double score = 0;
        double castleBonus = 0;
        double castleBonusOpening = 70;
        double castleBonusMidgame = 30;
        double castleBonusEndgame = -400;
        GamePhase gamePhase = assessGamePhase(pieceList);
        boolean whiteCastled = false;
        boolean blackCastled = false;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == PieceType.KING) {
                if (piece.getColor() == PieceColor.WHITE) {
                    whiteCastled = ((King) piece).getNoOfCastleMoves() >= 1;
                } else if (piece.getColor() == PieceColor.BLACK) {
                    blackCastled = ((King) piece).getNoOfCastleMoves() >= 1;
                }
            }
        }
        if (whiteCastled || blackCastled) {
            switch (gamePhase) {
                case OPENING:
                    castleBonus = castleBonusOpening;
                    break;
                case MIDGAME:
                    castleBonus = castleBonusMidgame;
                    break;
                case ENDGAME:
                    castleBonus = castleBonusEndgame;
                    break;
            }
            if (whiteCastled) {
                score += castleBonus;
            }
            if (blackCastled) {
                score -= castleBonus;
            }
        }
        //System.out.println("CASTLING EVAL : "  + score);
        return score;
    }

    private double pawnArrangementEval(ArrayList<Piece> pieceList) {
        double score = 0;
        double fullPawnBonus = -75;
        double passedPawnConst = 250;
        double doubledPawnConst = -50;
        double isolatedPawnConst = -40;
        int noOfWhitePawns = 0;
        int noOfBlackPawns = 0;
        ArrayList<Piece> pawns = Board.getPieceByType(pieceList, PieceType.PAWN);

        for (Piece pawn : pawns) {
            int row = pawn.getPos().getRow();
            int col = pawn.getPos().getCol();
            if (pawn.getColor() == PieceColor.WHITE) {
                noOfWhitePawns++;
            } else if (pawn.getColor() == PieceColor.BLACK) {
                noOfBlackPawns++;
            }
            if ((row >= 5 && pawn.getColor() == PieceColor.WHITE)
                    || (row <= 2 && pawn.getColor() == PieceColor.BLACK)) {
                score += pawn.getColor().getColorFactor() * passedPawnConst;
            }
            if (Board.getPieceByColumnAndColorAndType(pieceList, col, pawn.getColor(), pawn.getPieceType())
                    .size() >= 2) {
                score += pawn.getColor().getColorFactor() * doubledPawnConst;
            }
            if (Board.getPieceByColumnAndColorAndType(pieceList, col - 1, pawn.getColor(), pawn.getPieceType())
                    .size() == 0
                    && Board.getPieceByColumnAndColorAndType(pieceList, col + 1, pawn.getColor(), pawn.getPieceType())
                    .size() == 0) {
                score += pawn.getColor().getColorFactor() * isolatedPawnConst;
            }
        }
        if (noOfWhitePawns == 8) {
            score += fullPawnBonus;
        }
        if (noOfBlackPawns == 8) {
            score -= fullPawnBonus;
        }
        //System.out.println("PAWN ARRANGEMENT EVAL : "  + score);
        return score;
    }

    private double bishopStrengthEval(ArrayList<Piece> pieceList) {
        double score = 0;
        double incorrectBishopPosConst = 30;
        ArrayList<Piece> pawns = Board.getPieceByType(pieceList, PieceType.PAWN);
        ArrayList<Piece> bishops = Board.getPieceByType(pieceList, PieceType.BISHOP);
        int noOfBadBishopsWhite = 0;
        int noOfBadBishopsBlack = 0;

        for (Piece bishop : bishops) {
            PieceColor cellColorBishop = Board.getColorOfSquare(bishop.getPos());
            for (Piece pawn : pawns) {
                if (pawn.getColor() == bishop.getColor()) {
                    if (Board.getColorOfSquare(pawn.getPos()) == cellColorBishop) {
                        noOfBadBishopsWhite += (pawn.getColor() == PieceColor.WHITE) ? 1 : 0;
                        noOfBadBishopsBlack += (pawn.getColor() == PieceColor.BLACK) ? 1 : 0;
                    }
                }
            }
        }
        score = (noOfBadBishopsWhite * incorrectBishopPosConst) - (noOfBadBishopsBlack * incorrectBishopPosConst);
        //System.out.println("BISHOP STRENGTH EVAL : "  + score);
        return score;
    }

    private GamePhase assessGamePhase(ArrayList<Piece> pieceList) {
        double materialValue = matEval(pieceList, true, false);
        double maxMaterialValue = matEval(pieceList, false, true);
        boolean allQueensMoved = true;
        int openingMoves = 6;
        GamePhase gamePhase = GamePhase.OPENING;
        if (materialValue == maxMaterialValue) {
            gamePhase = GamePhase.OPENING;
        } else if (materialValue >= (maxMaterialValue / 4) && materialValue < maxMaterialValue) {
            gamePhase = GamePhase.MIDGAME;
        } else if (materialValue > 0 && materialValue < (maxMaterialValue / 4)) {
            gamePhase = GamePhase.ENDGAME;
        }
        if (Board.noOfMovesTotal(pieceList) < openingMoves) {
            gamePhase = GamePhase.OPENING;
        } else if (gamePhase == GamePhase.OPENING) {
            gamePhase = GamePhase.MIDGAME;
        }
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == PieceType.QUEEN) {
                if (piece.getNumberOfMoves() == 0) {
                    allQueensMoved = false;
                    break;
                }
            }
        }
        if (gamePhase == GamePhase.OPENING && allQueensMoved) {
            gamePhase = GamePhase.MIDGAME;
        }
        return gamePhase;
    }

    private boolean isKnightOnEdge(Piece piece) {
        return (piece.getPos().getCol() == 0 || piece.getPos().getCol() == 7);
    }

    private double posEval(ArrayList<Piece> pieceList) {
        double positionFactor = 50;
        int whiteScore = 0;
        int blackScore = 0;
        double overallScore = 0;
        for (Piece piece : pieceList) {
            int score = Piece.getPieceTableValue(piece, pieceList);
            if (piece.getColor() == PieceColor.BLACK) {
                blackScore += score;
            } else if (piece.getColor() == PieceColor.WHITE) {
                whiteScore += score;
            }
        }
        overallScore = positionFactor * (whiteScore - blackScore);
        //System.out.println("POS EVAL : "  + overallScore);
        return overallScore;
    }

    public double totalEvaluation(ArrayList<Piece> pieceList, PieceColor pieceColor) {
        return matEval(pieceList, false, false) + mobEval(pieceList)
                + developmentEval(pieceList) + pawnAdvancementEval(pieceList) + bishopPairEval(pieceList)
                + knightOnEdgeEval(pieceList) + kingEndGameEval(pieceList) + castlingEval(pieceList)
                + pawnArrangementEval(pieceList) + bishopStrengthEval(pieceList) + posEval(pieceList);

    }

    enum GamePhase {
        OPENING, MIDGAME, ENDGAME
    }

}
