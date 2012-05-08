<?/////////////////////////////////////////////////////////////////////////////
//
//	Xdroid Initialization Script
//
//	Xdroid is a system to manipulate X Windows on an Android phone or
//	tablet.
//
///////////////////////////////////////////////////////////////////////////////
//
//	Copyright 2012, Brian Murphy
//	www.gurudigitalsolutions.com
//
///////////////////////////////////////////////////////////////////////////////
//
//	Xdroid Version: 20120323
//	Leave the above line alone if you want the Xdroid app to automatically
//	check the version of the scripts.
//
///////////////////////////////////////////////////////////////////////////////

define("HOME", $_SERVER["HOME"]);

if($_SERVER["HOME"] == "") { define("WEBPATH", $_SERVER["DOCUMENT_ROOT"]."/"); }
else { define("WEBPATH", $_SERVER["HOME"]."/xdroid/"); }


class xdroid
{
	public $UseViewPorts = false;
	public $ViewPortX = -1;
	public $ViewPortY = -1;
	public $ViewPortWidth = -1;
	
	public function GetDesktopCount()
	{
		$cmd = "wmctrl -d";
		$rezz = `$cmd`;
		$rezz = trim($rezz);
		$parts = explode("\n", $rezz);
		$DesktopCount = count($parts);
		
		if($DesktopCount == 1)
		{
			//	Only one desktop available, so is it using view ports?
			$dparts = explode("DG:", $rezz);
			$vparts = explode("VP:", $dparts[1]);

			$dres = trim($vparts[0]);

			$vparts = explode("WA:", $vparts[1]);
			$vres = trim($vparts[0]);
	
			$desk = explode("x", $dres);
			$view = explode(",", $vres);
			
			$this->ViewPortWidth = $desk[0];
			$this->ViewPortX = $view[0];
			$this->ViewPortY = $view[1];
			
			if($this->GetDesktopWidth() < $desk[0])
			{
				$this->UseViewPorts = true;
				$DesktopCount = ceil($desk[0] / $this->GetDesktopWidth());
			}
		}
		return $DesktopCount;
	}
	
	public function GetDesktopHeight()
	{
		$cmd = "xwininfo -stats -root | grep Height:";
		$rezz = `$cmd`;
		$parts = explode(":", $rezz);
		$DesktopHeight = trim($parts[1]);
		return $DesktopHeight;
	}
	
	public function GetDesktopWidth()
	{
		

		$cmd = "xwininfo -stats -root | grep Width:";
		$rezz = `$cmd`;
		$parts = explode(":", $rezz);
		$DesktopWidth = trim($parts[1]);
		return $DesktopWidth;
	}
	
	public function FocusOnDesktop($desktopid)
	{
		if($desktopid == null || $desktopid == "") { return; }
		$dcnt = $this->GetDesktopCount();
		if($desktopid < 0 || $desktopid >= $dcnt) { return; }
		
		if($this->UseViewPorts)
		{
			$cmd = "wmctrl -o ".($this->GetDesktopWidth() * $desktopid).",0";
			//echo "OK ".$cmd."\n";
			`$cmd`;
		}
		else
		{
			$cmd = "wmctrl -s ".$desktopid;
			//echo "OK ".$cmd."\n";
			`$cmd`;
		}
		
		sleep(1);
		$this->TakeScreenshot();
	}
	
	public function GetCurrentDesktop()
	{
		$this->GetDesktopCount();
		
		if($this->UseViewPorts)
		{
			if($this->ViewPortX < 2) { return 0; }
			return floor($this->ViewPortX / $this->GetDesktopWidth());
		}
		else
		{
			$cmd = "wmctrl -d | grep \"*\"";
			$rezz = `$cmd`;
			$rezz = trim($rezz);
		
		
			return substr($rezz, 0, 1);
		}
		
	}
	
	public function TakeScreenshot()
	{
		$desktopid = $this->GetCurrentDesktop();
		echo WEBPATH."images/desktops/".$desktopid.".jpg\n";
		$cmd = "scrot -t 10 -z ".WEBPATH."images/desktops/".$desktopid.".jpg";
		`$cmd`;
	}
	
	public function GetWindowList()
	{
		$tlist = array();
		$deskcount = $this->GetDesktopCount();
		
		$result = `wmctrl -l -G`;

		$result = trim($result);
		$parts = explode("\n", $result);
		$lps = 0;
		for($epart = 0; $epart < count($parts); $epart++)
		{
			$tline = $parts[$epart];
			$tline = str_replace('\t\t', '\t', $tline);
			$tline = str_replace("  ", " ", $tline);
			$tprts = explode(" ", $tline);
			$WindowID = substr($tline, 0, 10);
	
			$strtokens = '\t';
			$tokeone = strtok($tline, $strtokens);
			$toketwo = strtok($strtokens);
			//echo "Tokeone: ".$tokeone." :: ".$toketwo."\n";
	
			$cmd = "xwininfo -id ".$WindowID;//." | grep \"Absolute upper-left X:\"";
			//echo $cmd."\n";
			$nrezz = `$cmd | grep "Absolute upper-left X:"`;
			//echo "CMD: ".$cmd."\n";
			//$nrezz = `$cmd`;
			$nrezz = trim($nrezz);
			//echo "Rezz: ".$nrezz."\n";
			$resp = explode(":", $nrezz);
			$WindowX = trim($resp[1]);
	
			$nrezz = trim(`$cmd | grep "Absolute upper-left Y:"`);
			$resp = explode(":", $nrezz);
			$WindowY = trim($resp[1]);
	
			$nrezz = trim(`$cmd | grep "Width:"`);
			$resp = explode(":", $nrezz);
			$WindowWidth = trim($resp[1]);
	
			$nrezz = trim(`$cmd | grep "Height:"`);
			$resp = explode(":", $nrezz);
			$WindowHeight = trim($resp[1]);
	
			if($WindowHeight != "" && $WindowWidth != ""
			&& $WindowX != "" && $WindowY != "")
			{
				$twindow = array(
					"windowid"=>$WindowID,
					"desktopid"=>$tprts[1],
					"windowx"=>$WindowX,
					"windowy"=>$WindowY,
					"width"=>$WindowWidth,
					"height"=>$WindowHeight,
					"desktopname"=>"dtopname",
					"windowname"=>"window name spot"
				);
				
				if($this->UseViewPorts)
				{
					
					$curdesk = $this->GetCurrentDesktop();
					$newx = $WindowX + $this->ViewPortX;//
					
					if($this->ViewPortX > 0)
					{
						$twindow['desktopid'] = ceil($newx / $this->ViewPortX);// - 1;
					}
					else
					{
						$twindow['desktopid'] = 0;
					}
						
					//echo "New x: ".$newx." ::  dID: ".$twindow['desktopid']." :: Desktop: ".$this->GetCurrentDesktop().": curdesk ".$curdesk.", vpx ".$this->ViewPortX."\n";
					
				}
				
				$tlist[] = $twindow;
				
				$lps++;
			}
		}
		
		return $tlist;
	}
	
	public function CloseWindow($windowid)
	{
		if($windowid == null || $windowid == "") { return; }
		$cmd = "wmctrl -i -c ".$windowid;
		`$cmd`;
	}
	
	public function FullScreen($windowid)
	{
		if($windowid == null || $windowid == "") { return; }
		$cmd = "wmctrl -i -r ".$windowid." -b toggle,fullscreen";
		`$cmd`;
	}
	
	public function Focus($windowid)
	{
		if($windowid == null || $windowid == "") { return; }

		$cmd = "wmctrl -i -a ".$windowid;
		`$cmd`;
	}
	
	public function GetMousePosition()
	{
		$cmd = "xdotool getmouselocation";
		$rezz = `$cmd`;

		$parts = explode(" ", $rezz);

		$mc = array(
			'x'=>trim(str_replace("x:", "", $parts[0])),
			'y'=>trim(str_replace("y:", "", $parts[1]))
		);
		return $mc;
	}
	
	public function SetMousePosition($mx, $my)
	{
		if($mx == null || $mx == "") { return; }
		if($my == null || $my == "") { return; }
		
		$cmd = "xte 'mousemove ".$mx." ".$my."'";
		`$cmd`;
	}
	
	public function SetMousePositionR($mx, $my)
	{
		if($mx == null || $mx == "") { return; }
		if($my == null || $my == "") { return; }
		
		$curmoucoords = $this->GetMousePosition();
		$mx = $curmoucoords['x'] + $mx;
		$my = $curmoucoords['y'] + $my;
		
		$cmd = "xte 'mousemove ".$mx." ".$my."'";
		`$cmd`;
	}
	
	public function ClickMouse($button, $times)
	{
		if($button == null || $button == "") { return; }
		if($times == null || $times == "") { return; }
		
		for($eclick = 0; $eclick < $times; $eclick++)
		{
			$cmd = "xte 'mousedown ".$button."'";
			`$cmd`;
			$cmd = "xte 'mouseup ".$button."'";
			`$cmd`;
		}
	}
	
	public function DesktopThumbnail($desktopid)
	{
		if($desktopid == $this->GetCurrentDesktop())
		{
			//	Yeah, the phone and the computer are actually on the same
			//	desktop, so let's just get a new screen shot right now
			$this->TakeScreenshot();

			//echo "OK A new screenshot of desktop ".$desktopid." was taken.";
			echo WEBPATH."images/desktops/".$desktopid."-thumb.jpg";
			//header("Location: /images/desktops/".$desktopid.".jpg");
		}
		else
		{
			//	The user is trying to get thumbnails for a screen that the
			//	computer isn't on.  So, if any are cached already, we'll use
			//	those.
			//	<<==== - Someday - ====>>
	
			$tfile = WEBPATH."images/desktops/".$desktopid."-thumb.jpg";
			if(file_exists($tfile))
			{
				echo WEBPATH."images/desktops/".$desktopid."-thumb.jpg";
			}
			else
			{
				echo "FAIL";
			}
		}
	}
	
	public function WindowThumbnail($desktopid, $windowid, $wx, $wy, $ww, $wh)
	{
		$filename = WEBPATH."images/desktops/".$desktopid.".jpg";
		if(file_exists($filename))
		{
			list($currentwidth, $currentheight) = getimagesize($filename);

			$canvas = imagecreatetruecolor($ww, $wh);
			$current_image = imagecreatefromjpeg($filename);
			imagecopy($canvas, $current_image, 0, 0, $wx, $wy, $currentwidth, $currentheight);
			//imagejpeg($canvas, $filename, 100);
			//header("Content-type: Image/jpeg");
			imagejpeg($canvas, WEBPATH."images/winthumbs/".$windowid.".jpg");
			echo WEBPATH."images/winthumbs/".$windowid.".jpg";
		}
		else
		{
			echo "FAIL";
		}
	}
	
	public function MaximizeWindow($windowid)
	{
		if($windowid == null || $windowid == "") { return; }
		$cmd = "wmctrl -i -r ".$windowid." -b toggle,maximized_vert,maximized_horz";
		`$cmd`;
	}
}

?>
