import com.badoo.reaktive.observable.doOnBeforeFinally
import com.badoo.reaktive.observable.doOnBeforeSubscribe
import com.badoo.reaktive.observable.flatten
import com.badoo.reaktive.observable.subscribe
import org.w3c.dom.Element
import org.w3c.dom.Image
import kotlin.browser.document
import kotlin.browser.window
import com.badoo.reaktive.promise.asSingle
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.merge

private lateinit var loader: Element
private lateinit var kittensContainer: Element

fun main() {
    document.addEventListener("DOMContentLoaded", {

        loader = document.getElementById("loader")!!
        kittensContainer = document.getElementById("kittens")!!

        document
            .getElementById("load-button")!!
            .addEventListener("click", {
                displayRandomKittens()
            })
    })
}

fun displayRandomKittens() {
    merge(loadRandomKitten(), loadRandomKitten(), loadRandomKitten())
        .doOnBeforeSubscribe { loader.removeAttribute("hidden") }
        .doOnBeforeFinally { loader.setAttribute("hidden", "true") }
        .flatten()
        .subscribe { kitten ->
            kittensContainer.appendChild(Image().apply {
                src = kitten.url
            })
        }
}

class Kitten(val url: String)

fun loadRandomKitten(): Single<List<Kitten>> {
    return window
        .fetch("https://api.thecatapi.com/v1/images/search")
        .asSingle()
        .flatMap { it.json().asSingle() }
        .map {
            (it.asDynamic() as Array<dynamic>).map { kitten ->
                Kitten(kitten.url as String)
            }
        }
}
