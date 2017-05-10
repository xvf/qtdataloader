GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root';

CREATE DATABASE IF NOT EXISTS qt;
DROP TABLE IF EXISTS qt.clinic_data;
CREATE TABLE qt.clinic_data (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  name_1 varchar(200) DEFAULT NULL,
  name_2 varchar(200) DEFAULT NULL,
  street_address varchar(200) DEFAULT NULL,
  zip varchar(50) DEFAULT NULL,
  city varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UniqueClinic (name_1,name_2,street_address)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO qt.clinic_data (name_1, name_2, city,zip, street_address )
VALUES
  ('Visiting Nurse Serve of New York','Visiting Nurse Bronx ACT Program','BRONX','10451','349 E. 149th Street'),
  ('Visiting Nurse Service of New York','VNS Children and Adolescent MH Clinic at FRIENDS','BRONX','10455','470 Jackson Avenue'),
  ('Fedcap Rehabilitation Services, Inc.','The Chelton Loft','NEW YORK','10035','104-106 East 126th Street'),
  ('Goddard-Riverside Community Center','Homeless Assertive Community Treatment','NEW YORK','10025','965 Columbus Avenue'),
  ('BOOM!Health','Harm Reduction Program','BRONX','10451','226 East 144th Street'),
  ('Washington Heights Corner Project','Harm Reduction Program','NEW YORK','10033','566 West 181st Street'),
  ('Allied Service Center NYC','Queens Opioid Treatment Clinic Otp 3','NEW YORK','10032','2036 Amsterdam Avenue'),
  ('Beth Israel Medical Center','Ny Center Addiction Treatment Op 1','BROOKLYN','11215','25 12th Street'),
  ('New York Center Addiction Treatment','Bleuler Cd Recovery Services Op','QUEENS','11372','37-20 74Th Street'),
  ('Bleuler Psychotherapy Center, Inc.','Bleuler Cd Recovery Services Op','QUEENS','11375','104-70 Queens Boulevard'),
  ('Samuel Field YM & YWHA, Inc.','Community Advisory Program for the Elderly','QUEENS','11375','67-09 108th Street'),
  ('Samuel Field YM & YWHA, Inc.','Community Advisory Program for the Elderly','QUEENS','11360','208-11 26th Avenue');


CREATE DATABASE IF NOT EXISTS master_data;
DROP TABLE IF EXISTS master_data.NYC_MASTER_CLINIC_DATA;
CREATE TABLE master_data.NYC_MASTER_CLINIC_DATA (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  name_1 varchar(200) DEFAULT NULL,
  name_2 varchar(200) DEFAULT 'no-name',
  city varchar(50) DEFAULT NULL,
  latitude varchar(100) DEFAULT NULL,
  longitude varchar(100) DEFAULT NULL,
  zip varchar(50) DEFAULT NULL,
  street_address varchar(100) DEFAULT NULL,
  phone varchar(50) DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  hash_id varchar(100) DEFAULT NULL,
  active tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY UniqueClinic (name_1,name_2,zip),
  UNIQUE KEY hash_unq (hash_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;