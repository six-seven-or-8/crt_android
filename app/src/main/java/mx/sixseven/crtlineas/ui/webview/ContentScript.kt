package mx.sixseven.crtlineas.ui.webview

import mx.sixseven.crtlineas.model.*

// ══════════════════════════════════════════════════════════
// ContentScript.kt — CRT Líneas Android
// Selectores DOM verificados con content.js de la extensión
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

object ContentScript {

    fun getScript(companyId: String, userData: UserData): String? {
        val id   = userData.identificador.trim().uppercase()
        val tipo = userData.personType
        val cit  = userData.citizenship
        return when (companyId) {
            "altan"        -> scriptAltan(id, tipo, cit)
            "vtl_freedompop" -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_oui"        -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_yobi"       -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_ahorrocel"  -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_chedraui"   -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_oxxocel"    -> scriptVinculaTuLinea(id, tipo, cit)
            "vtl_ubercel"    -> scriptVinculaTuLinea(id, tipo, cit)
            "weex"         -> scriptWeex(id, tipo, cit)
            "logistica"    -> scriptGenericAuto(id, "logistica")
            "dalefon"      -> scriptDalefon(id, tipo, cit)
            "dalefon_bien" -> scriptDalefonBien(id, tipo, cit)
            "redphone"     -> scriptTurboRails(id)
            "virgin"       -> scriptGenericAuto(id, "virgin")
            "mi_movil"     -> scriptTurboRails(id)
            "mirlo"        -> scriptMirlo(id, tipo)
            "mosi"         -> scriptTurboRails(id)
            "oxio"         -> scriptOxio(id)
            "bait"         -> scriptGenericAuto(id, "bait")
            else           -> null
        }
    }

    // ── Altán ──────────────────────────────────────────────
    // Selectores verificados: waitFor CURP input, radios tipo persona,
    // radios ciudadania, checkboxes TC/AV
    private fun scriptAltan(id: String, tipo: PersonType, cit: Citizenship): String {
        val isMoral   = tipo == PersonType.MORAL
        val isForeign = cit == Citizenship.EXTRANJERO && !isMoral
        val tipoText  = if (isMoral) "Persona moral" else "Persona f\u00edsica"
        val ciudText  = if (isForeign) "Ciudadano extranjero" else "Ciudadano mexicano"
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1500);
    // Tipo de persona
    var found = false;
    document.querySelectorAll('p,span,label,button,div').forEach(function(el) {
      if (el.children.length > 0) return;
      var t = el.textContent.trim();
      if (t === '$tipoText' || t === '$tipoText'.replace('\u00ed','i')) {
        el.click(); found = true;
      }
    });
    if (!found) {
      var radios = document.querySelectorAll('input[type="radio"]');
      if (radios.length) { radios[${if (isMoral) 1 else 0}].click(); }
    }
    await sleep(400);
    // Ciudadania (solo fisicas)
    ${if (!isMoral) """
    document.querySelectorAll('p,span,label,button,div').forEach(function(el) {
      if (el.children.length > 0) return;
      if (el.textContent.trim() === '$ciudText') el.click();
    });
    await sleep(400);
    """ else ""}
    // Rellenar identificador — con retry para esperar que el DOM se actualice
    var inp = null;
    for (var attempt = 0; attempt < 10; attempt++) {
      inp = document.querySelector(
        'input[placeholder="CURP"], ' +
        'input[placeholder*="CURP"], ' +
        'input[placeholder*="RFC"], ' +
        'input[placeholder*="asaporte"], ' +
        'input[placeholder*="Pasaporte"], ' +
        'input[placeholder*="PASAPORTE"], ' +
        'input[placeholder*="ej. "]'
      );
      if (!inp) inp = document.querySelector('input[type="text"]:not([disabled]):not([readonly])');
      if (inp) break;
      await sleep(400);
    }
    if (inp) { setVal(inp, '$id'); }
    await sleep(300);
    // Checkboxes TC y AV
    document.querySelectorAll('input[type="checkbox"]').forEach(function(cb) {
      if (!cb.checked) cb.click();
    });
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── VinculaTuLinea (Freedompop, OUI, YobiTelecom, etc.) ──
    // Angular app — esperar que cargue y buscar inputs por tipo
    private fun scriptVinculaTuLinea(id: String, tipo: PersonType, cit: Citizenship): String {
        val isMoral   = tipo == PersonType.MORAL
        val isForeign = cit == Citizenship.EXTRANJERO && !isMoral
        val docType   = when { isMoral -> "RFC"; isForeign -> "Pasaporte"; else -> "CURP" }
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
    el.dispatchEvent(new Event('blur',{bubbles:true}));
  }
  async function waitFor(fn, maxMs) {
    var end = Date.now() + (maxMs || 10000);
    while (Date.now() < end) {
      var r = fn();
      if (r) return r;
      await sleep(400);
    }
    return null;
  }
  async function run() {
    await sleep(2000);
    // Paso 1: Seleccionar tipo de documento si hay tabs/radios/botones
    var docBtns = Array.from(document.querySelectorAll('button,mat-button-toggle,mat-radio-button,[role="radio"],[role="tab"]'));
    var docBtn = docBtns.find(function(b) {
      return (b.textContent||'').trim().toUpperCase().includes('$docType');
    });
    if (docBtn) { docBtn.click(); await sleep(800); }

    // Paso 2: Esperar input y rellenar
    var inp = await waitFor(function() {
      return document.querySelector(
        'input[type="text"]:not([disabled]):not([readonly]),' +
        'input[placeholder*="CURP"]:not([disabled]),' +
        'input[placeholder*="RFC"]:not([disabled]),' +
        'input[placeholder*="asaporte"]:not([disabled]),' +
        'input[placeholder*="documento"]:not([disabled]),' +
        'mat-form-field input:not([disabled])'
      );
    }, 8000);
    if (!inp) return;
    inp.focus();
    await sleep(300);
    setVal(inp, '$id');
    await sleep(600);

    // Paso 3: Aceptar términos/checkboxes
    document.querySelectorAll('mat-checkbox input, input[type="checkbox"]').forEach(function(cb) {
      if (!cb.checked) cb.click();
    });
    await sleep(400);

    // Paso 4: Botón continuar/consultar
    var btn = await waitFor(function() {
      return Array.from(document.querySelectorAll(
        'button:not([disabled]), [mat-raised-button]:not([disabled])'
      )).find(function(b) {
        var t = (b.textContent||'').trim().toLowerCase();
        return t.includes('continu') || t.includes('consult') || t.includes('siguiente') || t.includes('buscar');
      });
    }, 4000);
    if (btn) btn.click();
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── Weex ───────────────────────────────────────────────
    // Tabs con texto exacto "CURP"/"RFC"/"PASAPORTE", luego input único
    private fun scriptWeex(id: String, tipo: PersonType, cit: Citizenship): String {
        val isMoral   = tipo == PersonType.MORAL
        val isForeign = cit == Citizenship.EXTRANJERO && !isMoral
        val tabText   = when { isMoral -> "RFC"; isForeign -> "PASAPORTE"; else -> "CURP" }
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1500);
    var tabs = Array.from(document.querySelectorAll('button'));
    var tabBtn = tabs.find(function(b) {
      return (b.textContent||'').trim().toUpperCase() === '$tabText';
    });
    if (tabBtn) { tabBtn.click(); await sleep(400); }
    var inp = document.querySelector(
      'input[type="text"], input[placeholder*="CURP"], input[placeholder*="RFC"], input[placeholder*="Pasaporte"]'
    );
    if (!inp) return;
    setVal(inp, '$id');
    await sleep(400);
    var btn = document.querySelector('button[type="submit"]') ||
      Array.from(document.querySelectorAll('button')).find(function(b) {
        return (b.textContent||'').toLowerCase().includes('consultar') && !b.disabled;
      });
    if (btn) btn.click();
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── Dalefon ────────────────────────────────────────────
    // Radio name="tipoPersona" value="curp/rfc/pasaporte"
    // Luego input específico según tipo, botón Continuar
    private fun scriptDalefon(id: String, tipo: PersonType, cit: Citizenship): String {
        val isMoral   = tipo == PersonType.MORAL
        val isForeign = cit == Citizenship.EXTRANJERO && !isMoral
        val radioVal  = when { isMoral -> "rfc"; isForeign -> "pasaporte"; else -> "curp" }
        // Para extranjero: el input aparece dinámicamente con maxlength mayor
        val inputSel  = when {
            isMoral   -> "#input-linked-line-rfc, input[name=\"rfc\"], input[placeholder=\"RFC\"]"
            isForeign -> "#input-linked-line-pasaporte, input[name=\"pasaporte\"], input[placeholder*=\"asaporte\"], input[placeholder*=\"PASAPORTE\"], input[placeholder*=\"Passport\"]"
            else      -> "input[placeholder*=\"CURP\"], input[type=\"text\"][maxlength=\"18\"], #input-linked-line-curp"
        }
        // Tiempo de espera extra para extranjero porque el DOM tarda más en actualizarse
        val waitMs = if (isForeign) 1000 else 600
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1800);
    var radio = document.querySelector('input[type="radio"][name="tipoPersona"][value="$radioVal"]');
    if (radio) { radio.click(); await sleep($waitMs); }
    var inp = null;
    for (var i = 0; i < 15; i++) {
      inp = document.querySelector('$inputSel');
      // Fallback: cualquier input de texto visible y habilitado
      if (!inp) {
        var all = document.querySelectorAll('input[type="text"]:not([disabled]):not([readonly])');
        if (all.length > 0) inp = all[all.length - 1]; // el ultimo input suele ser el activo
      }
      if (inp) break;
      await sleep(400);
    }
    if (!inp) return;
    setVal(inp, '$id');
    await sleep(500);
    var btn = Array.from(document.querySelectorAll('button')).find(function(b) {
      return (b.textContent||'').trim().toLowerCase() === 'continuar' && !b.disabled;
    });
    if (btn) btn.click();
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── Dalefon Bienestar ──────────────────────────────────
    // Radio value="fisica/moral/extranjero", MUI inputs
    private fun scriptDalefonBien(id: String, tipo: PersonType, cit: Citizenship = Citizenship.MEXICANO): String {
        val isForeign = cit == Citizenship.EXTRANJERO && tipo == PersonType.FISICA
        val radioVal  = when {
            tipo == PersonType.MORAL -> "moral"
            isForeign                -> "extranjero"
            else                     -> "fisica"
        }
        val waitMs = if (isForeign) 800 else 600
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1200);
    var radio = document.querySelector('input[type="radio"][value="$radioVal"]');
    if (radio) { radio.click(); await sleep($waitMs); }
    // Esperar a que aparezca el input correcto después del radio
    var inp = null;
    for (var i = 0; i < 12; i++) {
      var inputs = document.querySelectorAll('input[type="text"]:not([disabled]):not([readonly])');
      if (inputs.length > 0) { inp = inputs[inputs.length - 1]; break; }
      await sleep(400);
    }
    if (!inp) return;
    setVal(inp, '$id');
    await sleep(400);
    var btn = Array.from(document.querySelectorAll('button')).find(function(b) {
      return (b.textContent||'').toLowerCase().includes('consultar') && !b.disabled;
    });
    if (btn) btn.click();
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── TurboRails (Redphone, Mi Movil, Mosi) ─────────────
    // Selector: #identifier o input[name="identifier"]
    // Submit: input[type="submit"][name="commit"]
    // Recarga automática si error de seguridad
    private fun scriptTurboRails(id: String): String = """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1200);
    var inp = document.querySelector('#identifier, input[name="identifier"]');
    if (!inp) return;
    setVal(inp, '$id');
    // NO hacer click automatico — el servidor detecta bots y da error de seguridad.
    // El usuario hace click manualmente en Consultar.
    // Auto-recarga si verificacion de seguridad fallida (por si acaso)
    var obs = new MutationObserver(function() {
      var text = (document.body.innerText||'').toLowerCase();
      if (text.includes('verificaci') && text.includes('seguridad fallida')) {
        obs.disconnect();
        setTimeout(function() { location.reload(); }, 1500);
      }
    });
    obs.observe(document.body, { childList:true, subtree:true });
    setTimeout(function() { obs.disconnect(); }, 30000);
  }
  run().catch(function(){});
})();
    """.trimIndent()

    // ── Mirlo ──────────────────────────────────────────────
    // Paso 1: click "Ver líneas vinculadas"
    // Paso 2: forzar apertura del dialog nativo (showModal)
    // Paso 3: radio CURP/RFC
    // Paso 4: input class*="font-mono"
    // Paso 5: submit
    private fun scriptMirlo(id: String, tipo: PersonType): String {
        val isMoral  = tipo == PersonType.MORAL
        val radioVal = if (isMoral) "RFC" else "CURP"
        return """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function waitFor(sel, maxMs) {
    var end = Date.now() + (maxMs || 8000);
    while (Date.now() < end) {
      var el = document.querySelector(sel);
      if (el) return el;
      await sleep(300);
    }
    return null;
  }
  async function run() {
    await sleep(1200);
    // Paso 1: buscar boton Ver lineas vinculadas
    var verBtn = null;
    for (var i = 0; i < 20; i++) {
      verBtn = Array.from(document.querySelectorAll('button')).find(function(b) {
        var t = b.textContent || '';
        return t.includes('Ver l') && (t.includes('neas vinculadas') || t.includes('neas vinculadas'));
      });
      if (verBtn) break;
      await sleep(400);
    }
    if (!verBtn) return;
    verBtn.click();
    await sleep(800);
    // Paso 2: forzar apertura de cualquier dialog cerrado
    document.querySelectorAll('dialog').forEach(function(d) {
      if (!d.open) {
        try { d.showModal(); } catch(e) { d.setAttribute('open',''); }
      }
      d.style.display = 'flex';
      d.style.visibility = 'visible';
      d.style.opacity = '1';
      d.style.zIndex = '9999';
      d.style.position = 'fixed';
      d.style.inset = '0';
      d.style.width = '100vw';
      d.style.height = '100vh';
      d.style.alignItems = 'center';
      d.style.justifyContent = 'center';
      d.style.backgroundColor = 'rgba(0,0,0,0.5)';
    });
    // También buscar divs con role=dialog
    document.querySelectorAll('[role="dialog"],[aria-modal="true"]').forEach(function(d) {
      d.style.display = 'block';
      d.style.visibility = 'visible';
      d.style.opacity = '1';
      d.style.zIndex = '9999';
      d.style.position = 'fixed';
      d.style.top = '50%';
      d.style.left = '50%';
      d.style.transform = 'translate(-50%,-50%)';
      d.style.maxHeight = '90vh';
      d.style.overflowY = 'auto';
      d.style.backgroundColor = 'white';
      d.style.padding = '20px';
      d.style.borderRadius = '8px';
      d.style.width = '90vw';
    });
    await sleep(600);
    // Paso 3: radio
    var radioEl = document.querySelector('input[type="radio"][value="$radioVal"]');
    if (radioEl && !radioEl.checked) { radioEl.click(); await sleep(400); }
    // Paso 4: input
    var inp = await waitFor('input[class*="font-mono"], input[placeholder*="PEGJ"], input[placeholder*="ABC"]', 4000);
    if (!inp) inp = document.querySelector('input[type="text"]:not([disabled])');
    if (!inp) return;
    setVal(inp, '$id');
    await sleep(500);
    // Paso 5: submit con retry
    for (var j = 0; j < 20; j++) {
      var btn = document.querySelector('button[type="submit"]:not([disabled])');
      if (btn) { btn.click(); break; }
      await sleep(300);
    }
  }
  run().catch(function(){});
})();
        """.trimIndent()
    }

    // ── Oxio ───────────────────────────────────────────────
    // Angular: formcontrolname="curp", setAngularVal
    private fun scriptOxio(id: String): String = """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setAngularVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
    el.dispatchEvent(new Event('blur',{bubbles:true}));
  }
  async function run() {
    await sleep(1500);
    var inp = document.querySelector('input[formcontrolname="curp"]');
    if (!inp) inp = document.querySelector('input[type="text"]:not([disabled])');
    if (!inp) return;
    setAngularVal(inp, '$id');
    await sleep(1000);
    var btn = document.querySelector('button[nztype="primary"], button.ant-btn-primary, button[type="submit"]');
    if (btn && !btn.disabled) btn.click();
  }
  run().catch(function(){});
})();
    """.trimIndent()

    // ── Generic Auto (Virgin, Bait, Logística, etc.) ───────
    // Busca el input CURP con múltiples selectores, rellena y busca botón
    private fun scriptGenericAuto(id: String, portalId: String): String = """
(function() {
  function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
  function setVal(el, v) {
    var nd = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value');
    if (nd && nd.set) { nd.set.call(el, v); } else { el.value = v; }
    el.dispatchEvent(new Event('input',{bubbles:true}));
    el.dispatchEvent(new Event('change',{bubbles:true}));
  }
  async function run() {
    await sleep(1500);
    var sels = ['#curp','input[name="curp"]','input[name="curpa"]','#curpa',
      'input[placeholder*="CURP"]','input[placeholder*="curp"]',
      'input[type="text"][maxlength="18"]','input[type="text"]:not([disabled])'];
    var inp = null;
    sels.forEach(function(s) { if (!inp) { inp = document.querySelector(s); } });
    if (!inp) return;
    setVal(inp, '$id');
    await sleep(500);
    document.querySelectorAll('input[type="checkbox"]').forEach(function(cb) {
      if (!cb.checked) cb.click();
    });
    var submitTexts = ['consultar','buscar','search','continuar'];
    var btn = null;
    for (var i = 0; i < 15; i++) {
      btn = Array.from(document.querySelectorAll(
        'button[type="submit"],input[type="submit"],button[type="button"],button:not([type])'
      )).find(function(b) {
        if (b.disabled) return false;
        var t = (b.textContent || b.getAttribute('value') || '').toLowerCase();
        return submitTexts.some(function(kw) { return t.includes(kw); });
      });
      if (btn) break;
      await sleep(300);
    }
    if (btn) btn.click();
  }
  run().catch(function(){});
})();
    """.trimIndent()

    // ── Extractor de resultados ────────────────────────────
    // Números de footer/contacto conocidos que NO son resultados de consulta
    private val FOOTER_PHONES = setOf(
        "5587103011", // Virgin Mobile footer
        "5592255999", // Mirlo footer
        "8009999999", // genérico
    )

    fun getResultExtractor(companyId: String): String {
        val footerExclusions = FOOTER_PHONES.joinToString(",") { "\"$it\"" }
        return """
(function() {
  var EXCLUDE = [$footerExclusions];
  var phones = [];
  // Intentar extraer solo del contenedor de resultados, no del footer
  var resultContainer = document.querySelector(
    '[class*="result"], [class*="Result"], [id*="result"], [id*="Result"], main, article, .content, #content'
  ) || document.body;
  var allText = resultContainer ? (resultContainer.innerText || '') : '';
  var matches = allText.match(/\b\d{10}\b/g) || [];
  matches.forEach(function(m) {
    if (phones.indexOf(m) < 0 && EXCLUDE.indexOf(m) < 0) phones.push(m);
  });
  var noLines = allText.toLowerCase().includes('no se encontr') ||
                allText.toLowerCase().includes('sin l') ||
                allText.toLowerCase().includes('no hay') ||
                allText.toLowerCase().includes('no registr') ||
                allText.toLowerCase().includes('no tienes');
  return JSON.stringify({ phones: phones, noLines: noLines, done: true });
})();
        """.trimIndent()
    }
}
