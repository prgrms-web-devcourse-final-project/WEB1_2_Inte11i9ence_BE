package com.prgrmsfinal.skypedia.reply.service;

import com.prgrmsfinal.skypedia.reply.dto.ReplyDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;
import com.prgrmsfinal.skypedia.planShare.exception.PlanError;
import com.prgrmsfinal.skypedia.reply.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{
    private final ReplyRepository replyRepository;

    @Override
    public List<ReplyDTO> readAll(ReplyDTO replyDTO) {
        List<Reply> replies = replyRepository.findAll();
        return replies.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReplyDTO register(ReplyDTO replyDTO) {
        try {
            Reply reply = dtoToEntity(replyDTO);
            reply = replyRepository.save(reply);
            return entityToDto(reply);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_REGISTERED.get();
        }
    }

    @Override
    public ReplyDTO update(ReplyDTO replyDTO) {
        Optional<Reply> updateReply = replyRepository.findById(replyDTO.getId());
        Reply reply = updateReply.orElseThrow(PlanError.NOT_REGISTERED::get);

        try {
            reply.setContent(replyDTO.getContent());

            replyRepository.save(reply);

            return entityToDto(reply);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw PlanError.NOT_MODIFIED.get();
        }
    }

    @Override
    public void delete(Long id) {
        Reply reply = replyRepository.findById(id).orElseThrow(PlanError.NOT_REGISTERED::get);
        replyRepository.delete(reply);
    }

    private ReplyDTO entityToDto(Reply reply) {
        return ReplyDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .likes(reply.getLikes())
                .deleted(reply.isDeleted())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .deletedAt(reply.getDeletedAt())
                .build();
    }

    private Reply dtoToEntity(ReplyDTO replyDTO) {
        return Reply.builder()
                .id(replyDTO.getId())
                .content(replyDTO.getContent())
                .likes(replyDTO.getLikes())
                .deleted(replyDTO.getDeleted())
                .createdAt(replyDTO.getCreatedAt())
                .updatedAt(replyDTO.getUpdatedAt())
                .deletedAt(replyDTO.getDeletedAt())
                .build();
    }
}
