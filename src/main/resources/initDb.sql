DROP TABLE IF EXISTS public.t_stored_galleries;
DROP TABLE IF EXISTS public.t_uploads;
DROP TABLE IF EXISTS public.t_tube_galleries;
DROP TABLE IF EXISTS public.t_fetch_invocations;
DROP TABLE IF EXISTS public.t_fetches;
DROP TABLE IF EXISTS public.t_checks;
DROP TABLE IF EXISTS public.t_sites;
DROP TABLE IF EXISTS public.t_tube_embed_video_hosts;
DROP TABLE IF EXISTS public.t_tubes;
DROP TABLE IF EXISTS public.t_sponsors;
DROP TABLE IF EXISTS auth.t_users;

DROP SEQUENCE IF EXISTS public.seq_stored_galleries;
DROP SEQUENCE IF EXISTS public.seq_uploads;
DROP SEQUENCE IF EXISTS public.seq_tube_galleries;
DROP SEQUENCE IF EXISTS public.seq_fetches;
DROP SEQUENCE IF EXISTS public.seq_checks;
DROP SEQUENCE IF EXISTS public.seq_sites;
DROP SEQUENCE IF EXISTS public.seq_tubes;
DROP SEQUENCE IF EXISTS public.seq_sponsors;
DROP SEQUENCE IF EXISTS auth.seq_users;

DROP SCHEMA IF EXISTS auth;

CREATE SCHEMA auth AUTHORIZATION checker;

CREATE SEQUENCE public.seq_sponsors START WITH 1;
CREATE SEQUENCE public.seq_tubes START WITH 1;
CREATE SEQUENCE public.seq_sites START WITH 1;
CREATE SEQUENCE public.seq_uploads START WITH 1;
CREATE SEQUENCE public.seq_fetches START WITH 1;
CREATE SEQUENCE public.seq_checks START WITH 1;
CREATE SEQUENCE public.seq_tube_galleries START WITH 1;
CREATE SEQUENCE public.seq_stored_galleries START WITH 1;
CREATE SEQUENCE auth.seq_users START WITH 1;

ALTER SEQUENCE public.seq_sponsors OWNER TO checker;
ALTER SEQUENCE public.seq_tubes OWNER TO checker;
ALTER SEQUENCE public.seq_sites OWNER TO checker;
ALTER SEQUENCE public.seq_uploads OWNER TO checker;
ALTER SEQUENCE public.seq_fetches OWNER TO checker;
ALTER SEQUENCE public.seq_checks OWNER TO checker;
ALTER SEQUENCE public.seq_tube_galleries OWNER TO checker;
ALTER SEQUENCE public.seq_stored_galleries OWNER TO checker;
ALTER SEQUENCE auth.seq_users OWNER TO checker;

CREATE TABLE auth.t_users
(
    id smallint PRIMARY KEY DEFAULT nextval('auth.seq_users'),
	username character varying(255),
    email character varying(255),
    password character varying(255),
    role smallint,
    status smallint
);
ALTER TABLE auth.t_users OWNER TO checker;
INSERT INTO auth.t_users (username, email, password, role, status) VALUES
 ('admin', 'admin@admin.admin', '$2a$12$RkE2LtwH4M1DnWiU06sCd.5QJh/tVmJ2IV484T5OQ5hVnY9WMLdBu', 1, 1);

CREATE TABLE public.t_sites
(
  id						SMALLINT PRIMARY KEY DEFAULT nextval('public.seq_sites'),
  name						VARCHAR(255) NULL,
  host						VARCHAR(255) NOT NULL,
  active					BOOLEAN DEFAULT TRUE NOT NULL,
  UNIQUE (host)
);
ALTER TABLE public.t_sites OWNER TO checker;

CREATE TABLE public.t_sponsors
(
  id						SMALLINT PRIMARY KEY DEFAULT nextval('public.seq_sponsors'),
  name						VARCHAR(255) NULL,
  url						VARCHAR(255) NULL,
  active					BOOLEAN DEFAULT TRUE NOT NULL
);
ALTER TABLE public.t_sponsors OWNER TO checker;
INSERT INTO public.t_sponsors (id, name) VALUES (0, 'undefined');

create table public.t_tubes
(
  id						SMALLINT PRIMARY KEY DEFAULT nextval('public.seq_tubes'),
  sponsor_id				SMALLINT NOT NULL DEFAULT (0),
  host						VARCHAR(255) NOT NULL,
  UNIQUE (host)
);
ALTER TABLE public.t_tubes ADD CONSTRAINT fk_tubes_sponsors FOREIGN KEY (sponsor_id) REFERENCES public.t_sponsors (id);
ALTER TABLE public.t_tubes OWNER TO checker;

create table public.t_tube_embed_video_hosts
(
	tube_id						SMALLINT NOT NULL,
	embed_video_host			VARCHAR(255) NOT NULL,
	UNIQUE (embed_video_host)
);
ALTER TABLE public.t_tube_embed_video_hosts ADD CONSTRAINT fk_tube_embed_video_hosts_tubes FOREIGN KEY (tube_id) REFERENCES public.t_tubes (id);
ALTER TABLE public.t_tube_embed_video_hosts OWNER TO checker;

CREATE TABLE public.t_uploads
(
  id						SMALLINT PRIMARY KEY DEFAULT nextval('public.seq_uploads'),
  user_id					SMALLINT NULL,
  site_id					SMALLINT NULL,
  file_name					VARCHAR(255) NULL,
  upload_datetime			TIMESTAMP DEFAULT NOW(),
  uploaded_galleries    	INTEGER NULL,
  new_uploaded_galleries  	INTEGER NULL,
  status				    VARCHAR(25) NULL,
  error_message				VARCHAR(2048) NULL
);
ALTER TABLE public.t_uploads ADD CONSTRAINT fk_uploads_users FOREIGN KEY (user_id) REFERENCES auth.t_users (id);
ALTER TABLE public.t_uploads ADD CONSTRAINT fk_uploads_sites FOREIGN KEY (site_id) REFERENCES public.t_sites (id);
ALTER TABLE public.t_uploads OWNER TO checker;
INSERT INTO public.t_uploads (file_name) VALUES ('manual uploading');

CREATE TABLE public.t_fetches
(
  id						INTEGER PRIMARY KEY DEFAULT nextval('public.seq_fetches'),
  user_id					SMALLINT NULL,
  tube_id					SMALLINT NULL,
  fetch_aim                 SMALLINT NOT NULL,
  fetch_datetime			TIMESTAMP DEFAULT NOW(),
  fetched_galleries    	    INTEGER NULL,
  status				    VARCHAR(25) NULL,
  error_message				VARCHAR(2048) NULL
);
ALTER TABLE public.t_fetches ADD CONSTRAINT fk_fetches_users FOREIGN KEY (user_id) REFERENCES auth.t_users (id);
ALTER TABLE public.t_fetches ADD CONSTRAINT fk_fetches_tubes FOREIGN KEY (tube_id) REFERENCES public.t_tubes (id);
ALTER TABLE public.t_fetches OWNER TO checker;

CREATE TABLE public.t_fetch_invocations
(
  fetch_id					INTEGER NOT NULL,
  invocation_number			SMALLINT NOT NULL,
  url					    VARCHAR(1020) NULL,
  fetched_galleries    	    INTEGER NULL,
  status				    VARCHAR(25) NULL,
  error_message				VARCHAR(2048) NULL,
  PRIMARY KEY (fetch_id, invocation_number)
);
ALTER TABLE public.t_fetch_invocations ADD CONSTRAINT fk_fetch_invocations_fetches FOREIGN KEY (fetch_id) REFERENCES public.t_fetches (id);
ALTER TABLE public.t_fetch_invocations OWNER TO checker;

CREATE TABLE public.t_checks
(
  id						INTEGER PRIMARY KEY DEFAULT nextval('public.seq_checks'),
  user_id					SMALLINT NULL,
  site_id					SMALLINT NULL,
  check_aim                 SMALLINT NOT NULL,
  checked_galleries			INTEGER NULL,
  check_datetime			TIMESTAMP DEFAULT NOW(),
  status				    VARCHAR(25) NULL,
  error_message				VARCHAR(2048) NULL
);
ALTER TABLE public.t_checks ADD CONSTRAINT fk_checks_users FOREIGN KEY (user_id) REFERENCES auth.t_users (id);
ALTER TABLE public.t_checks ADD CONSTRAINT fk_checks_sites FOREIGN KEY (site_id) REFERENCES public.t_sites (id);
ALTER TABLE public.t_checks OWNER TO checker;

CREATE TABLE public.t_tube_galleries
(
  id						INTEGER PRIMARY KEY DEFAULT nextval('public.seq_tube_galleries'),
  fetch_id					INTEGER NOT NULL,
  tube_id					SMALLINT NOT NULL,
  external_id		  		VARCHAR(255) NULL,
  url						VARCHAR(1020) NOT NULL,
  thumb_url					VARCHAR(1020),
  embed_code				VARCHAR(2048) NULL,
  video_url				    VARCHAR(1020) NULL,
  duration					INTEGER NULL,
  description				VARCHAR(1020) NULL,
  tags						VARCHAR(1020) NULL,
  model				    	VARCHAR(255) NULL,
  active					BOOLEAN DEFAULT TRUE NOT NULL,
  video_date 				DATE NULL,
  change_datetime			TIMESTAMP DEFAULT NOW() NOT NULL
);
ALTER TABLE public.t_tube_galleries ADD CONSTRAINT fk_tube_galleries_fetches FOREIGN KEY (fetch_id) REFERENCES public.t_fetches (id);
ALTER TABLE public.t_tube_galleries ADD CONSTRAINT fk_tube_galleries_tubes FOREIGN KEY (tube_id) REFERENCES public.t_tubes (id);
ALTER TABLE public.t_tube_galleries OWNER TO checker;

CREATE TABLE public.t_stored_galleries
(
  id						INTEGER PRIMARY KEY DEFAULT nextval('public.seq_stored_galleries'),
  upload_id					INTEGER NOT NULL,
  site_id					SMALLINT NOT NULL,
  tube_id					SMALLINT NULL,
  url						VARCHAR(1020) NOT NULL,
  gallery_datetime			TIMESTAMP NULL,
  thumb_url					VARCHAR(1020),
  embed_code				VARCHAR(2048) NULL,
  embed_url				VARCHAR(1020) NULL,
  video_url				    VARCHAR(1020) NULL,
  checked					BOOLEAN DEFAULT FALSE NOT NULL,
  available					BOOLEAN DEFAULT TRUE NOT NULL,
  status_code	 		    INTEGER NULL,
  status_message			VARCHAR(2048) NULL,
  error	 		   			BOOLEAN DEFAULT FALSE NOT NULL,
  error_message				VARCHAR(2048) NULL,
  check_datetime			TIMESTAMP NULL
);
ALTER TABLE public.t_stored_galleries ADD CONSTRAINT fk_stored_galleries_uploads FOREIGN KEY (upload_id) REFERENCES public.t_uploads (id);
ALTER TABLE public.t_stored_galleries ADD CONSTRAINT fk_stored_galleries_sites FOREIGN KEY (site_id) REFERENCES public.t_sites (id);
ALTER TABLE public.t_stored_galleries ADD CONSTRAINT fk_stored_galleries_tubes FOREIGN KEY (tube_id) REFERENCES public.t_tubes (id);
ALTER TABLE public.t_stored_galleries OWNER TO checker;