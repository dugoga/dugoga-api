package com.project.dugoga.domain.aiprompt.infrastructure.repository;

import com.project.dugoga.config.config.DataJpaTestBase;
import com.project.dugoga.config.config.generator.AiPromptFixtureGenerator;
import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import com.project.dugoga.domain.aiprompt.infastructure.repository.AiPromptJpaRepository;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static com.project.dugoga.config.config.generator.AiPromptFixtureGenerator.generateAiPromptFixture;
import static com.project.dugoga.config.config.generator.AvailableAddressFixtureGenerator.generateAvailableAddressFixture;
import static com.project.dugoga.config.config.generator.CategoryFixtureGenerator.generateCategoryFixture;
import static com.project.dugoga.config.config.generator.ProductFixtureGenerator.generateProductFixture;
import static com.project.dugoga.config.config.generator.StoreFixtureGenerator.generateStoreFixture;
import static com.project.dugoga.config.config.generator.UserFixtureGenerator.generateUserFixture;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Repository: AiPrompt 레포지터리 테스트")
public class AiPromptJpaRepositoryTest extends DataJpaTestBase {

    @Autowired
    private AiPromptJpaRepository aiPromptJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;
    private Store savedStore;
    private Product savedProduct;
    private Category savedCategory;
    private AvailableAddress savedAvailableAddress;
    private AiPrompt savedAiPrompt;

    @BeforeEach
    void setUp() {
        savedUser = entityManager.persist(generateUserFixture());
        savedCategory = entityManager.persist(generateCategoryFixture());
        savedAvailableAddress = entityManager.persist(generateAvailableAddressFixture());
        savedStore = entityManager.persist(generateStoreFixture(savedUser, savedCategory, savedAvailableAddress));
        savedProduct = entityManager.persist(generateProductFixture(savedStore));
        savedAiPrompt = entityManager.persist(generateAiPromptFixture(savedUser, savedStore, savedProduct));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Create AiPrompt (save)")
    void create_aiPrompt() {

        // given
        User user = savedUser;
        Store store = savedStore;
        Product product = savedProduct;
        String promptText = AiPromptFixtureGenerator.PROMPT_TEXT;
        String responseText = AiPromptFixtureGenerator.RESPONSE_TEXT;

        // when
        AiPrompt saved = aiPromptJpaRepository.save(new AiPrompt(user, store, product, promptText, responseText));

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getStore()).isEqualTo(store);
        assertThat(saved.getProduct()).isEqualTo(product);
        assertThat(saved.getPromptText()).isEqualTo(promptText);
        assertThat(saved.getResponseText()).isEqualTo(responseText);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getDeletedAt()).isNull();

    }

    @Test
    @DisplayName("Recreate AiPrompt (findByIdAndDeletedAtIsNull)")
    void recreate_aiPrompt() {

        // given
        AiPrompt aiPrompt = savedAiPrompt;
        UUID aiPromptId = aiPrompt.getId();

        User user = savedUser;
        Store store = savedStore;
        Product product = savedProduct;
        String promptText = AiPromptFixtureGenerator.PROMPT_TEXT;
        String responseText = AiPromptFixtureGenerator.RESPONSE_TEXT;

        // when
        AiPrompt searchResult = aiPromptJpaRepository.findByIdAndDeletedAtIsNull(aiPromptId).orElse(null);

        // then
        assertThat(searchResult.getId()).isNotNull();
        assertThat(searchResult.getUser().getId()).isEqualTo(user.getId());
        assertThat(searchResult.getStore().getId()).isEqualTo(store.getId());
        assertThat(searchResult.getProduct().getId()).isEqualTo(product.getId());

        assertThat(searchResult.getPromptText()).isEqualTo(promptText);
        assertThat(searchResult.getResponseText()).isEqualTo(responseText);

        assertThat(searchResult.getCreatedAt()).isNotNull();
        assertThat(searchResult.getUpdatedAt()).isNotNull();

        // deletedAtIsNull 조건으로 확인해야
        assertThat(searchResult.getDeletedAt()).isNull();

    }

    @Test
    @DisplayName("Delete AiPrompt (findByIdAndDeletedAtIsNotNull)")
    void delete_aiPrompt() {

        // given
        UUID aiPromptId = savedAiPrompt.getId();
        User user = savedUser;

        AiPrompt aiPrompt = aiPromptJpaRepository.findById(aiPromptId).orElseThrow();

        aiPrompt.delete(user.getId());
        entityManager.flush();
        entityManager.clear();

        // when
        AiPrompt deletedResult
                = aiPromptJpaRepository.findByIdAndDeletedAtIsNotNull(aiPromptId).orElse(null);

        // then
        assertThat(deletedResult).isNotNull();
        assertThat(deletedResult.getId()).isEqualTo(aiPromptId);

        assertThat(deletedResult.getDeletedAt()).isNotNull();
        assertThat(deletedResult.getDeletedBy()).isEqualTo(user.getId());
    }

}
