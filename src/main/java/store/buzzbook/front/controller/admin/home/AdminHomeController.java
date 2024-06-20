package store.buzzbook.front.controller.admin.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
	@GetMapping("/home")
	public String adminHome() {
		return "admin/index";
	}

	@GetMapping("/components/avatars")
	public String adminAvatars() {
		return "admin/components/avatars";
	}

	@GetMapping("/components/buttons")
	public String adminButtons() {
		return "admin/components/buttons";
	}

	@GetMapping("/components/gridsystem")
	public String adminGridSystem() {
		return "admin/components/gridsystem";
	}

	@GetMapping("/components/panels")
	public String adminPanels() {
		return "admin/components/panels";
	}

	@GetMapping("/components/notifications")
	public String adminNotifications() {
		return "admin/components/notifications";
	}

	@GetMapping("/components/sweetalert")
	public String adminSweetAlert() {
		return "admin/components/sweetalert";
	}

	@GetMapping("/components/font-awesome-icons")
	public String adminFontAwesomeIcons() {
		return "admin/components/font-awesome-icons";
	}

	@GetMapping("/components/simple-line-icons")
	public String adminSimpleLineIcons() {
		return "admin/components/simple-line-icons";
	}

	@GetMapping("/components/typography")
	public String adminTypography() {
		return "admin/components/typography";
	}

	@GetMapping("/sidebar-style-2")
	public String adminSidebarStyle2() {
		return "admin/sidebar-style-2";
	}

	@GetMapping("/icon-menu")
	public String adminIconMenu() {
		return "admin/icon-menu";
	}

	@GetMapping("/tables/tables")
	public String adminTables() {
		return "admin/tables/tables";
	}

	@GetMapping("/tables/datatables")
	public String adminTablesDatatables() {
		return "admin/tables/datatables";
	}

	@GetMapping("/maps/googlemaps")
	public String adminGoogleMaps() {
		return "admin/maps/googlemaps";
	}

	@GetMapping("/maps/jsvectormap")
	public String adminJsVectorMap() {
		return "admin/maps/jsvectormap";
	}

	@GetMapping("/charts/charts")
	public String adminCharts() {
		return "admin/charts/charts";
	}

	@GetMapping("/charts/sparkline")
	public String adminSparkline() {
		return "admin/charts/sparkline";
	}

	@GetMapping("/widgets")
	public String adminWidgets() {
		return "admin/widgets";
	}


}
