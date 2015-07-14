package org.de.jmg.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.de.jmg.lib.lib.libString;

/// <summary>
/// Rich Text Stripper
/// </summary>
/// <remarks>
/// Translated from Python located at:
/// http://stackoverflow.com/a/188877/448
/// </remarks>
/// Translated from c# (http://chrisbenard.net/2014/08/20/Extract-Text-from-RTF-in-.Net/RichTextStripper.cs)
public class RichTextStripper
{
	private static class StackEntry
	{
		public int NumberOfCharactersToSkip;
		public boolean Ignorable;

		public StackEntry(int numberOfCharactersToSkip, boolean ignorable)
		{
			NumberOfCharactersToSkip = numberOfCharactersToSkip;
			Ignorable = ignorable;
		}
	}

	private static final Pattern _rtfRegex = Pattern.compile
			("\\\\([a-z]{1,32})(-?\\d{1,10})?[ ]?|\\\\'([0-9a-f]{2})|\\\\([^a-z])|([{}])|[\\r\\n]+|(.)"
					, Pattern.DOTALL|Pattern.CASE_INSENSITIVE) ;

	private static final List<String> destinations = Arrays.asList(
		"aftncn","aftnsep","aftnsepc","annotation","atnauthor","atndate","atnicn","atnid",
		"atnparent","atnref","atntime","atrfend","atrfstart","author","background",
		"bkmkend","bkmkstart","blipuid","buptim","category","colorschememapping",
		"colortbl","comment","company","creatim","datafield","datastore","defchp","defpap",
		"do","doccomm","docvar","dptxbxtext","ebcend","ebcstart","factoidname","falt",
		"fchars","ffdeftext","ffentrymcr","ffexitmcr","ffformat","ffhelptext","ffl",
		"ffname","ffstattext","field","file","filetbl","fldinst","fldrslt","fldtype",
		"fname","fontemb","fontfile","fonttbl","footer","footerf","footerl","footerr",
		"footnote","formfield","ftncn","ftnsep","ftnsepc","g","generator","gridtbl",
		"header","headerf","headerl","headerr","hl","hlfr","hlinkbase","hlloc","hlsrc",
		"hsv","htmltag","info","keycode","keywords","latentstyles","lchars","levelnumbers",
		"leveltext","lfolevel","linkval","list","listlevel","listname","listoverride",
		"listoverridetable","listpicture","liststylename","listtable","listtext",
		"lsdlockedexcept","macc","maccPr","mailmerge","maln","malnScr","manager","margPr",
		"mbar","mbarPr","mbaseJc","mbegChr","mborderBox","mborderBoxPr","mbox","mboxPr",
		"mchr","mcount","mctrlPr","md","mdeg","mdegHide","mden","mdiff","mdPr","me",
		"mendChr","meqArr","meqArrPr","mf","mfName","mfPr","mfunc","mfuncPr","mgroupChr",
		"mgroupChrPr","mgrow","mhideBot","mhideLeft","mhideRight","mhideTop","mhtmltag",
		"mlim","mlimloc","mlimlow","mlimlowPr","mlimupp","mlimuppPr","mm","mmaddfieldname",
		"mmath","mmathPict","mmathPr","mmaxdist","mmc","mmcJc","mmconnectstr",
		"mmconnectstrdata","mmcPr","mmcs","mmdatasource","mmheadersource","mmmailsubject",
		"mmodso","mmodsofilter","mmodsofldmpdata","mmodsomappedname","mmodsoname",
		"mmodsorecipdata","mmodsosort","mmodsosrc","mmodsotable","mmodsoudl",
		"mmodsoudldata","mmodsouniquetag","mmPr","mmquery","mmr","mnary","mnaryPr",
		"mnoBreak","mnum","mobjDist","moMath","moMathPara","moMathParaPr","mopEmu",
		"mphant","mphantPr","mplcHide","mpos","mr","mrad","mradPr","mrPr","msepChr",
		"mshow","mshp","msPre","msPrePr","msSub","msSubPr","msSubSup","msSubSupPr","msSup",
		"msSupPr","mstrikeBLTR","mstrikeH","mstrikeTLBR","mstrikeV","msub","msubHide",
		"msup","msupHide","mtransp","mtype","mvertJc","mvfmf","mvfml","mvtof","mvtol",
		"mzeroAsc","mzeroDesc","mzeroWid","nesttableprops","nextfile","nonesttables",
		"objalias","objclass","objdata","object","objname","objsect","objtime","oldcprops",
		"oldpprops","oldsprops","oldtprops","oleclsid","operator","panose","password",
		"passwordhash","pgp","pgptbl","picprop","pict","pn","pnseclvl","pntext","pntxta",
		"pntxtb","printim","private","propname","protend","protstart","protusertbl","pxe",
		"result","revtbl","revtim","rsidtbl","rxe","shp","shpgrp","shpinst",
		"shppict","shprslt","shptxt","sn","sp","staticval","stylesheet","subject","sv",
		"svb","tc","template","themedata","title","txe","ud","upr","userprops",
		"wgrffmtfilter","windowcaption","writereservation","writereservhash","xe","xform",
		"xmlattrname","xmlattrvalue","xmlclose","xmlname","xmlnstbl",
		"xmlopen"
	);

	private static final HashMap<String, String> specialCharacters = new HashMap<String, String>()
	{/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
		put( "par", "\n" );
		put( "sect", "\n\n" );
		put( "page", "\n\n" );
		put( "line", "\n" );
		put( "tab", "\t" );
		put( "emdash", "\u2014" );
		put( "endash", "\u2013" );
		put( "emspace", "\u2003" );
		put( "enspace", "\u2002" );
		put( "qmspace", "\u2005" );
		put( "bullet", "\u2022" );
		put( "lquote", "\u2018" );
		put( "rquote", "\u2019" );
		put( "ldblquote", "\u201C" );
		put( "rdblquote", "\u201D" ); 
	}};
	/// <summary>
	/// Strip RTF Tags from RTF Text
	/// </summary>
	/// <param name="inputRtf">RTF formatted text</param>
	/// <returns>Plain text from RTF</returns>
	public static String StripRichTextFormat(String inputRtf) throws Exception
	{
		if (inputRtf == null)
		{
			return null;
		}

		String returnString = null;

		Stack<StackEntry> stack = new Stack<StackEntry>();
		boolean ignorable = false;              // Whether this group (and all inside it) are "ignorable".
		int ucskip = 1;                      // Number of ASCII characters to skip after a unicode character.
		int curskip = 0;                     // Number of ASCII characters left to skip
		List<String> outList = new ArrayList<String>();    // Output buffer.

		Matcher matches = _rtfRegex.matcher(inputRtf);

		if (matches.find())
		{
			do
			{
				String word = matches.group(1);
				String arg = matches.group(2); // match.Groups[2].Value;
				String hex = matches.group(3); //.Value;
				String character = matches.group(4); //.Value;
				String brace = matches.group(5); //.Value;
				String tchar = matches.group(6) ; //.Value;

				if (!libString.IsNullOrEmpty(brace))
				{
					curskip = 0;
					if (brace.equalsIgnoreCase("{"))
					{
						// Push state
						stack.push(new RichTextStripper.StackEntry(ucskip, ignorable));
					}
					else if (brace.equalsIgnoreCase("}"))
					{
						// Pop state
						StackEntry entry = stack.pop();
						ucskip = entry.NumberOfCharactersToSkip;
						ignorable = entry.Ignorable;
					}
				}
				else if (!libString.IsNullOrEmpty(character)) // \x (not a letter)
				{
					curskip = 0;
					if (character.equals("~"))
					{
						if (!ignorable)
						{
							outList.add("" + 0xA0);
						}
					}
					else if ("{}\\".contains(character))
					{
						if (!ignorable)
						{
							outList.add(character);
						}
					}
					else if (character.equalsIgnoreCase("*"))
					{
						ignorable = true;
					}
				}
				else if (!libString.IsNullOrEmpty(word)) // \foo
				{
					curskip = 0;
					if (destinations.contains(word))
					{
						ignorable = true;
					}
					else if (ignorable)
					{
					}
					else if (specialCharacters.containsKey(word))
					{
						outList.add(specialCharacters.get(word));
					}
					else if (word.equals("uc"))
					{
						ucskip = Integer.parseInt(arg);
					}
					else if (word.equals("u"))
					{
						int c = Integer.parseInt(arg);
						if (c < 0)
						{
							c += 0x10000;
						}
						outList.add("" + (char)(c));
						curskip = ucskip;
					}
				}
				else if (!libString.IsNullOrEmpty(hex)) // \'xx
				{
					if (curskip > 0)
					{
						curskip -= 1;
					}
					else if (!ignorable)
					{
						char c =  (char) ((Character.digit(hex.charAt(0), 16) << 4) + Character.digit(hex.charAt(1), 16));//
		                outList.add("" + (c));
					}
				}
				else if (!libString.IsNullOrEmpty(tchar))
				{
					if (curskip > 0)
					{
						curskip -= 1;
					}
					else if (!ignorable)
					{
						outList.add(tchar);
					}
				}
			} while(matches.find());
		}
		else
		{
			// Didn't match the regex
			returnString = inputRtf;
		}
		StringBuilder sb = new StringBuilder();
		for (String s : outList)
		{
			sb.append(s);
		}
		returnString = sb.toString();
		//returnString = Arrays.toString(outList.toArray()); // String.Join("", outList.toArray());

		return returnString;
	}
}