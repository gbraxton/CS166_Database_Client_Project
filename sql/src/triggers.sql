DROP LANGUAGE plpgsql CASCADE;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION message_self_destruct() RETURNS trigger AS 
$BODY$
    BEGIN
        DELETE FROM message WHERE destr_timestamp < now();
        RETURN NEW;
    END;
$BODY$ 
LANGUAGE plpgsql;


CREATE TRIGGER self_destruct BEFORE INSERT OR UPDATE ON message
FOR EACH ROW EXECUTE PROCEDURE message_self_destruct();