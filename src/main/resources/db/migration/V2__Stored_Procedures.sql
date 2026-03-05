DELIMITER $$

CREATE PROCEDURE proc_vote_incident(
    IN p_user_id BIGINT,
    IN p_incident_id BIGINT,
    IN p_is_upvote BOOLEAN,
    OUT p_new_vote_count INT
)

BEGIN
    INSERT INTO votes (user_id ,incident_id) VALUES (p_user_id , p_incident_id);

    UPDATE incidents
    SET vote_count = CASE WHEN p_is_upvote THEN vote_count + 1 ELSE vote_count - 1 END
    WHERE id = p_incident_id;

    UPDATE incidents
    SET status = CASE
                    WHEN vote_count >= 5 AND status = 'PENDING' THEN 'VERIFIED'
                    WHEN vote_count <= -5 THEN 'EXPIRED'
                    ELSE status
                 END
    WHERE id = p_incident_id;

    SELECT vote_count INTO p_new_vote_count FROM incidents WHERE id = p_incident_id;
END $$

DELIMITER;