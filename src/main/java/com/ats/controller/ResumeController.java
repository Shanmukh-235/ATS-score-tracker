package com.ats.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ats.service.ResumeScoreService;

@Controller
public class ResumeController {

	@Autowired
	private ResumeScoreService resumeScoreService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PostMapping("/checkScore")
	public String checkScore(
			@RequestParam("name") String name,
			@RequestParam("role") String role,
			@RequestParam("resume") MultipartFile resume,
			Model model) {
		try {
			if (resume.isEmpty()) {
				model.addAttribute("error", "Please upload a resume file.");
				return "index";
			}

			String content = resumeScoreService.extractTextFromResume(resume.getInputStream());
			int score = resumeScoreService.calculateScore(content, role);

			model.addAttribute("name", name);
			model.addAttribute("score", score);
			return "result";
		} catch (Exception e) {
			//e.printStackTrace();
			model.addAttribute("error", "Failed to process resume.");
			return "index";
		}
	}

}
