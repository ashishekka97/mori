package me.ashishekka.mori.biome.compiler

import me.ashishekka.mori.engine.core.util.OpCode
import java.util.*

/**
 * Compiles human-readable math expressions into Mori Rule Engine Bytecode.
 * Supports infix notation, standard operators, functions, and fact access.
 * 
 * Example: "10 + 2 * sin(time)" -> [PUSH_CONST, 10, PUSH_CONST, 2, GET_TIME, SIN, MUL, ADD]
 * 
 * DESIGN PRINCIPLE:
 * This compiler is designed to be "Fail-Safe." If an expression is malformed,
 * it returns an empty bytecode array, allowing the Engine to safely skip it.
 */
object ExpressionCompiler {

    /**
     * Compiles a string expression into a postfix IntArray of OpCodes and parameters.
     * Returns an empty array if the expression is malformed or empty.
     */
    fun compile(expression: String?): IntArray {
        if (expression.isNullOrBlank()) return intArrayOf()
        
        return try {
            val tokens = tokenize(expression)
            val postfix = infixToPostfix(tokens)
            val bytecode = generateBytecode(postfix)
            if (validateBytecode(bytecode)) bytecode else intArrayOf()
        } catch (e: Exception) {
            // Logically malformed expression, return empty bytecode
            intArrayOf()
        }
    }

    /**
     * Validates that the bytecode will not cause a stack underflow.
     * Simulates the stack pointer during execution.
     */
    private fun validateBytecode(bytecode: IntArray): Boolean {
        if (bytecode.isEmpty()) return false
        var stackDepth = 0
        var i = 0
        while (i < bytecode.size) {
            val opcode = bytecode[i++]
            when (opcode) {
                OpCode.PUSH_CONST -> {
                    stackDepth++
                    i++ // Skip constant value
                }
                OpCode.GET_TIME -> stackDepth++
                OpCode.GET_STATE -> {
                    stackDepth++
                    i++ // Skip index
                }
                OpCode.GET_SIGNAL -> {
                    stackDepth++
                    i++ // Skip index
                }
                
                // Binary Operators (Pop 2, Push 1)
                OpCode.ADD, OpCode.SUB, OpCode.MUL, OpCode.DIV, OpCode.MOD,
                OpCode.AND, OpCode.OR, OpCode.STEP -> {
                    if (stackDepth < 2) return false
                    stackDepth -= 1
                }
                
                // Unary Operators (Pop 1, Push 1)
                OpCode.SIN, OpCode.COS, OpCode.ABS, OpCode.SQRT, OpCode.NOISE,
                OpCode.EASE_IN_OUT, OpCode.EASE_BACK, OpCode.EASE_ELASTIC -> {
                    if (stackDepth < 1) return false
                }
                
                // Ternary / Macros
                OpCode.LERP, OpCode.MIX_OKLAB, OpCode.CLAMP -> {
                    if (stackDepth < 3) return false
                    stackDepth -= 2
                }
                
                OpCode.IF_GT, OpCode.OSCILLATE -> {
                    if (stackDepth < 4) return false
                    stackDepth -= 3
                }
                
                OpCode.REMAP -> {
                    if (stackDepth < 5) return false
                    stackDepth -= 4
                }
            }
        }
        return stackDepth == 1
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        // Improved regex to capture all valid tokens including floats, identifiers, and operators
        val regex = Regex("""([0-9]*\.?[0-9]+)|([a-zA-Z_][a-zA-Z0-9_]*)|(\[)|(\])|(\()|(\))|(\+|-|\*|/|%)|(,)""")
        regex.findAll(expression).forEach { match ->
            tokens.add(match.value)
        }
        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()

        val precedence = mapOf(
            "+" to 1, "-" to 1,
            "*" to 2, "/" to 2, "%" to 2
        )

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            when {
                // Number
                token.first().isDigit() || (token.length > 1 && token.first() == '.' && token[1].isDigit()) -> output.add(token)
                
                // Function or Fact
                token.all { it.isLetter() || it == '_' } -> {
                    if (i + 1 < tokens.size && (tokens[i + 1] == "(" || tokens[i + 1] == "[")) {
                        operators.push(token)
                    } else if (token == "time") {
                        output.add("time")
                    } else {
                        // Unknown variable, push 0
                        output.add("0")
                    }
                }

                // Operator
                token in precedence.keys -> {
                    while (operators.isNotEmpty() && operators.peek() in precedence.keys &&
                        precedence[operators.peek()]!! >= precedence[token]!!) {
                        output.add(operators.pop())
                    }
                    operators.push(token)
                }

                // Left Parenthesis / Bracket
                token == "(" || token == "[" -> operators.push(token)

                // Right Parenthesis / Bracket
                token == ")" || token == "]" -> {
                    val opening = if (token == ")") "(" else "["
                    while (operators.isNotEmpty() && operators.peek() != opening) {
                        output.add(operators.pop())
                    }
                    if (operators.isEmpty()) throw IllegalArgumentException("Mismatched parentheses")
                    operators.pop() // Pop the opening bracket
                    
                    // If it was a function call, pop the function name
                    if (operators.isNotEmpty() && operators.peek().all { it.isLetter() || it == '_' }) {
                        output.add(operators.pop())
                    }
                }
                
                // Comma (Function arguments separator)
                token == "," -> {
                    while (operators.isNotEmpty() && operators.peek() != "(") {
                        output.add(operators.pop())
                    }
                    if (operators.isEmpty()) throw IllegalArgumentException("Comma outside of function")
                }
            }
            i++
        }

        while (operators.isNotEmpty()) {
            val op = operators.pop()
            if (op == "(" || op == "[" || op == ")" || op == "]") {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(op)
        }

        return output
    }

    private fun generateBytecode(postfix: List<String>): IntArray {
        val bytecode = mutableListOf<Int>()
        
        postfix.forEach { token ->
            when (token) {
                // --- Values ---
                "time" -> bytecode.add(OpCode.GET_TIME)
                
                // --- Operators ---
                "+" -> bytecode.add(OpCode.ADD)
                "-" -> bytecode.add(OpCode.SUB)
                "*" -> bytecode.add(OpCode.MUL)
                "/" -> bytecode.add(OpCode.DIV)
                "%" -> bytecode.add(OpCode.MOD)
                
                // --- Functions ---
                "sin" -> bytecode.add(OpCode.SIN)
                "cos" -> bytecode.add(OpCode.COS)
                "abs" -> bytecode.add(OpCode.ABS)
                "sqrt" -> bytecode.add(OpCode.SQRT)
                "remap" -> bytecode.add(OpCode.REMAP)
                "clamp" -> bytecode.add(OpCode.CLAMP)
                "step" -> bytecode.add(OpCode.STEP)
                "lerp" -> bytecode.add(OpCode.LERP)
                "oscillate" -> bytecode.add(OpCode.OSCILLATE)
                "noise" -> bytecode.add(OpCode.NOISE)
                "mix_oklab" -> bytecode.add(OpCode.MIX_OKLAB)
                "ease_in_out" -> bytecode.add(OpCode.EASE_IN_OUT)
                "ease_back" -> bytecode.add(OpCode.EASE_BACK)
                "ease_elastic" -> bytecode.add(OpCode.EASE_ELASTIC)
                
                // --- Special: fact[n] ---
                "fact" -> bytecode.add(OpCode.GET_STATE)
                
                else -> {
                    // Constant Number
                    val value = token.toFloatOrNull()
                    if (value != null) {
                        bytecode.add(OpCode.PUSH_CONST)
                        bytecode.add(value.toBits())
                    } else {
                        // Unknown or error fallback: Push 0
                        bytecode.add(OpCode.PUSH_CONST)
                        bytecode.add(0f.toBits())
                    }
                }
            }
        }
        
        return bytecode.toIntArray()
    }
}
