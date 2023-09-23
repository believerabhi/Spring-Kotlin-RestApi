package com.abhi.example.springbootkotlin

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional

@Entity
class ArticleWithJpa(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long ? = null,
        var title: String,
        var content:String,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        var slug: String = title.toSlug()
)

interface ArticleRepostiroy : JpaRepository<ArticleWithJpa , Long> {

    fun findAllByOrderByCreatedAtDesc() : Iterable<ArticleWithJpa>

    fun findBySlug(slug: String) : Optional<ArticleWithJpa>
}

@RestController
@RequestMapping("/api/v2/articles")
class ArticleControllerWithJpa(val articleRepostiroy: ArticleRepostiroy) {

    @GetMapping
    fun articles() = articleRepostiroy.findAllByOrderByCreatedAtDesc()

    @GetMapping("/{slug}")
    fun articles(@PathVariable slug:String) =
            articleRepostiroy.findBySlug(slug).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND)}

    @PostMapping
    fun newArticle(@RequestBody article: ArticleWithJpa) : ArticleWithJpa  {
        article.id = null
        articleRepostiroy.save(article)
        return article
    }

    @PutMapping("/{slug}")
    fun updateArticle(@RequestBody article: ArticleWithJpa, @PathVariable slug: String) : ArticleWithJpa {
        val existingArticle = articleRepostiroy.findBySlug(slug).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND)}
        existingArticle.content = article.content
        articleRepostiroy.save(existingArticle)
        return article
    }

    @DeleteMapping("/{slug}")
    fun deleteArticle(@PathVariable slug: String) {
        val existingArticle = articleRepostiroy.findBySlug(slug).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND)}
        articleRepostiroy.delete(existingArticle)
    }

}