package com.example.engine.localization

enum class EngineLanguage {
    ENGLISH,
    PERSIAN
}

object EngineStrings {
    // Top Bar
    fun appTitle(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "GAME ENGINE PERSIAN GULF" else "موتور بازی خلیج فارس"
    fun build(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Build" else "ساخت پروژه"
    fun assets(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Assets" else "دارایی‌ها"
    fun settings(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Settings" else "تنظیمات"
    fun playMode(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Play Mode" else "حالت بازی"
    fun exitPlayMode(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Exit to Editor" else "بازگشت به ویرایشگر"

    // Hierarchy Left Panel
    fun scenes(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Scenes" else "صحنه‌ها"
    fun scripts(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Scripts" else "اسکریپت‌ها"
    fun textures(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Textures" else "تکسچرها"
    fun sound(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Sound" else "صداها"
    fun fonts(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Fonts" else "فونت‌ها"
    fun plugins(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Plugins" else "پلاگین‌ها"
    fun items(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Items" else "آیتم‌ها"
    
    // Tools
    fun selectTool(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Select" else "انتخاب"
    fun moveTool(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Transform" else "جابجایی"
    fun rectTool(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Area Select" else "انتخاب ناحیه"
    fun deleteTool(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Delete" else "حذف"
    fun layersTool(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Layers" else "لایه‌ها"

    // Visual Scripting
    fun visualScripting(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Visual Scripting" else "اسکریپت‌نویسی تصویری"
    fun addNode(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Add Block" else "افزودن بلوک"
    fun runGraph(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Execute Logic" else "اجرای منطق"
    fun itemCount(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Item Count" else "تعداد آیتم"
    
    // Touch Controls HUD
    fun jump(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Jump" else "پرش"
    fun attack(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Attack" else "حمله"
    fun dodge(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Dodge" else "جاخالی"
    fun inventory(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Inventory" else "کوله پشتی"
    
    // License & IP
    fun licensedTo(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Licensed to: Milad Aziznejad" else "مجوز ثبت شده به نام: میلاد عزیز نژاد"
    fun licenseKey(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Master Commercial Key: 55555milad3603" else "کلید تجاری موتور: 55555milad3603"
    fun licenseStatus(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Commercial PRO License Activated" else "مجوز رسمی تجاری پرو فعال است"

    // Weather
    fun weather(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Gulf Weather" else "آب و هوای خلیج"
    fun sunny(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Sunny Gulf" else "آفتابی خلیج فارس"
    fun rain(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Tropical Rain" else "باران استوایی"
    fun mistSnow(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Sea Mist & Snow" else "مه دریایی و برف"
    fun storm(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Gulf Storm" else "طوفان خلیج"

    // Multiplayer
    fun multiplayerLobby(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Multiplayer Lobby" else "لابی چند نفره آنلاین"
    fun cutsceneCreator(lang: EngineLanguage) = if (lang == EngineLanguage.ENGLISH) "Cutscene & Narrative" else "سازنده کات‌سین و داستان"
}
