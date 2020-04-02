CREATE DATABASE petstore;
\c petstore;

DROP TABLE pets;
CREATE TABLE pets (
  id character varying COLLATE pg_catalog."default" NOT NULL,
  name VARCHAR NOT NULL
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.pets
    OWNER to postgres;

DROP TABLE IF EXISTS masterfiles;
CREATE TABLE masterfiles (
  id character varying COLLATE pg_catalog."default" NOT NULL,
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  modelid bigint NOT NULL,
  parent character varying COLLATE pg_catalog."default" NOT NULL
)WITH (OIDS = FALSE)TABLESPACE pg_default;

ALTER TABLE public.masterfiles OWNER to postgres;

DROP TABLE article;
CREATE TABLE public.article
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    description character varying COLLATE pg_catalog."default" NOT NULL,
    modelid bigint NOT NULL,
    price DECIMAL (10,2),
    parent character varying COLLATE pg_catalog."default" NOT NULL,
	stocked boolean,
    CONSTRAINT article_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE)TABLESPACE pg_default;
DROP TABLE IF EXISTS supplier;
create table if not exists customer
(
	id varchar not null,
	name varchar not null,
	modelid integer not null,
	accountid varchar not null,
	street varchar not null,
	city varchar not null,
	state varchar not null,
	zip varchar not null,
    CONSTRAINT customer_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE)TABLESPACE pg_default;

DROP TABLE IF EXISTS supplier;
create table if not exists supplier
(
    id varchar not null constraint supplier_pkey primary key,
    name varchar not null,
    modelid integer not null,
    accountid varchar not null,
    street varchar not null,
    city varchar not null,
    state varchar not null,
    zip varchar not null
)WITH (OIDS = FALSE)TABLESPACE pg_default;
DROP TABLE IF EXISTS company;
create table company
(
    id varchar(50) not null constraint company_pkey primary key,
    name varchar(255) not null,
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    bankaccountid varchar(255) not null,
    purchasingclearingaccountid varchar(255) not null,
    salesclearingaccountid varchar(255) not null,
    paymentclearingaccountid varchar(255) not null,
    settlementclearingaccountid varchar(255) not null,
    taxcode varchar(255) not null,
    vatcode varchar(255) not null,
    currency varchar(50) not null,
    IBAN varchar(50) not null,
    CIF varchar(50) not null,
    balanceSheet varchar(50) not null,
    incomesStatement varchar(50) not null,
    cashAccountId varchar(255) not null,
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    classCash varchar(50) not null,
    classBank varchar(50) not null,
    ModelId int not null,
    pageHeaderText varchar(350),
    pageFooterText varchar(350),
    headerText varchar(350),
    footerText varchar(350),
    logoContent varchar(3500),
    logoName varchar(50),
    contentType varchar(50),
    partner varchar(250),
    tel varchar(50),
    fax varchar(50),
    email varchar(50),
    timeZone varchar(150)
)WITH (OIDS = FALSE)TABLESPACE pg_default;

DROP TABLE IF EXISTS costcenter;
create table costcenter
(
    id varchar(50) not null constraint costcenter_pkey primary key,
    name varchar(255) not null,
    description varchar(255),
    account varchar(50) not null,
    company varchar(50) not null,
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modelId int not null
)WITH (OIDS = FALSE)TABLESPACE pg_default;

DROP TABLE IF EXISTS currency;
create table currency
(
    id varchar(10) not null,
    name varchar(10) not null,
    company varchar(50) not null,
    description varchar(255),
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;
DROP TABLE IF EXISTS bank;
create table bank
(
    id varchar(50) not null constraint bank_pkey primary key,
    name varchar(250) not null,
    description varchar(255),
    company varchar(50) not null,
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS bankaccount;
create table bankaccount
(
    iban varchar(50) not null,
    owner varchar(255) not null,
    bic varchar(50) not null,
    account varchar(50),
    company varchar(50) not null
)WITH ( OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS bankstatement;
create table bankstatement
(
    id bigserial NOT NULL PRIMARY KEY,
    auftragskonto varchar(50),
    buchungstag DATE,
    valutadatum DATE,
    buchungstext varchar(300),
    verwendungszweck varchar(350),
    beguenstigterZahlungspflichtiger varchar(250),
    kontonummer varchar(50),
    blz varchar(50),
    betrag money,
    waehrung varchar(200),
    info varchar(250),
    company varchar(50),
    companyiban varchar(50),
    isValidated bit,
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

create table bankstatement2
(
    id bigserial NOT NULL PRIMARY KEY,
    auftragskonto varchar(50),
    buchungstag varchar(50),
    valutadatum varchar(50),
    buchungstext varchar(300),
    verwendungszweck varchar(350),
    beguenstigterZahlungspflichtiger varchar(250),
    kontonummer varchar(50),
    blz varchar(50),
    betrag money,
    waehrung varchar(200),
    info varchar(250),
    company varchar(50),
    companyiban varchar(50),
    isValidated bit,
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS account;
create table account
(
    id varchar(50) not null  primary key,
    name varchar(255) not null,
    description varchar(255),
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    enter_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    modelId int not null default 9,
    parent_id varchar(50),
    isDebit BOOLEAN default TRUE not null,
    balanceSheet BOOLEAN default FALSE not null

)WITH (OIDS = FALSE) TABLESPACE pg_default
DROP TABLE IF EXISTS account2;
create table account2
(
    id varchar(50) not null  primary key,
    name varchar(255) not null,
    description varchar(255),
    posting_date varchar(50) not null,
    modified_date varchar(50) not null,
    enter_date varchar(50) not null,
    company varchar(50) not null,
    modelId int not null default 9,
    parent_id varchar(50),
    isDebit BOOLEAN default TRUE not null,
    balanceSheet BOOLEAN default FALSE not null

)WITH (OIDS = FALSE) TABLESPACE pg_default

DROP TABLE IF EXISTS asset;
create table asset
(
    id varchar(50) not null constraint asset_pkey primary key,
    name varchar(50),
    description varchar(255),
    scrapValue money,
    currency varchar(50) not null,
    lifeSpan int,
    depreciation int,
    account varchar(50),
    oAccount varchar(50),
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    modelId int not null,
    Rate decimal(5,2),
    frequency int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS details_compta;
create table detailcompta
(
    id  bigserial NOT NULL PRIMARY KEY,
    transid int not null,
    account varchar(50) not null,
    side bit not null,
    oaccount varchar(50) not null,
    amount money not null,
    duedate DATE NOT NULL DEFAULT CURRENT_DATE,
    text varchar(380),
    currency varchar(10) not null,
    modelId int not null,
    Terms varchar(250),
    Balanced bit default 0 not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;
DROP TABLE IF EXISTS mastercompta;
create table mastercompta
(
    id  bigserial NOT NULL PRIMARY KEY,
    oid int not null,
    costcenter varchar(50) not null,
    account varchar(50) not null,
    HeaderText varchar(380),
    transdate DATE NOT NULL DEFAULT CURRENT_DATE,
    itemdate DATE NOT NULL DEFAULT CURRENT_DATE,
    enterdate DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    FileName varchar(255),
    FileContent bytea,
    ContentType varchar(50),
    IsValidated bit,
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;
DROP TABLE IF EXISTS store;
create table store
(
    id varchar(50) not null constraint store_pkey  primary key,
    name varchar(50) not null,
    street varchar(50),
    city varchar(50),
    state varchar(50),
    zip varchar(50),
    account varchar(50) not null,
    company varchar(50) not null,
    enter_date DATE NOT NULL DEFAULT CURRENT_DATE,
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    modelId int not null,
    ModelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS vat;
create table vat
(
    id varchar(50) not null constraint vat_pkey primary key,
    name varchar(255) not null,
    description varchar(255),
    vat decimal(3,2) not null,
    inputvataccountid varchar(255) not null,
    outputvataccountid varchar(255) not null,
    revenueaccountid varchar(255) not null,
    company varchar(50) not null,
    company varchar(50) not null,
    modelId int not null,
    ModelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS quantityunit;
create table quantityunit
(
    id varchar(50) not null constraint quantityunit_pkey primary key,
    name varchar(50) not null,
    description varchar(255),
    company varchar(50) not null,
    company varchar(50) not null,
    modelId int not null,
    ModelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS periodic_account_balance;
create table periodic_account_balance
(
    id varchar(255) not null,
    name varchar(255) not null,
    account varchar(50) not null,
    periode varchar(6) not null,
    debit decimal(18,2) default 0 not null,
    credit decimal(18,2) default 0 not null,
    company varchar(50) not null,
    currency varchar(10) not null,
    modelid int default 0 not null,
    idebit decimal(18,2) default 0,
    icredit decimal(18,2) default 0,
    fdebit decimal(18,2) default 0,
    fcredit decimal(18,2) default 0
)WITH (OIDS = FALSE) TABLESPACE pg_default;
DROP TABLE IF EXISTS journal;
create table journal(
    id  bigserial NOT NULL PRIMARY KEY,
    transid int not null,
    oid int not null,
    transaction_type varchar(50),
    cust_supplier varchar(50) not null,
    store varchar(50) not null,
    transdate DATE not null,
    postingdate DATE NOT NULL ,
    enterdate DATE not null,
    periode nchar(6) not null,
    account varchar(50) not null,
    oaccount varchar(50) not null,
    amount money not null,
    side varchar(1) not null,
    company varchar(50) not null,
    company_iban varchar(50) not null,
    iban varchar(50) not null,
    currency varchar(10) not null,
    Info varchar(255),
    storeName varchar(150),
    account_name varchar(150),
    oaccount_name varchar(150),
    cust_supplier_name varchar(150),
    company_name varchar(150),
    type_journal varchar(50),
    costcenter varchar(50),
    costcenter_name varchar(150),
    type_journal_name varchar(150),
    modelId int,
    idebit  money default 0 not null,
    icredit money default 0 not null,
    fdebit decimal(18,2) default 0,
    fcredit decimal(18,2) default 0
)WITH (OIDS = FALSE) TABLESPACE pg_default;

ALTER TABLE article OWNER to postgres;
ALTER TABLE asset OWNER to postgres;
ALTER TABLE account OWNER to postgres;
ALTER TABLE customer OWNER to postgres;
ALTER TABLE supplier OWNER to postgres;
ALTER TABLE company OWNER to postgres;
ALTER TABLE currency OWNER to postgres;
ALTER TABLE costcenter OWNER to postgres;
ALTER TABLE store OWNER to postgres;
ALTER TABLE bank OWNER to postgres;
ALTER TABLE bankaccount OWNER to postgres;
ALTER TABLE bankstatement OWNER to postgres;
ALTER TABLE mastercompta OWNER to postgres;
ALTER TABLE details_compta OWNER to postgres;
ALTER TABLE quantityunit OWNER to postgres;
ALTER TABLE vat OWNER to postgres;
ALTER TABLE  store OWNER to postgres;
ALTER TABLE periodic_account_balance OWNER to postgres;
ALTER TABLE journal OWNER to postgres;
DROP SEQUENCE IF EXISTS mastercompta_seq;
CREATE SEQUENCE mastercompta_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

DROP SEQUENCE IF EXISTS detailcompta_seq;
CREATE SEQUENCE detailcompta_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
DROP SEQUENCE IF EXISTS journal_seq;
CREATE SEQUENCE journal_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

INSERT INTO pets (id, name) VALUES ('1', 'Bailey');
INSERT INTO pets (id, name) VALUES ('2','Bella');
INSERT INTO pets (id, name) VALUES ('3', 'Max');
select * from pets;

INSERT INTO masterfiles (name, description) VALUES ('IWS', 'IWS',  0, '0');
INSERT INTO masterfiles (name, description) VALUES ('Masterfiles', 'Masterfiles', 0, '0');
INSERT INTO masterfiles (name, description) VALUES ('Accounting', 'Accounting', 0, '0');
INSERT INTO masterfiles (name, description) VALUES ('SCM', 'Supply Chain Management',  0, '0');
INSERT INTO masterfiles (name, description) VALUES ('Sales', 'Customer Relationship Management',  0, '0');

INSERT INTO article (id, name, description, modelId, price, parent, stocked) VALUES ('1', 'IWS','Integrated Warehouse Management System',8, 100000, '0', false);
INSERT INTO article (id, name, description, modelId, price, parent, stocked) VALUES ('2', 'Masterfiles', 'Masterfiles', 8, 10000, '1',false);
INSERT INTO article (id, name, description, modelId,  price, parent, stocked) VALUES ('3', 'Purchasinhg', 'Purchasinhg',8,20000, '1',false);
INSERT INTO article (id, name, description, modelId,  price, parent, stocked) VALUES ('4', 'Inventory', 'Inventory',8, 40000,'1',false);
INSERT INTO article (id, name, description, modelId,  price, parent, stocked) VALUES ('5', 'Financials', 'Financials',8, 50000,'1',false);
INSERT INTO article (id, name, description, modelId,  price, parent, stocked) VALUES ('6', 'AI', 'AI',8, 120000, '1',false);


select * from masterfiles
    select * from  supplier


DROP TABLE IF EXISTS supplier;
create table supplier
(
    id varchar(50) not null primary key,
    name varchar(255) not null,
    description varchar(255) not null,
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    phone varchar(50),
    email varchar(50),
    account varchar(50),
    iban varchar(50),
    vatCode varchar(50),
    charge_account varchar(50),
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    enter_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    modelId int  default 5  not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS supplier2;
create table supplier2
(
    id varchar(50) not null primary key,
    name varchar(255) not null,
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    phone varchar(50),
    email varchar(50),
    account varchar(50),
    iban varchar(50),
    vatCode varchar(50),
    charge_account varchar(50),
    posting_date varchar(50) not null,
    modified_date varchar(50) not null,
    enter_date varchar(50) not null,
    company varchar(50) not null,
    modelId int  default 5  not null
) WITH (OIDS = FALSE) TABLESPACE pg_default;


DROP TABLE IF EXISTS customer;
create table customer
(
    id varchar(50) not null primary key,
    name varchar(255) not null,
    description varchar(255) not null,
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    phone varchar(50),
    email varchar(50),
    account varchar(50),
    iban varchar(50),
    vatCode varchar(50),
    revenue_account varchar(50),
    posting_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE NOT NULL DEFAULT CURRENT_DATE,
    enter_date DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    modelId int  default 3  not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS customer2;
create table customer2
(
    id varchar(50) not null primary key,
    name varchar(255) not null,
    street varchar(255),
    city varchar(255),
    state varchar(255),
    zip varchar(255),
    phone varchar(50),
    email varchar(50),
    account varchar(50),
    iban varchar(50),
    vatCode varchar(50),
    charge_account varchar(50),
    posting_date varchar(50) not null,
    modified_date varchar(50) not null,
    enter_date varchar(50) not null,
    company varchar(50) not null,
    modelId int  default 3  not null
) WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS details_compta;
create table DetailCompta
(
    id int not null primary key,
    transid int not null,
    account varchar(50) not null,
    side bit not null,
    oaccount varchar(50) not null,
    amount money not null,
    duedate DATE NOT NULL DEFAULT CURRENT_DATE,
    text varchar(380),
    currency varchar(10) not null,
    modelId int not null,
    terms varchar(250),
    posted BOOLEAN default FALSE
) WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS details_compta;
create table DetailCompta
(
    id int not null primary key,
    transid int not null,
    account varchar(50) not null,
    side BOOLEAN  not null,
    oaccount varchar(50) not null,
    amount money not null,
    duedate DATE NOT NULL DEFAULT CURRENT_DATE,
    text varchar(380),
    currency varchar(10) not null,
    modelId int not null,
    terms varchar(250),
    posted BOOLEAN default FALSE
) WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS DetailCompta2;
create table DetailCompta2
(
    id int not null primary key,
    transid int not null,
    account varchar(50) not null,
    side BOOLEAN not null,
    oaccount varchar(50) not null,
    amount money not null,
    duedate varchar(50) not null,
    text varchar(380),
    currency varchar(10) not null,
    modelId int not null,
    terms varchar(250),
    posted BOOLEAN default FALSE
) WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS file_content;
create table file_content
(
    id int not null primary key,
    transid int not null,
    fileName varchar(255),
    fileContent varchar(3500),
    contentType varchar(50)
)WITH (OIDS = FALSE) TABLESPACE pg_default;

DROP TABLE IF EXISTS master_compta;
create table master_compta
(
    id int  not null primary key,
    oid int not null,
    costcenter varchar(50) not null,
    account varchar(50) not null,
    headerText varchar(380),
    transdate DATE NOT NULL DEFAULT CURRENT_DATE,
    postingdate DATE NOT NULL DEFAULT CURRENT_DATE,
    enterdate DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    file_content int default -1,
    posted BOOLEAN default FALSE,
    typeJournal varchar(50),
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;

create table master_compta2
(
    id int  not null primary key,
    oid int not null,
    costcenter varchar(50) not null,
    account varchar(50) not null,
    headerText varchar(380),
    transdate DATE NOT NULL DEFAULT CURRENT_DATE,
    postingdate DATE NOT NULL DEFAULT CURRENT_DATE,
    enterdate DATE NOT NULL DEFAULT CURRENT_DATE,
    company varchar(50) not null,
    fileName varchar(255),
    fileContent bytea,
    contentType varchar(50),
    posted BOOLEAN default FALSE,
    typeJournal varchar(50),
    modelId int not null
)WITH (OIDS = FALSE) TABLESPACE pg_default;