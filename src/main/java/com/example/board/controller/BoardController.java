package com.example.board.controller;

import com.example.board.BoardApplication;
import com.example.board.dto.BoardDTO;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    //게시글 작성
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {
        boardService.save(boardDTO);
        return "index";
    }

    //게시글 목록
    @GetMapping("/")
    public String findAll(Model model) {
        //Model은 Spring MVC에서 컨트롤러와 뷰(View) 사이에 데이터를 전달하는 데 사용
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    //게시글 조회
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        /*
            해당 게시글 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
        */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "detail";
    }

    //게시글 수정 조회
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    //게시글 수정
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";
    }

    //게시글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    //페이징 처리 (/board/paging?page=1)
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1)Pageable pageable, Model model) {
        Page<BoardDTO> boardList = boardService.paging(pageable);

        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개 : 7,8

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";

    }
}
