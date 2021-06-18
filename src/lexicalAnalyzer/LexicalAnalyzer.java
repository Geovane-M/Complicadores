package lexicalAnalyzer;

import java.io.IOException;

import ErrorWarnings.EmptyCharacter;
import ErrorWarnings.LexicalError;
import ErrorWarnings.ManyCharacters;
import ErrorWarnings.NoTarget;
import ErrorWarnings.OutOfRange;

public class LexicalAnalyzer {
	private int currentIndexMark;
	private String currentValue;
	private Token currentToken;
	private Tag currentMark;
	private Pointer pointer;
	private boolean flag;
	private char cr_char;
	private int column;
	private int line;

	public LexicalAnalyzer() throws IOException, NoTarget {
		this.pointer = Pointer.getInstance();
		if (this.pointer.isEOF()) {
			throw new NoTarget();
		} else {
			this.line = 1;
			this.column = 0;
			this.flag = false;
			this.cr_char = this.nextChar();
		}
	}

	public Token nextToken() throws OutOfRange, ManyCharacters, LexicalError, EmptyCharacter {
		if (this.flag) {
			this.currentMark = Tag.EOF;
			this.currentValue = Tag.EOF.getDescription();
			this.currentToken = new Token(this.currentMark, this.currentValue);
		} else {
			this.getNextToken();
			this.currentToken = new Token(this.currentMark, this.currentValue);
			this.currentToken.setColumn(this.currentIndexMark);
			this.currentToken.setLine(this.line);
		}
		
		return this.currentToken;
	}

	private void getNextToken() throws OutOfRange, ManyCharacters, LexicalError, EmptyCharacter {
		this.currentValue = "";
		do {
			if (this.isNewLine()) {
				this.updateLine();
				if (!this.pointer.isEOF())
					this.cr_char = this.nextChar();
			} else if (Character.isSpaceChar(this.cr_char) || this.cr_char == '\t') {
				if(this.cr_char == '\t')this.column += 4;
				this.cr_char = this.nextChar();
			} else if (Character.isDigit(this.cr_char)) {
				this.updateMarkLocation();
				this.currentValue += this.cr_char;
				this.currentMark = Tag.INTEGER;
				this.endNumeric();
				return;
			} else if (this.isComment()) {
				//apenas ignora o comentário
			} else if (this.isArithmeticOp()) {
				break;
			} else if (this.isRelationalOp()) {
				break;
			} else if (this.isSpecialCharacter()) {
				break;
			} else if (Character.isLetter(this.cr_char) || this.cr_char == '_') {
				this.updateMarkLocation();
				this.currentValue += this.cr_char;
				this.endIdOrReserved();
				return;
			} else if (this.cr_char == '\'') {
				this.updateMarkLocation();
				this.currentValue += this.cr_char;
				this.currentMark = Tag.CHARACTER;
				this.endCharacter();
				return;
			} else {
				this.updateMarkLocation();
				throw new LexicalError(this.line, this.lineError());
			}
		} while (true);
		if (!this.pointer.isEOF()) {
			this.cr_char = this.nextChar();
		}else {
			this.flag = true;
		}
	}

	private boolean isComment() throws LexicalError {
		boolean okay = true;
		if (this.cr_char == '/'){
			char helper = this.pointer.nextChar();
			if(helper == '/') {
				this.endCommentLine();
			}else if(helper == '*') {
				this.endCommentBlock();
			}else {
				this.pointer.comeBack(1);
				okay = false;
			}
		}else {
			okay = false;
		}
		return okay;
	}

	private void endCommentLine() {
		while (true) {
			if (this.isNewLine()) {
				//this.updateLine();
				return;
			}
			if (!this.pointer.isEOF())
				this.cr_char = this.nextChar();
		}
	}

	private void endCommentBlock() throws LexicalError {
		while (true) {
			if (this.isNewLine()) {
				this.updateLine();
			}
			if (this.cr_char == '*' && this.nextChar() == '/') {
				if (this.pointer.isEOF() && this.currentMark.equals(null)) {
					throw new LexicalError("Error: Without code\n");
				} else {
					if (!this.pointer.isEOF())
						this.cr_char = this.nextChar();
					return;
				}
			} else if (this.pointer.isEOF()) {
				throw new LexicalError("Error!: Unterminated comment\n");
			} else {
				this.cr_char = this.nextChar();
			}
		}
	}

	private void endNumeric() throws OutOfRange {
		while (!this.pointer.isEOF()) {
			this.cr_char = this.nextChar();
			if (Character.isDigit(this.cr_char)) {
				this.currentValue += this.cr_char;
				this.currentMark = Tag.INTEGER;
			} else if (this.cr_char == '.') {
				this.currentMark = Tag.FLOATING_POINT;
				this.currentValue += this.cr_char;
				this.endFloat();
				break;
			} else {
				if (Double.parseDouble(this.currentValue) < -2147483648d
						|| Double.parseDouble(this.currentValue) > 2147483647d)
					throw new OutOfRange(this.currentValue, this.currentMark.getDescription(), this.line,
							this.lineError());
				break;
			}
		}
	}

	private void endFloat() throws OutOfRange {
		while (!this.pointer.isEOF()) {
			this.cr_char = this.nextChar();
			if (Character.isDigit(this.cr_char)) {
				this.currentValue += this.cr_char;
			} else if (this.cr_char == 'f') {
				this.currentValue += this.cr_char;
				this.cr_char = this.nextChar();
				break;
			} else {
				if (Double.parseDouble(this.currentValue) < 1.2E-38d
						|| Double.parseDouble(this.currentValue) > 3.4E+38d)
					throw new OutOfRange(this.currentValue, this.currentMark.getDescription(), this.line,
							this.lineError());
				break;
			}
		}
	}

	private boolean isArithmeticOp() {
		boolean okay = true;
		switch (this.cr_char) {
		case '=':
			this.updateMarkLocation();
			if (this.pointer.nextChar() == '=') {
				this.pointer.comeBack(1);
				okay = false;
			} else {
				this.pointer.comeBack(1);
				this.currentValue += this.cr_char;
				this.currentMark = Tag.ARI_OP_ATTRIBUTION;
			}
			break;
		case '+':
			this.updateMarkLocation();
			this.currentMark = Tag.ARI_OP_ADDITION;
			this.currentValue += this.cr_char;
			break;
		case '-':
			this.updateMarkLocation();
			this.currentMark = Tag.ARI_OP_SUBTRACTION;
			this.currentValue += this.cr_char;
			break;
		case '*':
			this.updateMarkLocation();
			this.currentMark = Tag.ARI_OP_MULTIPLICATION;
			this.currentValue += this.cr_char;
			break;
		case '/':
			this.updateMarkLocation();
			this.currentMark = Tag.ARI_OP_DIVISION;
			this.currentValue += this.cr_char;
			break;
		default:
			okay = false;
			break;
		}
		return okay;
	}

	private boolean isRelationalOp() {
		boolean okay = true;
		switch (this.cr_char) {
		case '=':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.cr_char = this.nextChar();
			if (this.cr_char == '=') {
				this.currentValue += this.cr_char;
				this.currentMark = Tag.REL_OP_EQUALS;
			} else {
				this.pointer.comeBack(1);
				this.column--;
				okay = false;
			}
			break;
		case '<':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.cr_char = this.nextChar();
			if (this.cr_char == '=') {
				this.currentValue += this.cr_char;
				this.currentMark = Tag.REL_OP_LESS_THAN_OR_EQUALS_TO;
			} else {
				this.pointer.comeBack(1);
				this.column--;
				this.currentMark = Tag.REL_OP_LESS_THAN;
			}
			break;
		case '>':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.cr_char = this.nextChar();
			if (this.cr_char == '=') {
				this.currentValue += this.cr_char;
				this.currentMark = Tag.REL_OP_GREATER_THAN_OR_EQUALS_TO;
			} else {
				this.pointer.comeBack(1);
				this.column--;
				this.currentMark = Tag.REL_OP_GREATER_THAN;
			}
			break;
		case '!':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.cr_char = this.nextChar();
			if (this.cr_char == '=') {
				this.currentValue += this.cr_char;
				this.currentMark = Tag.REL_OP_NOT_EQUAL_TO;
			} else {
				this.pointer.comeBack(1);
				this.column--;
				this.currentMark = Tag.OP_COND_NOT;
			}
			break;
		default:
			okay = false;
			break;
		}
		return okay;
	}

	private boolean isSpecialCharacter() {
		boolean okay = true;
		switch (this.cr_char) {
		case '(':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_OPEN_PARENTHESES;
			break;
		case ')':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_CLOSE_PARENTHESES;
			break;
		case '{':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_OPEN_BRACES;
			break;
		case '}':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_CLOSE_BRACES;
			break;
		case ',':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_COMMA;
			break;
		case ';':
			this.updateMarkLocation();
			this.currentValue += this.cr_char;
			this.currentMark = Tag.SP_CHAR_SEMICOLON;
			break;
		default:
			okay = false;
			break;
		}
		return okay;
	}

	private void endIdOrReserved() {
		while (!this.pointer.isEOF()) {
			this.cr_char = this.nextChar();
			if (Character.isLetterOrDigit(this.cr_char) || this.cr_char == '_') {
				this.currentValue += this.cr_char;
			} else {
				break;
			}
		}
		if (new ReservedWords().containsKey(this.currentValue)) {
			this.currentMark = new ReservedWords().getTag(this.currentValue);
		} else {
			this.currentMark = Tag.ID;
		}
	}

	private void endCharacter() throws ManyCharacters, LexicalError, EmptyCharacter {
		this.cr_char = this.nextChar();
		if (Character.isLetterOrDigit(this.cr_char) || Character.isSpaceChar(this.cr_char)) {
			this.currentValue += this.cr_char;
			this.cr_char = this.nextChar();
			if (this.cr_char == '\'') {
				this.currentValue += this.cr_char;
				this.cr_char = this.nextChar();
			} else {
				throw new ManyCharacters(this.line, this.lineError());
			}
		} else if (this.pointer.nextChar() == '\'') {
			throw new EmptyCharacter(this.line, this.lineError());
		} else {
			this.updateMarkLocation();
			throw new LexicalError(this.line, this.lineError());
		}
	}

	private boolean isNewLine() {
		// "new line" case in windows
		if (this.cr_char == '\r') {
			if (!this.pointer.isEOF())
				this.cr_char = this.pointer.nextChar();
			if (this.cr_char == '\n') {
				return true;
			}
			// other cases of "new line"
		} else if (this.cr_char == '\n') {
			return true;
		}
		return false;
	}

	private void updateLine() {
		this.line++;
		this.column = 0;
		this.pointer.nextLine();
	}

	private void updateMarkLocation() {
		this.currentIndexMark = this.column;
	}

	private Character nextChar() {
		this.column++;
		return this.pointer.nextChar();
	}

	public String lineError() {
		String stg = "\n";
		stg += this.pointer.nextLine();
		stg = stg.replace("\t", "     ");
		stg += "\n";
		for (int i = 0; i < this.currentIndexMark; i += 1) {
			if(i > 0) {
				stg += "-";
			}
		}
		stg += "^";
		return stg;
	}

	public void restartPointer() throws IOException{
		this.line = 1;
		this.column = 0;
		this.flag = false;
		this.updateMarkLocation();
		this.pointer = this.pointer.restartInstance();
	}
}
