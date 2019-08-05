# HOWTO

```bash
sudo -u postgres psql
```
```sql
create database aim;
create database “aim-test”;
create user aimuser with encrypted password 'mypass';
grant all privileges on database aim to aimuser;
grant all privileges on database “aim-test” to aimuser;
\c aim
```

Now paste the content of `create_words.sql` and do the same with `\c "aim-test"`
