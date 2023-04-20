package grimuri.backend.domain.diary;

import grimuri.backend.domain.BaseTimeEntity;
import grimuri.backend.domain.image.Image;
import grimuri.backend.domain.user.User;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String originalContent;

    @Column(nullable = true)
    private String shortContent;

    @OneToMany(mappedBy = "diary")
    @BatchSize(size = 10)
    private List<Image> imageList = new ArrayList<>();

    @Column(nullable = false)
    private Boolean selected;

    public void saveTags(String tagStr) {
        this.shortContent = tagStr;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
