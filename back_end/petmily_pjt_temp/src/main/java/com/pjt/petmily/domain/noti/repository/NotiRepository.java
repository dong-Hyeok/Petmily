package com.pjt.petmily.domain.noti.repository;

import java.util.List;

import com.pjt.petmily.domain.noti.entity.Noti;
import com.pjt.petmily.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface NotiRepository extends JpaRepository<Noti, Integer>{

//    @Modifying
//    @Query(value = "INSERT INTO noti(fromUserId, toUserId, notiType, createDate) VALUES(?1, ?2, ?3, now())", nativeQuery = true)
//    int mSave(int fromUserId, int toUserId, String notiType);


//    List<Noti> findByToUserId(int loginUserId);
    List<Noti> findByToUserAndIsCheckedFalseOrderByCreatedAtDesc(User user);

    boolean existsByToUserAndIsCheckedFalse(User user);

//    // 최근 5개 알림만 나오도록 기능구현
//    @Query(value = "select * from noti where toUserId = ?1 order by createDate desc limit  0, 10;", nativeQuery = true)
//    List<Noti> mNotiForHeader(int loginUserId);
}
