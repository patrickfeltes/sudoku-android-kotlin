package com.patrickfeltes.sudokuyoutube.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import com.patrickfeltes.sudokuyoutube.R
import com.patrickfeltes.sudokuyoutube.game.Cell
import com.patrickfeltes.sudokuyoutube.view.custom.SudokuBoardView
import com.patrickfeltes.sudokuyoutube.viewmodel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
                sevenButton, eightButton, nineButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary) else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary) else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}
