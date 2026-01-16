package technikal.task.fishmarket.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.service.FileService;
import technikal.task.fishmarket.services.FishRepository;

import static java.util.function.Predicate.not;

@Slf4j
@Controller
@RequestMapping("/fish")
public class FishController {

	private FishRepository repo;
	private FileService fileService;
	private MessageSource messageSource;

	@Autowired
	public FishController(FileService fileService, FishRepository repo, MessageSource messageSource) {
		this.fileService = fileService;
		this.repo = repo;
		this.messageSource = messageSource;
	}
	
	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "index";
	}
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}
	
	@PostMapping("/delete")
	public String deleteFish(@RequestParam int id) {

		List<String> fileNames;

		try {
			Fish fish = repo.findById(id).orElseThrow(() -> new RuntimeException("Рибу не знайдено"));

			fileNames = fish.getImages().stream().map(FishImage::getFileName).toList();
			repo.delete(fish);
			fileService.deleteFiles(fileNames);

		} catch(Exception ex) {
			log.error("Помилка при видаленні риби: {}", ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не вдалося видалити рибу з бази даних.");
		}
		
		return "redirect:/fish";
	}
	
	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
		
		if(fishDto.getImageFiles() == null || fishDto.getImageFiles().get(0).isEmpty()){
			result.addError(new FieldError("fishDto", "imageFiles", messageSource.getMessage("fish.images.required", null, LocaleContextHolder.getLocale())));
		}
		
		if(result.hasErrors()) {
			return "createFish";
		}
		
		Fish fish = new Fish();
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setCatchDate(new Date());

		List<String> savedFiles = new ArrayList<>();

		try {
			List<MultipartFile> applicableImageFiles = fishDto.getImageFiles().stream()
					.filter(not(MultipartFile::isEmpty))
					.toList();

			for(MultipartFile image : applicableImageFiles) {
				String imageFileName = fileService.saveFile(image);
				savedFiles.add(imageFileName);

				FishImage fishImage = new FishImage();
				fishImage.setFileName(imageFileName);
				fishImage.setFish(fish);

				fish.getImages().add(fishImage);
			}

			repo.save(fish);

		} catch(Exception ex) {
			log.error("Помилка при збереженні риби: {}", ex);
			fileService.deleteFiles(savedFiles);
			result.addError(new ObjectError("global", messageSource.getMessage("error.global.save", null, LocaleContextHolder.getLocale())));
			return "createFish";
		}
		return "redirect:/fish";
	}
}
