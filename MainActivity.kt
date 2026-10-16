package ci.scrabble2g

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.text.Normalizer
import kotlin.random.Random

class MainActivity : Activity() {
    private val values = mapOf('A' to 1,'B' to 3,'C' to 3,'D' to 2,'E' to 1,'F' to 4,'G' to 2,'H' to 4,'I' to 1,'J' to 8,'K' to 10,'L' to 1,'M' to 2,'N' to 1,'O' to 1,'P' to 3,'Q' to 8,'R' to 1,'S' to 1,'T' to 1,'U' to 1,'V' to 4,'W' to 10,'X' to 10,'Y' to 10,'Z' to 10)
    private val bag = "AAAAAAAEEEEEEEEEEEEIIIIIIIIOOOOOOONNNNNNRRRRRRSSSSSSTTTTTTUUUUUULLLLMMMDDDDGGBCCPPFHVJQKWXYZ"
    private val words = mutableMapOf<String,String>()
    private val placed = mutableMapOf<Pair<Int,Int>,Char>()
    private var scores = intArrayOf(0,0)
    private var player = 0
    private var rack = mutableListOf<Char>()
    private lateinit var scoresView: TextView
    private lateinit var rackView: TextView
    private lateinit var wordInput: EditText
    private lateinit var result: TextView
    private lateinit var board: GridLayout

    private fun norm(s:String) = Normalizer.normalize(s.uppercase(), Normalizer.Form.NFD)
        .replace(Regex("\\\\p{Mn}+"), "").replace(Regex("[^A-Z]"), "")
    private fun dp(v:Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        assets.open("mots_demo.txt").bufferedReader().useLines { lines ->
            lines.forEach { line -> val p=line.split("|",limit=2); if(p.size==2) words[norm(p[0])]=p[1] }
        }
        rack = drawRack()
        val scroll=ScrollView(this)
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(12,14,12,20); setBackgroundColor(Color.rgb(246,247,241)) }
        scroll.addView(root); setContentView(scroll)
        root.addView(TextView(this).apply { text="SCRABBLE 2G"; textSize=26f; gravity=Gravity.CENTER; setTextColor(Color.rgb(20,83,45)) })
        root.addView(TextView(this).apply { text="Prototype hors connexion • deux joueurs"; gravity=Gravity.CENTER; textSize=13f })
        scoresView=TextView(this).apply { textSize=18f; gravity=Gravity.CENTER; setPadding(0,12,0,8) }; root.addView(scoresView)
        board=GridLayout(this).apply { rowCount=15; columnCount=15 }; root.addView(board); renderBoard()
        rackView=TextView(this).apply { textSize=18f; gravity=Gravity.CENTER; setPadding(0,12,0,8) }; root.addView(rackView)
        wordInput=EditText(this).apply { hint="Mot à vérifier ou jouer"; singleLine=true }; root.addView(wordInput)
        val buttons=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        buttons.addView(Button(this).apply { text="Vérifier"; setOnClickListener { checkWord(false) } }, LinearLayout.LayoutParams(0,-2,1f))
        buttons.addView(Button(this).apply { text="Jouer"; setOnClickListener { checkWord(true) } }, LinearLayout.LayoutParams(0,-2,1f))
        root.addView(buttons)
        root.addView(Button(this).apply { text="Changer de joueur"; setOnClickListener { player=1-player; rack=drawRack(); updateHeader(); result.text="Tour du joueur ${player+1}." } })
        result=TextView(this).apply { textSize=15f; setPadding(4,8,4,8) }; root.addView(result)
        val search=EditText(this).apply { hint="Rechercher un mot et sa définition"; singleLine=true }; root.addView(search)
        root.addView(Button(this).apply { text="Rechercher dans la base locale"; setOnClickListener {
            val key=norm(search.text.toString()); val d=words[key]
            result.text=if(key.isBlank()) "Saisis un mot." else if(d!=null) "$key : $d" else "Mot absent de la petite liste de démonstration. Cela ne signifie pas qu'il est invalide."
        } })
        root.addView(TextView(this).apply {
            text="Note : la liste intégrée est une petite liste de test, pas l'ODS officiel ni le Larousse. Les règles complètes du Scrabble seront ajoutées ensuite."
            textSize=12f; setPadding(2,12,2,2)
        })
        updateHeader()
    }
    private fun drawRack()=MutableList(7){ bag[Random.nextInt(bag.length)] }
    private fun renderBoard() {
        board.removeAllViews()
        for(r in 0..14) for(c in 0..14) {
            val ch=placed[r to c]
            val cell=TextView(this).apply {
                text=ch?.toString() ?: if(r==7&&c==7) "★" else ""
                gravity=Gravity.CENTER; textSize=10f; setTextColor(Color.rgb(30,65,38))
                setBackgroundColor(if(ch!=null) Color.rgb(255,239,184) else if(r==7&&c==7) Color.rgb(240,205,113) else Color.rgb(211,229,207))
            }
            board.addView(cell,GridLayout.LayoutParams().apply { width=dp(19); height=dp(23); setMargins(dp(1),dp(1),dp(1),dp(1)) })
        }
    }
    private fun checkWord(play:Boolean) {
        val key=norm(wordInput.text.toString())
        if(key.isBlank()){ result.text="Saisis un mot."; return }
        val def=words[key]; val points=key.sumOf{values[it]?:0}
        if(play && def!=null) {
            scores[player]+=points
            val start=7-key.length/2
            key.forEachIndexed { i,ch -> val col=(start+i).coerceIn(0,14); placed.putIfAbsent(7 to col,ch) }
            rack=drawRack(); renderBoard(); updateHeader()
            result.text="$key joué en mode démonstration : +$points points. Le placement officiel et les bonus ne sont pas encore actifs."
        } else {
            result.text="$key — $points point(s) de lettres.\n"+(if(def!=null) "Présent dans la liste de démonstration.\nDéfinition : $def" else "Absent de la petite liste de démonstration : cela ne prouve pas qu'il est invalide.")
        }
    }
    private fun updateHeader() {
        scoresView.text="Joueur 1 : ${scores[0]}     |     Joueur 2 : ${scores[1]}\nTour : Joueur ${player+1}"
        rackView.text="Lettres du joueur ${player+1} : ${rack.joinToString("  ")}"
    }
}
