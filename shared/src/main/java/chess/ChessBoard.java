package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    public ChessBoard() {
        board = new ChessPiece[8][8]; // array of array, initialized with null values
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
        // -1 is needed because internally arrays go from 0 to 7, but position notation is from 1 to 8
        // should have just converted it in the first level of UI
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        setUpPlayer(ChessGame.TeamColor.WHITE, board[1], board[0]);
        setUpPlayer(ChessGame.TeamColor.BLACK, board[6], board[7]);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ChessBoard clone = (ChessBoard) super.clone();
        ChessBoard pieces = new ChessBoard();

        for (int i = 0; i < clone.board.length; i++) {
            for (int j = 0; j < clone.board.length; j++) {
                pieces.board[i][j] = this.board[i][j]; // will that work?
            }
        }
        return pieces;
    }

    //ctr alt p -> refactors to be a parameter
    // ctrl alt m -> methodizes the thing

    private void setUpPlayer(ChessGame.TeamColor teamColor, ChessPiece[] pawnRow, ChessPiece[] baseRow) {
        for (int i = 0; i < 8; i++) {
            pawnRow[i] = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
        }
        baseRow[0] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
        baseRow[1] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        baseRow[2] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        baseRow[3] = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        baseRow[4] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        baseRow[5] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        baseRow[6] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        baseRow[7] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
    }

    private
    ChessPiece[][] board;

}
