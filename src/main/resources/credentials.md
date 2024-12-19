classDiagram
direction BT
class credentials {
   bigint unsigned user_id
   varchar(255) achievement_name
   text description
   enum('personal', 'local', 'national', 'international') category
   date date_achieved
   text notes
   int credential_id
}
class daily_routines {
   varchar(255) routine
   int id
}
class job_experience {
   bigint unsigned user_id
   varchar(255) company_name
   varchar(255) job_title
   date start_date
   date end_date
   text description
   varchar(20) job_type
   int id
}
class user_daily_routines {
   tinyint(1) completed
   bigint unsigned user_id
   int routine_id
   date date
}
class user_details {
   varchar(100) full_name
   date birthday
   varchar(15) contact_number
   varchar(100) email
   int age
   float height
   float weight
   varchar(15) gender
   varchar(50) profession
   bigint unsigned user_id
}
class users {
   varchar(50) username
   varchar(100) password
   int exp
   bigint unsigned user_id
}

credentials  -->  users : user_id
job_experience  -->  users : user_id
user_daily_routines  -->  daily_routines : routine_id:id
user_daily_routines  -->  users : user_id
user_details  -->  users : user_id
